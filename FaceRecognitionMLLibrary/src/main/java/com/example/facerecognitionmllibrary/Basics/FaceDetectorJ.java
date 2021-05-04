package com.example.facerecognitionmllibrary.Basics;


import android.graphics.Bitmap;

import java.util.List;

public abstract class FaceDetectorJ {
   protected FaceNetRecognition faceRecognizer;
   public abstract void recognitionDone(PossibleResult possibleResult, List<double[]> embeddingsOfFace, String keyOfBestResult, List<Bitmap> facesRecognized);
   public abstract void detectFace(Bitmap imagePepper,Boolean takeMorePictures);
   public abstract void subscribe (ControllerListener controllerListener);
   public abstract void adviseSubscribersOfResult (String string);
   public abstract void unsubscribe (ControllerListener controllerListener);
   public abstract void unsubscribeAll ();
   public abstract void closeModels ();
}
