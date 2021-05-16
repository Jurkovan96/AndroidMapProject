package com.example.rent.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rent.FireHelper;
import com.example.rent.model.Location;
import com.example.rent.model.Terrain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LocationRepository {

    public static final String TAG = LocationRepository.class.getName();

    private final String LOCATION_COLLECTION = "Locations";

    public static final String TERRAIN_COLLECTION = "Terrains";

    private final FirebaseFirestore mFirebaseFirestore;

    public LocationRepository() {
        this.mFirebaseFirestore = FireHelper.getInstanceOfFirebase();
    }


    public MutableLiveData<Set<Location>> getAllLocations() {
        MutableLiveData<Set<Location>> listMutableLiveData = new MutableLiveData<>();
        mFirebaseFirestore.collection(LOCATION_COLLECTION).addSnapshotListener((value, error) -> {
            Set<Location> locations = new HashSet<Location>();
            if (value != null) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Location location = documentSnapshot.toObject(Location.class);
                    if (location != null) {
                        location.setId(documentSnapshot.getId());
                        locations.add(location);
                    }
                }
            }
            listMutableLiveData.setValue(locations);
        });
        return listMutableLiveData;
    }

    public void addNewLocation(Location location) {
        mFirebaseFirestore.collection(LOCATION_COLLECTION).add(location);
    }

    public void deleteLocation(Location location) {
        mFirebaseFirestore.collection(LOCATION_COLLECTION).document(location.getId()).delete();
    }

    public MutableLiveData<Location> getLocationById(String location_id) {
        MutableLiveData<Location> requestLocation = new MutableLiveData<>();
        mFirebaseFirestore
                .collection(LOCATION_COLLECTION)
                .document(location_id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null) {
                        Location location = documentSnapshot.toObject(Location.class);
                        assert location != null;
                        location.setId(documentSnapshot.getId());
                        requestLocation.setValue(location);
                    }
                });
        return requestLocation;
    }

    public MutableLiveData<Set<String>> getLocationObjectName() {
        MutableLiveData<Set<String>> requestLocation = new MutableLiveData<>();
        mFirebaseFirestore
                .collection(LOCATION_COLLECTION)
                .addSnapshotListener((value, error) -> {
                    Set<String> locations = new HashSet<String>();
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Location location = documentSnapshot.toObject(Location.class);
                            if (location != null && !location.getName().isEmpty()) {
                                locations.add(location.getName());
                            }
                        }
                    }
                    requestLocation.setValue(locations);
                });
        return requestLocation;
    }
}
