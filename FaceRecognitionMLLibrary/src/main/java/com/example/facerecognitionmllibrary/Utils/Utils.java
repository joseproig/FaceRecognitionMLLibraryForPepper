package com.example.facerecognitionmllibrary.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Double> convertFloatArrayToList (double [] array) {
        List<Double> result = new ArrayList<>();
        for (double f : array) {
            result.add(f);
        }
        return result;
    }
    public static double[] convertListToFloatArray (List<Double> array) {
        double[] arr = new double[array.size()];
        int index = 0;
        for (int j = 0; j < array.size();j++) {
            double d = 0;
            if (array.get(j) instanceof Number) {
                d = ((Number) array.get(j)).doubleValue();
            }
            arr[index++] = d;
            Log.i("go",""+j);
        }
        return arr;
    }

    public static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
    public static double distance(double[] a, double[] b) {
        double diff_square_sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            diff_square_sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(diff_square_sum);
    }


    public static double cosineSimilarity(double [] x1  , double [] x2)  {
        double dotProduct = 0.0;
        double mag1 = 0.0;
        double mag2 = 0.0;
        double sum = 0.0;
        for (int i = 0; i < x1.length;i++) {
            dotProduct += (x1[i] * x2[i]);
            mag1 += Math.pow(x1[i],2.0);
            mag2 += Math.pow(x2[i],2.0);
            sum +=  Math.pow((x1[i] - x2[i]),2.0);
        }
        mag1 = Math.sqrt(mag1);
        mag2 = Math.sqrt(mag2);
        return dotProduct / (mag1 * mag2);
    }

}
