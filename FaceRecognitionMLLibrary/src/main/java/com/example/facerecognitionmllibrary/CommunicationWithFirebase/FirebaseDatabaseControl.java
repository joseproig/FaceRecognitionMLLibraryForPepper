package com.example.facerecognitionmllibrary.CommunicationWithFirebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.facerecognitionmllibrary.Model.MappingObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.example.facerecognitionmllibrary.CommunicationWithFirebase.Editors.EditorOfFirebase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseControl {
    private DatabaseReference mDatabase;
    private FirebaseListener firebaseListener;
    private EditorOfFirebase editorOfFirebase;


    public FirebaseDatabaseControl (String linkToDatabase, FirebaseListener firebaseListener, EditorOfFirebase editorOfFirebase) {
        mDatabase = FirebaseDatabase.getInstance(linkToDatabase).getReference();
        this.firebaseListener = firebaseListener;
        this.editorOfFirebase = editorOfFirebase;
    }



    public void readChild(List<String> childs) {
        if (childs != null && childs.size() != 0) {
            DatabaseReference databaseReference = null;
            for (String child: childs){
                if (databaseReference != null) {
                    databaseReference = databaseReference.child(child);
                } else {
                    databaseReference = mDatabase.child(child);
                }
            }

            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());

                    } else {
                        if (task.getResult() != null) {
                            firebaseListener.readFinished((Map<String, Object>) task.getResult().getValue());
                        } else {
                            firebaseListener.readFinished(null);
                        }
                    }
                }
            });
        }
    }

    public boolean writeNewInfoWithoutKey(List<String> childs, MappingObject objectToPut) {
        if (childs != null && childs.size() != 0) {
            DatabaseReference databaseReference = null;
            StringBuilder string = new StringBuilder("/");
            for (String child: childs) {
                if (databaseReference != null) {
                    databaseReference = databaseReference.child(child);
                } else {
                    databaseReference = mDatabase.child(child);
                }
                string.append(child);
                string.append("/");
            }

            String key = databaseReference.push().getKey();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(string + key, objectToPut.toMap());
            mDatabase.updateChildren(childUpdates);
            return true;
        }
        return false;
    }

    public boolean updateNewEmbedding (List<String> childs,Object object,int typeOfUpdate) {
        if (childs != null && childs.size() != 0) {
            DatabaseReference databaseReference = null;
            StringBuilder string = new StringBuilder("/");
            for (String child : childs) {
                if (databaseReference != null) {
                    databaseReference = databaseReference.child(child);
                } else {
                    databaseReference = mDatabase.child(child);
                }
                string.append(child);
                string.append("/");
            }
            databaseReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    return editorOfFirebase.update(typeOfUpdate,mutableData,object);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    Log.d("updateNewEmbedding", "postTransaction:onComplete:" + databaseError);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public EditorOfFirebase getEditorOfFirebase() {
        return editorOfFirebase;
    }
}
