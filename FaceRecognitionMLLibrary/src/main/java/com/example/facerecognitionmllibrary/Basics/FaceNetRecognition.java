package com.example.facerecognitionmllibrary.Basics;


import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;


import com.example.facerecognitionmllibrary.Utils.Utils;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FaceNetRecognition {


        private static final String MODEL_PATH = "facenet.tflite";

        private static final int IMAGE_HEIGHT = 160;
        private static final int IMAGE_WIDTH = 160;
        private static final int NUM_CANALS = 3;
        private static final int NUM_BYTES_PER_CANAL = 4;


        private final int[] intValues = new int[IMAGE_HEIGHT * IMAGE_WIDTH];
        private ByteBuffer imgData;

        private MappedByteBuffer tfliteModel;
        private Interpreter tflite;
        private final Interpreter.Options tfliteOptions = new Interpreter.Options();
        private Bitmap lastCropped;

        public FaceNetRecognition(AssetManager assetManager) throws IOException{
            tfliteModel = loadModelFile(assetManager);
            tflite = new Interpreter(tfliteModel, tfliteOptions);
            imgData = ByteBuffer.allocateDirect(IMAGE_HEIGHT * IMAGE_WIDTH * NUM_CANALS * NUM_BYTES_PER_CANAL);
            imgData.order(ByteOrder.nativeOrder());
        }

        private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException{
            AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }

        private void convertBitmapToByteBuffer(Bitmap bitmap) {
            if (imgData == null) {
                return;
            }
            imgData.rewind();
            bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            // Convert the image to floating point.
            int pixel = 0;
            for (int i = 0; i < IMAGE_HEIGHT; ++i) {
                for (int j = 0; j < IMAGE_WIDTH; ++j) {
                    final int val = intValues[pixel++];
                    imgData.putFloat(((val >> 16) & 0xFF) / 255.0f);
                    imgData.putFloat(((val >> 8) & 0xFF) / 255.0f);
                    imgData.putFloat((val & 0xFF) / 255.0f);
                }
            }
        }



        private Bitmap resizedBitmap(Bitmap bitmap,int width, int height){
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        private Bitmap croppedBitmap(Bitmap bitmap, int left, int top,int width, int height){
            if ( (left + width) > bitmap.getWidth() ){
                width = bitmap.getWidth() - left;
            }
            if ( (top + height) > bitmap.getHeight() ){
                height = bitmap.getHeight() - top;
            }

            return Bitmap.createBitmap(bitmap, left, top, width, height);
        }

        public double[] doEmbedding(Bitmap bitmap,Rect rect){
            lastCropped = croppedBitmap(bitmap,rect.left,rect.top,rect.width(),rect.height());
            Bitmap resizedbitmap = resizedBitmap(lastCropped,IMAGE_WIDTH,IMAGE_HEIGHT);
            convertBitmapToByteBuffer(resizedbitmap);

            float[][] embeddings = new float[1][512];
            tflite.run(imgData, embeddings);

            return Utils.convertFloatsToDoubles(embeddings[0]);
        }


        public void closeModel(){
            if (tflite != null) {
                tflite.close();
                tflite = null;
            }
            tfliteModel = null;
        }

        public Bitmap getLastCropped() {
            return lastCropped;
        }




}
