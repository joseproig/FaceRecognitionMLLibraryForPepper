package com.example.facerecognitionmllibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.aldebaran.qi.sdk.QiContext;
import com.example.facerecognitionmllibrary.Basics.ControllerListener;
import com.example.facerecognitionmllibrary.Basics.FaceComparator;
import com.example.facerecognitionmllibrary.Basics.FaceDetectionML;
import com.example.facerecognitionmllibrary.Basics.FaceDetectorJ;
import com.example.facerecognitionmllibrary.Basics.FaceNetRecognition;

import java.io.IOException;

public class DefaultRecognitionML {
    Context context;
    ControllerListener controllerListener;
    private FaceDetectorJ faceDetectionML;

    public DefaultRecognitionML(QiContext context, AssetManager assetManager, ControllerListener controllerListener,String linkToDatabase) {
        this.context = context;
        this.controllerListener = controllerListener;
        FaceNetRecognition faceRecognizer = null;
        try {
            faceRecognizer = new FaceNetRecognition(assetManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        faceDetectionML = new FaceDetectionML(faceRecognizer,new FaceComparator(linkToDatabase));

        faceDetectionML.subscribe(controllerListener);
    }


    public void detectFaces (Bitmap faceObtained) {
        faceDetectionML.detectFace(faceObtained,true);
    }

    public void close () {
        faceDetectionML.closeModels();
        faceDetectionML.unsubscribeAll();
    }

}
