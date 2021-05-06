package com.example.rent.model;

import com.google.firebase.firestore.Exclude;

public class Reservation {

    @Exclude
    private int id;

    private String date;

    private String startHour;

    private String endHour;

    public Reservation(String date, String startHour, String endHour) {
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Exclude
    public int getId() {
        return id;
    }

    @Exclude
    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }
}
