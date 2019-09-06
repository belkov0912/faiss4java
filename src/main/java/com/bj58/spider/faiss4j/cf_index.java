package com.bj58.spider.faiss4j;

import com.bj58.spider.faiss.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;

import static com.bj58.spider.faiss4j.IndexHelper.*;

public class cf_index {
    private static final Logger logger = LoggerFactory.getLogger(cf_index.class);
    private static final boolean debug_mode = true;

    static {
//        System.loadLibrary("faiss");
//        logger.info("load libfaiss success");
        System.loadLibrary("swigfaiss4j");
        logger.info("load libswigfaiss4j success");
    }

    private static float[][] randomData3d(int size) {
        float[][] data = new float[size * 3][3];
        float half = size / 2.0f;
        Random rand = new Random();
        for (int i = 0, j = data.length; i < j; i++) {
            float[] row = new float[]{rand.nextFloat() * size, rand.nextFloat() * size, rand.nextFloat() * size};
            data[i] = row;
        }
        return data;
    }

    private static float[][] fvecs_read(String fileName) {
        File file = new File(fileName);
        int max_lines;
        if (debug_mode) {
            max_lines = 100000;
        } else {
            max_lines = 943139;
        }

        BufferedReader reader = null;
        int row = 0, column = 0;
        ArrayList<float[]> vecs = new ArrayList<>(max_lines);
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
                if (row >= max_lines)
                    break;
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

    public static void IndexIVFFlat(float[][] data, boolean use_imi) {
        try {
            int d = data[0].length;
            int row_nums = data.length;

            int nhash;
            long ncentroids;
            Index quantizer;
            if (use_imi) {
                nhash = 2;
                long nbits_subq = (int)(Math.log(row_nums+1)/Math.log(2))/2;      // good choice in general
                ncentroids = 1 << (nhash * nbits_subq);        // total # of centroids == nlist
//                MultiIndexQuantizer
                quantizer = new MultiIndexQuantizer(d, nhash, nbits_subq);
                logger.info(String.format("IMI (%d,%d): %d virtual centroids (target: %d base vectors)", nhash, nbits_subq, ncentroids, row_nums));

            } else {
//                IndexFlatL2
                quantizer = new IndexFlatL2(d);
                ncentroids = 4096;
                logger.info(String.format("IF: %d virtual centroids (target: %d base vectors)", ncentroids, row_nums));
            }

            IndexIVFFlat index = new IndexIVFFlat(quantizer, d, ncentroids, MetricType.METRIC_L2);

            logger.info(String.format("index is trained: %b", index.getIs_trained()));

            if (use_imi) {
                index.setQuantizer_trains_alone('1');
            }

            // define the number of probes. 2048 is for high-dim, overkilled in practice
            // Use 4-1024 depending on the trade-off speed accuracy that you want
            index.setNprobe(2048);
//            index.setNprobe(65536);

            long t0 = System.currentTimeMillis();
            floatArray xb = makeFloatArray(data);
//            logger.info("Vectors:\n{}", show(tb, trainData.length, dimension));
            index.train(data.length, xb.cast());
            long t1 = System.currentTimeMillis();
            logger.info(String.format("[%.3f s] Training the index", (t1-t0)*1e-6));

            index.add(row_nums, xb.cast());
            long t2 = System.currentTimeMillis();
            logger.info(String.format("[%.3f s] Adding the vectors to the index", (t2-t1)*1e-6));

            // remember a few elements from the database as queries
            int i0 = 1234;
            int i1 = 1244;

            int nq = i1 - i0;
            float[][] queryConds = Arrays.copyOfRange(data, i0, i1);

            long t3 = System.currentTimeMillis();
            int k = 5;
            logger.info(String.format("[%d ms] Searching the %d nearest neighbors of %d vectors in the index", t3 - t0, k, nq));
            floatArray query = makeFloatArray(queryConds);
            longlongArray labels = new longlongArray(k * nq);
            floatArray distances = new floatArray(k * nq);

            long t4 = System.currentTimeMillis();
            index.search(queryConds.length, query.cast(), k, distances.cast(), labels.cast());

            long t5 = System.currentTimeMillis();
            logger.info(String.format("[%dms] Query results (vector ids, then distances):", t5 - t4));


//            logger.info("Vectors:\n{}", show(xb, nq, d));
//            logger.info("Query:\n{}", show(query, queryConds.length, queryConds[0].length));
            logger.info("Distances:\n{}", show(distances, nq, k));
            logger.info("Labels:\n{}", show(labels, nq, k));
        } catch (Exception e) {
            logger.error("failed", e);
        }
    }

    public static void main(String argv[]) {

        String file_path = "/Users/jiananliu/work/AISEALs/src/faiss_index/data/part-00000";

        float[][] data = fvecs_read(file_path);
        logger.info(String.format("train vecs row:[%d] columns:[%d]", data.length, data[0].length));

        IndexIVFFlat(data, true);
    }
}
