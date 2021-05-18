package com.example.rent.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Reservation implements Serializable {

    @Exclude
    private String id;

    private String date;

    private String startHour;

    private String endHour;

    private String terrainId;

    private String userId;

    private String terrainName;


    public String getTerrainId() {
        return terrainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setTerrainId(String terrainId) {
        this.terrainId = terrainId;
    }

    public Reservation(String date, String startHour, String endHour, String terrainId, String userId,
                       String terrainName) {
        this.date = date;
        this.terrainName = terrainName;
        this.startHour = startHour;
        this.endHour = endHour;
        this.terrainId = terrainId;
        this.userId = userId;
    }

    public String getTerrainName() {
        return terrainName;
    }

    public void setTerrainName(String terrainName) {
        this.terrainName = terrainName;
    }

    public Reservation() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
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
