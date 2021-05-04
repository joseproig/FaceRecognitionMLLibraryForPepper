package com.example.facerecognitionmllibrary.Basics;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;


import com.example.facerecognitionmllibrary.CommunicationWithFirebase.DatabaseControl;
import com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors.CustomEditorFaceRecognition;
import com.example.facerecognitionmllibrary.CommunicationWithFirebase.FirebaseListener;
import com.example.facerecognitionmllibrary.Model.User;
import com.example.facerecognitionmllibrary.Utils.Utils;

import java.util.List;
import java.util.Map;

public class FaceComparator implements FirebaseListener {
    private Activity activity;
    private List<double[]> lastEmbeddings;
    private FaceDetectorJ faceDetectorJToAdvise;
    private List<Bitmap> detectedFaces;
    private DatabaseControl databaseControl;


    public FaceComparator(String linkToDatabase) {
        this.databaseControl = new DatabaseControl(linkToDatabase,this,new CustomEditorFaceRecognition());
    }

    public void doComparation (FaceDetectorJ faceDetectorJToAdvise, List<Bitmap> detectedFaces, List<double []> embeddingsToRecognize) {

        //Aqui només comparem al pepe, però s'hauria de crear una bbdd amb firebase amb els diferents embeddings de les cares
        this.faceDetectorJToAdvise =  faceDetectorJToAdvise;
        //float [] embeddingCaraReconeguda = {-0.13924907f,-0.38680297f,-0.18566543f,-0.46416354f,0.34038663f,0.030944243f,0.077360585f,-0.23208179f,0.046416357f,0.21660967f,0.18566541f,-0.26302603f,0.10830484f,-0.37133086f,-0.015472114f,0.38680297f,0.2939703f,-0.37133086f,0.2939703f,-0.5415242f,0.12377696f,0.0928327f,-0.29397026f,-0.015472114f,0.077360585f,-0.12377695f,0.5415242f,0.32491452f,-0.17019331f,0.0f,-0.17019331f,0.32491452f,0.13924907f,-0.092832714f,0.49510783f,-0.44869143f,0.13924907f,0.046416357f,0.5415242f,0.030944243f,0.27849817f,0.0f,-0.17019331f,-0.15472119f,-0.35585874f,-0.52605206f,-0.030944243f,-0.12377695f,0.4641636f,0.2475539f,-0.4796357f,0.35585874f,0.13924907f,-0.44869143f,0.077360585f,0.21660967f,0.63435686f,0.18566541f,-0.27849814f,-0.9747435f,-0.4332193f,-0.27849814f,-0.13924907f,0.80455023f,-0.15472119f,0.52605206f,0.38680297f,-0.046416357f,0.21660967f,-0.4796357f,-0.0773606f,-0.4951078f,0.27849817f,-0.44869143f,-0.12377695f,-0.18566543f,-0.030944243f,0.2475539f,-0.29397026f,-0.38680297f,-0.5879405f,-0.15472119f,0.23208179f,0.1701933f,0.37133086f,0.13924907f,0.35585874f,-0.092832714f,-0.6034126f,0.80455023f,0.27849817f,0.06188847f,-0.030944243f,0.0928327f,0.66530114f,0.10830484f,-0.030944243f,-0.29397026f,-0.78907806f,-0.015472114f,-0.27849814f,0.5879405f,0.18566541f,-0.29397026f,0.06188847f,0.18566541f,-0.015472114f,0.12377696f,-0.18566543f,-0.34038663f,0.12377696f,0.23208179f,0.3094424f,0.49510783f,-0.0773606f,0.13924907f,0.2475539f,0.35585874f,0.23208179f,-0.015472114f,-0.15472119f,0.15472119f,0.34038663f,0.06188847f,0.35585874f,-0.52605206f,0.20113756f,0.077360585f};
        lastEmbeddings = embeddingsToRecognize;
        databaseControl.readUsers();
        this.detectedFaces = detectedFaces;
    }

    public void differentFacesObtained (Map<String, Object> entries) {
        PossibleResult bestResult = null;
        String keyOfBestResult = null;
        if (entries != null) {
            for (Map.Entry<String, Object> entry : entries.entrySet()) {
                Map<String, Object> data = (Map<String, Object>)entry.getValue();
                User user = new User((String)data.get("name"),(String)data.get("surname"), (List<List<Double>>)data.get("embedding"));

                for (double[] embeddingCaraReconeguda : user.getEmbeddingResult()) {
                    for (double[] lastEmbedding: lastEmbeddings) {
                        double distance = Utils.cosineSimilarity(lastEmbedding, embeddingCaraReconeguda);
                        //double distance = vector_dot(embeddingCaraReconeguda, lastEmbedding) / (vector_norm(embeddingCaraReconeguda) * vector_norm(lastEmbedding));
                        Log.i("FaceComparator", "La distancia amb " + user.getName() + " es " + distance);
                        if (distance > 0.70) {
                            if (bestResult == null) {
                                bestResult = new PossibleResult(user.getName(), user.getSurname(), distance);
                                keyOfBestResult = entry.getKey();
                            } else {
                                if (bestResult.getDistance() < distance) {
                                    bestResult = new PossibleResult(user.getName(), user.getSurname(), distance);
                                    keyOfBestResult = entry.getKey();
                                }
                            }
                        }
                    }
                }
            }
        }
        faceDetectorJToAdvise.recognitionDone(bestResult,lastEmbeddings,keyOfBestResult,detectedFaces);
    }

    @Override
    public void readFinished(Map<String, Object> objectReaded) {

    }
}
