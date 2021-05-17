package com.example.rent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rent.model.Terrain;
import com.example.rent.repository.TerrainRepository;

import java.util.List;

public class TerrainViewModel extends ViewModel {

    private final TerrainRepository terrainRepository;

    public TerrainViewModel() {
        this.terrainRepository = new TerrainRepository();
    }

    public MutableLiveData<List<Terrain>> getTerrainsByLocationId(String locationId) {
        return terrainRepository.getTerrainsByLocationId(locationId);
    }

//    public MutableLiveData<List<Terrain>> getTerrainsByLocationId2(String locationId) {
//        return terrainRepository.getTerrainsByLocationId2(locationId);
//    }

}
