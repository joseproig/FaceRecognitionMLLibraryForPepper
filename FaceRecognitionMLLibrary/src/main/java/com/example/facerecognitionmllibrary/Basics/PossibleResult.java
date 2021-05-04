package com.example.facerecognitionmllibrary.Basics;

public class PossibleResult {
    private String name;
    private String surname;
    private double  distance;


    public PossibleResult(String name, String surname, double distance) {
        this.name = name;
        this.surname = surname;
        this.distance = distance;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
