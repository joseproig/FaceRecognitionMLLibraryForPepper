package com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors;

import android.util.Log;

import com.example.facerecognitionmllibrary.Model.User;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class CustomEditorFaceRecognition implements EditorOfFirebase{
    public static final int USEREMBEDDING = 0;
    @Override
    public Transaction.Result update(int numOfUpdate, MutableData mutableData, Object objectToUpdate) {
        switch (numOfUpdate) {
            case 0:
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    Log.i("updateNewEmbedding:", "transaction could not be done");
                    return Transaction.success(mutableData);
                }

                u.getEmbeddingResult().add((double[])objectToUpdate);

                // Set value and report transaction success
                mutableData.setValue(u);
                return Transaction.success(mutableData);
        }
        return Transaction.success(mutableData);
    }
}
