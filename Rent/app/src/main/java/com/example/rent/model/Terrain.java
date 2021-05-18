package com.example.rent.model;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rent.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Terrain implements Serializable {

    @Exclude
    private String id;

    private String name;

    private String terrainType;

    private String imagePath;

    private String description;

    private String hours;

    private int price;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private String contactNumber;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Exclude
    private List<Reservation> reservations;

    @Exclude
    public List<Reservation> getReservations() {
        return reservations;
    }

    @Exclude
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Terrain() {
    }

    public Terrain(String name, String terrainType) {
        this.name = name;
        this.terrainType = terrainType;
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

    public String getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(String terrainType) {
        this.terrainType = terrainType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @SuppressLint("RestrictedApi")
    @BindingAdapter("terrainImage")
    public static void loadUserProfilePicture(
            ImageView imageView, String imagePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (imagePath == null || imagePath.equals(" ")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.ic_no_photo)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.5f)
                    .fitCenter()
                    .into(imageView);

        } else {
            storage.getReference().child(imagePath).getDownloadUrl().addOnSuccessListener(uri ->
                    Glide.with(imageView.getRootView().getContext())
                            .load(uri)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.5f)
                            .fitCenter()
                            .into(imageView));
        }
    }
}

