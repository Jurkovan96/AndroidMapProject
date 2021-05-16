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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Terrain implements Serializable {

    @Exclude
    private String id;

    private String name;

    private String terrainType;

    private String imagePath;

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
    @BindingAdapter("loadUserProfilePicture")
    public static void loadUserProfilePicture(
            ImageView imageView, String imagePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (imagePath == null || imagePath.equals(" ")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.no_image_available)
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

