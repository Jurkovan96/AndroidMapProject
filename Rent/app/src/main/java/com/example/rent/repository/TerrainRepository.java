package com.example.rent.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.rent.FireHelper;
import com.example.rent.model.Terrain;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TerrainRepository {

    public static final String TAG = LocationRepository.class.getName();

    private final String LOCATION_COLLECTION = "Locations";

    public static final String TERRAIN_COLLECTION = "Terrains";

    private final FirebaseFirestore mFirebaseFirestore;

    public TerrainRepository() {
        this.mFirebaseFirestore = FireHelper.getInstanceOfFirebase();
    }

    public MutableLiveData<List<Terrain>> getTerrainsByLocationId(String locationId) {
        MutableLiveData<List<Terrain>> terrainMutableLiveData = new MutableLiveData<>();
        mFirebaseFirestore
                .collection(LOCATION_COLLECTION)
                .document(locationId)
                .collection(TERRAIN_COLLECTION)
                .addSnapshotListener((value, error) -> {
                    List<Terrain> terrains = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Terrain terrain = documentSnapshot.toObject(Terrain.class);
                            if (terrain != null) {
                                terrain.setId(documentSnapshot.getId());
                                terrains.add(terrain);
                            }
                        }
                    }
                    terrainMutableLiveData.setValue(terrains);
                });
        return terrainMutableLiveData;
    }

    public MutableLiveData<List<Terrain>> getTerrainsByLocationId2(String locationId) {
        MutableLiveData<List<Terrain>> terrainMutableLiveData = new MutableLiveData<>();
        mFirebaseFirestore
                .collection(LOCATION_COLLECTION)
                .document(locationId)
                .collection(TERRAIN_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Terrain> terrains = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Terrain terrain = documentSnapshot.toObject(Terrain.class);
                            if (terrain != null) {
                                terrains.add(terrain);
                            }
                        }
                    }
                    terrainMutableLiveData.setValue(terrains);
                });
        return terrainMutableLiveData;
    }
}
