package com.example.rent.model;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.example.rent.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Location implements Serializable {
    @Exclude
    private String id;

    private String name;

    private String description;

    private String review;

    @Exclude
    private Set<Reservation> reservations;

    private List<String> imageList;

    private double latitude;

    private double longitude;

    private List<Terrain> terrains = new ArrayList<>();

    public Location(String name, String description, String review, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.review = review;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageList = new ArrayList<>();
        this.terrains = new ArrayList<>();
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<Terrain> terrains) {
        this.terrains = terrains;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Exclude
    public Set<Reservation> getReservations() {
        return reservations;
    }

    @Exclude
    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    @BindingAdapter("locationPictures")
    public static void loadImage(final ImageView view, List<String> imageList) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (imageList == null || imageList.size() == 0) {
            Picasso.get()
                    .load(R.drawable.no_image_available)
                    .into(view);
        } else {
            storageRef.child(imageList.get(0)).getDownloadUrl().addOnSuccessListener(uri ->
                    Picasso.get()
                            .load(uri).error(R.drawable.no_image_available)
                            .into(view)).addOnFailureListener(exception -> Picasso.get()
                    .load(R.drawable.no_image_available)
                    .into(view));
        }
    }


}
