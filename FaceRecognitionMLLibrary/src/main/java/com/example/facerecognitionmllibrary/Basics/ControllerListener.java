package com.example.facerecognitionmllibrary.Basics;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.List;

public interface ControllerListener {
    void beginOfFaceDetection (Bitmap imageWithDetectections);
    void updateName (String string);
    void updateDetectedFace (Rect boundingBoxOfFace);
    void updateDrawAllFacesDetected ();
    void newFaceRecognized (List<double []> embeddings);
    void knownFaceRecognized(String name, double distance, List<double[]> embeddingOfFace, String keyOfBestResult, List<Bitmap> bitmaps);
    void onClickSomeButton (String buttonName);
    void onViewCreated (String fragmentname);
    void nameOfUnknownPersonDone (String name);
}
