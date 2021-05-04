package com.example.facerecognitionmllibrary.CommunicationWithFirebase;

import com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors.CustomEditorFaceRecognition;
import com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors.EditorOfFirebase;
import com.example.facerecognitionmllibrary.Model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseControl {
    protected FirebaseDatabaseControl firebaseDatabaseControl;

    public DatabaseControl(String linkToFirebase, FirebaseListener firebaseListener, EditorOfFirebase editorOfFirebase) {
        firebaseDatabaseControl = new FirebaseDatabaseControl(linkToFirebase,firebaseListener,editorOfFirebase);
    }

    protected float calculateValueOfEmbedding (float [] embedding) {
        float totalValue = 0;
        for (float value : embedding) {
            totalValue += value;
        }
        return totalValue;
    }


    public void readUsers () {
        ArrayList childs = new ArrayList();
        childs.add("users");
        firebaseDatabaseControl.readChild(childs);
    }

    public void writeNewUser(String name,String surname, List<double []> embeddings) {
        User user = new User(embeddings,name,surname);
        ArrayList childs = new ArrayList();
        childs.add("users");
        firebaseDatabaseControl.writeNewInfoWithoutKey(childs, user);
    }

    public void updateNewEmbedding (double[] newEmbedding, String keyOfBestResult) {
        ArrayList childs = new ArrayList();
        childs.add("users");
        childs.add(keyOfBestResult);
        firebaseDatabaseControl.updateNewEmbedding(childs,newEmbedding, CustomEditorFaceRecognition.USEREMBEDDING);
    }

    public FirebaseDatabaseControl getFirebaseDatabaseControl() {
        return firebaseDatabaseControl;
    }

    public void setFirebaseDatabaseControl(FirebaseDatabaseControl firebaseDatabaseControl) {
        this.firebaseDatabaseControl = firebaseDatabaseControl;
    }

    public EditorOfFirebase getEditor () {
        return this.firebaseDatabaseControl.getEditorOfFirebase();
    }
}
