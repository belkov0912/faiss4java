package com.bj58.spider.faiss4j;

import com.bj58.spider.faiss.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Arrays;

import static com.bj58.spider.faiss4j.IndexHelper.*;

public class FaissIndex {
    private static final Logger logger = LoggerFactory.getLogger(FaissIndex.class);
    private static final boolean debug_mode = false;

    static {
        try {
            System.loadLibrary("swigfaiss4j");
        } catch (Exception ex) {
            throw new java.lang.RuntimeException("please make sure libs:[faiss, swigfaiss4j] in java.library.path");
        }

        logger.info("load libswigfaiss4j success");
    }

    private Index index;
    private floatArray xb;
    private int d;
    private int nb;
    private float[][] tmp_vec = null;

    private float[][] fvecs_read(String fileName, int min, int max) {
        File file = new File(fileName);
        int max_line;
        if (debug_mode) max_line = 100000; else max_line = 943139;
        if (max == -1)
            max = max_line;

        BufferedReader reader = null;
        int row = 0, column = 0;
        ArrayList<float[]> vecs = new ArrayList<>(max);
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                int last_index = line.lastIndexOf(':');
//                String id = line.substring(0, last_index);
                String values = line.substring(last_index + 1);
                String[] sp = values.split(",");
                if (column == 0) column = sp.length;
                if (sp.length != column) {
                    logger.warn(String.format("column:[%d] != real_columns:[%d] row:[%d] line:[%s]", column, sp.length, row, line));
                    continue;
                }

                float[] vs = IndexHelper.toFloatArray(sp);
                vecs.add(vs);
                row += 1;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    logger.error("close reader failed");
                }
            }
        }

        return vecs.toArray(new float[row][column]);
    }

    public void set_train_data(String file_name) {
        float[][] data = fvecs_read(file_name, 0, -1);
        this.xb = makeFloatArray(data);
        this.nb = data.length;
        this.d = data[0].length;
        this.tmp_vec = data;
        logger.info(String.format("train vecs row:[%d] columns:[%d]", nb, d));
    }

    public void printMatrixStats() {
        MatrixStats matrixStats = new MatrixStats(nb, d, xb.cast());
        logger.info("-----------------------------------");
        logger.info(matrixStats.getComments());
        logger.info("-----------------------------------");
    }

    public void create_index(boolean use_imi, int nprob) {
        try {
            int nhash;
            long ncentroids;
            Index quantizer;
            if (use_imi) {
                nhash = 2;
                long nbits_subq = (int)(Math.log(nb+1)/Math.log(2))/2;      // good choice in general
                ncentroids = 1 << (nhash * nbits_subq);        // total # of centroids == nlist
//                MultiIndexQuantizer
                quantizer = new MultiIndexQuantizer(d, nhash, nbits_subq);
                logger.info(String.format("IMI (%d,%d): %d virtual centroids (target: %d base vectors)", nhash, nbits_subq, ncentroids, nb));

            } else {
//                IndexFlatL2
                quantizer = new IndexFlatL2(d);
                ncentroids = 4096;
                logger.info(String.format("IF: %d virtual centroids (target: %d base vectors)", ncentroids, nb));
            }

            IndexIVFFlat index = new IndexIVFFlat(quantizer, d, ncentroids, MetricType.METRIC_L2);

            logger.info(String.format("index is trained: %b", index.getIs_trained()));

            if (use_imi) {
                index.setQuantizer_trains_alone('1');
            }

            // define the number of probes. 2048 is for high-dim, overkilled in practice
            // Use 4-1024 depending on the trade-off speed accuracy that you want
            index.setNprobe(nprob);
            logger.info(String.format("set nprob:%d", nprob));

            long t0 = System.currentTimeMillis();
//            logger.info("Vectors:\n{}", show(tb, trainData.length, dimension));
            index.train(nb, xb.cast());
            long t1 = System.currentTimeMillis();
            logger.info(String.format("[%d ms] Training the index", t1-t0));

            index.add(nb, xb.cast());
            long t2 = System.currentTimeMillis();
            logger.info(String.format("[%d ms] Adding the vectors to the index", t2-t1));

            this.index = index;
        } catch (Exception e) {
            logger.error("failed", e);
        }
    }

    public void search(float[][] query) {
        int k = 5;
        int nq = query.length;
        floatArray q = makeFloatArray(query);
        longlongArray labels = new longlongArray(k * nq);
        floatArray distances = new floatArray(k * nq);

        long t4 = System.currentTimeMillis();
        index.search(query.length, q.cast(), k, distances.cast(), labels.cast());

        long t5 = System.currentTimeMillis();
        logger.info(String.format("[%dms] Searching the %d nearest neighbors of %d vectors in the index", t5 - t4, k, nq));
        logger.info("Query results (vector ids, then distances):");

        logger.info("Distances:\n{}", show(distances, nq, k));
        logger.info("Labels:\n{}", show(labels, nq, k));
    }

    public static void main(String[] argv) {

        String file_name = "part-00000";
        int nprob = 512;
        if (argv.length >= 2) {
            file_name = argv[0];
            nprob = Integer.parseInt(argv[1]);
        }
        logger.info("read file:" + file_name);

        FaissIndex index = new FaissIndex();

        index.set_train_data(file_name);

        index.printMatrixStats();

        index.create_index(true, nprob);

        {
            // remember a few elements from the database as queries
            int i0 = 1234;
            int i1 = 1245;

            float[][] query = Arrays.copyOfRange(index.tmp_vec, i0, i1);

            index.search(query);
        }
    }
}
