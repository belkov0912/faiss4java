package com.bj58.spider.faiss4j;

import com.bj58.spider.faiss.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexHelper {
    private static final Logger log = LoggerFactory.getLogger(IndexHelper.class);

    public static String show(longlongArray a, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(i).append('\t').append('|');
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%5d ", a.getitem(i * cols + j)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String show(longArray a, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(i).append('\t').append('|');
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%5d ", a.getitem(i * cols + j)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String show(floatArray a, int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append(i).append('\t').append('|');
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%7g ", a.getitem(i * cols + j)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static floatArray makeFloatArray(float[][] vectors) {
        int d = vectors[0].length;
        int nb = vectors.length;
        floatArray fa = new floatArray(d * nb);
        for (int i = 0; i < nb; i++) {
            for (int j = 0; j < d; j++) {
                fa.setitem(d * i + j, vectors[i][j]);
            }
        }
        return fa;
    }

    public static doubleArray makeDoubleArray(Double[][] vectors) {
        int d = vectors[0].length;
        int nb = vectors.length;
        doubleArray fa = new doubleArray(d * nb);
        for (int i = 0; i < nb; i++) {
            for (int j = 0; j < d; j++) {
                fa.setitem(d * i + j, vectors[i][j]);
            }
        }
        return fa;
    }

    public static longArray makeLongArray(int[] ints) {
        int len = ints.length;
        longArray la = new longArray(len);
        for (int i = 0; i < len; i++) {
            la.setitem(i, ints[i]);
        }
        return la;
    }

    public static long[] toArray(longArray c_array, int length) {
        return toArray(c_array, 0, length);
    }

    public static long[] toArray(longArray c_array, int start, int length) {
        long[] re = new long[length];
        for (int i = start; i < length; i++) {
            re[i] = c_array.getitem(i);
        }
        return re;
    }

    public static int[] toArray(intArray c_array, int length) {
        return toArray(c_array, 0, length);
    }

    public static int[] toArray(intArray c_array, int start, int length) {
        int[] re = new int[length];
        for (int i = start; i < length; i++) {
            re[i] = c_array.getitem(i);
        }
        return re;
    }

    public static float[] toArray(floatArray c_array, int length) {
        return toArray(c_array, 0, length);
    }

    public static float[] toArray(floatArray c_array, int start, int length) {
        float[] re = new float[length];
        for (int i = start; i < length; i++) {
            re[i] = c_array.getitem(i);
        }
        return re;
    }

    public static float[] toFloatArray(double[] arr) {
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float)arr[i];
        }
        return ret;
    }

    public static float[] toFloatArray(String[] arr) {
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = Float.parseFloat(arr[i]);
        }
        return ret;
    }
}
