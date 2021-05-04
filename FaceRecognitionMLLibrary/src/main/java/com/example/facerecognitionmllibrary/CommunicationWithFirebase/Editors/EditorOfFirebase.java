package com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors;

import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public interface EditorOfFirebase {
    public Transaction.Result update(int numOfUpdate, MutableData mutableData, Object objectToUpdate);
}
