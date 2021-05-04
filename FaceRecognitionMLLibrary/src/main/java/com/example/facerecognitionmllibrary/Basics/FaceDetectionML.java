package com.example.facerecognitionmllibrary.Basics;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;


import com.example.facerecognitionmllibrary.Utils.PictureListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectionML extends FaceDetectorJ implements PictureListener {
    private com.google.mlkit.vision.face.FaceDetector detector;
    private FaceComparator faceComparator;
    private List<ControllerListener> subscribers;
    private boolean isFaceRecognized;
    private final int PICTURES_TO_TAKE =  2;
    private List <Bitmap> facesDetected;
    private List <double[]>embeddingsDetected;
    private int picturesTaken;
    private int numFacesDetec;

    public FaceDetectionML(FaceNetRecognition faceRecognizer, FaceComparator faceComparator) {
        /**
         * FAST,NO_LANDMARKS,NO_CONTOURS,NO_CLASSIFICATIONS,100X100 minim tamny de la cara, deshabilitat el seguiment de cara
         */
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().build();
        subscribers = new ArrayList<>();
        detector = com.google.mlkit.vision.face.FaceDetection.getClient(options);
        this.faceRecognizer = faceRecognizer;
        this.faceComparator = faceComparator;
        clearData();
    }

    private void clearData () {
        isFaceRecognized = false;
        picturesTaken = 0;
        numFacesDetec = 0;
        facesDetected = new ArrayList<>();
        embeddingsDetected = new ArrayList<>();
    }

    public void recognitionDone(PossibleResult bestResult,List<double[]> embeddingsOfFace,String keyOfBestResult, List<Bitmap> facesRecognized) {
        if (bestResult != null) {
            adviseSubscribersOfResult(bestResult.getName());
            for (ControllerListener controllerListener: subscribers) {
                controllerListener.knownFaceRecognized(bestResult.getName(),bestResult.getDistance(),embeddingsOfFace,keyOfBestResult,facesRecognized);
            }
            //Només actualitzem la base de dades en cas que la distancia sigui menor de 0.9, sinó no val la pena guardar-se més embeddings

        } else {
            for (ControllerListener controllerListener: subscribers) {
                controllerListener.newFaceRecognized(embeddingsOfFace);
            }
        }
    }

    public void detectFace(Bitmap imagePepper,Boolean takeMorePictures) {
        Log.i("Listener", "Value: " + imagePepper.getPixel(210,210));
        //0 es el Rotation Degree
        InputImage image = InputImage.fromBitmap(imagePepper,0);

        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                controlOfDetectedFaces(imagePepper, faces,takeMorePictures);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Listener", "Not face Detected");
                            }
                        });

    }

    private void controlOfDetectedFaces (Bitmap imagePepper, List<Face> faces,Boolean takeMorePictures) {
        List <Rect>  lastDetectedFaces;
        lastDetectedFaces = new ArrayList<>();

        if (!faces.isEmpty()) {
            //Solo avisamos que empezamos el reconocimiento en la primera foto
            if(takeMorePictures) {
                for (ControllerListener controllerListener : subscribers) {
                    controllerListener.beginOfFaceDetection(imagePepper);
                }
            }

            Log.i("Listener", "Algo Detected");


            for (Face face : faces) {
                //Solo printamos el rectangulo en el caso de la primera foto que sacamos
                if(takeMorePictures) {
                    //Dibuixem el rectangle en la cara detectada
                    for (ControllerListener controllerListener : subscribers) {
                        controllerListener.updateDetectedFace(face.getBoundingBox());
                    }
                }
                //Array con las caras croopped
                lastDetectedFaces.add(face.getBoundingBox());
                Log.i("Listener", "Face Detected " + Float.toString(face.getBoundingBox().bottom - face.getBoundingBox().top) + " x " + Float.toString(face.getBoundingBox().right - face.getBoundingBox().left));
            }
            //Solo printamos la cara en el caso de la primera foto que sacamos
            if(takeMorePictures) {
                //Enviamos a la View que "Oye, tudas las faces ya estan detectadas, ya puedes printar tudo lo que has detcetado"
                for (ControllerListener controllerListener : subscribers) {
                    controllerListener.updateDrawAllFacesDetected();
                }
            }
            if (!lastDetectedFaces.isEmpty()) {

                //TODO: Control de una o més cares
                double[] embedding = faceRecognizer.doEmbedding(imagePepper,lastDetectedFaces.get(0));
                Bitmap croppedbitmap = faceRecognizer.getLastCropped();

                //Si no quieres hacer el log del embedding se puede quitar hasta el comentario que pone FIN
                int i = 0;
                StringBuilder string = new StringBuilder();
                string.append("{");
                while (i < embedding.length) {
                    string.append(embedding[i]);
                    string.append(",");
                    i++;
                }
                string.append("}");
                //FIN

                Log.i("FaceDecteted", string.toString());
                facesDetected.add(croppedbitmap);
                embeddingsDetected.add(embedding);
                numFacesDetec++;
                faceComparator.doComparation(this, facesDetected, embeddingsDetected);
            }
        } else {
            //ContextOfPepper.getContext(null,null,null).getQiContext().;
            Log.i("Listener", "Nothing Detected");
            //Si la primera no la pilla no perdem més el temps
            if (takeMorePictures) {
                clearData();
                return;
            }
        }
        clearData();
       /* picturesTaken++;
        //Una vegada tenim les diferents fotos que volem passem a la proxima fase de comparar
        if (picturesTaken == PICTURES_TO_TAKE && numFacesDetec != 0) {
            clearData();
            faceComparator.doComparation(this, facesDetected, embeddingsDetected);
        } else {
            if (picturesTaken != PICTURES_TO_TAKE) {
                DoPictures.getInstance().takePicture(this);
            } else {
                clearData();
            }
        }*/
    }



    @Override
    public void subscribe(ControllerListener controllerListener) {
        subscribers.add(controllerListener);
    }

    /**
     * Avisamos a las diferentes vistas que queramos avisar que se actualizen
     * @param string Resultado que les queremos pasar
     */
    @Override
    public void adviseSubscribersOfResult(String string) {
        for (ControllerListener subscriber: subscribers) {
            subscriber.updateName(string);
        }
    }


    @Override
    public void unsubscribe(ControllerListener controllerListener) {
        int i = 0;
        for (ControllerListener subscriber: subscribers) {
            if (subscriber == controllerListener) {
                subscribers.remove(i);
            }
            i++;
        }
    }

    @Override
    public void unsubscribeAll() {
        int i = 0;
        for (ControllerListener subscriber : subscribers) {
            subscribers.remove(i);
            i++;
        }
    }

    @Override
    public void closeModels() {
        this.faceRecognizer.closeModel();
    }

    @Override
    public void pictureDone(Bitmap bitmapObtained) {
        //if (!isFaceRecognized) {
            //isFaceRecognized = true;
            this.detectFace(bitmapObtained,true);
        /*} else {
            this.detectFace(bitmapObtained,false);
        }*/
    }
}
