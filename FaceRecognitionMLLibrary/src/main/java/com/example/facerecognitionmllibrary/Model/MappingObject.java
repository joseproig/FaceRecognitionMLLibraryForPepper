package com.example.facerecognitionmllibrary.Model;


import com.example.facerecognitionmllibrary.Utils.Utils;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MappingObject {
    private String name;
    private String username;
    private String surname;
    private List<List<Double>> embedding;

    public MappingObject() {
    }


    public MappingObject(String name,String surname, List<List<Double>> embedding) {
        this.name = name;
        this.surname = surname;
        this.embedding = new ArrayList<>();
        this.embedding = embedding;
    }

    public MappingObject(List<double[]> embeddings, String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.embedding = new ArrayList<>();
        for (int i=0;i< embeddings.size();i++) {
            this.embedding.add(Utils.convertFloatArrayToList(embeddings.get(i)));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<List<Double>> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<List<Double>> embedding) {
        this.embedding = embedding;
    }

    @Exclude
    public List<double[]> getEmbeddingResult() {
        List<double[]> floats = new ArrayList<>();
        for (int u = 0; u < embedding.size();u++) {
            floats.add(Utils.convertListToFloatArray(embedding.get(u)));
        }
        return floats;
    }
    public abstract Map<String, Object> toMap();
}
