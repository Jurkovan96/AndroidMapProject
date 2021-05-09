package com.example.rent.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.rent.FireHelper;
import com.example.rent.model.Location;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class LocationRepository {

    private final String LOCATION_COLLECTION = "Locations";

    private FirebaseFirestore mFirebaseFirestore;

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
}
