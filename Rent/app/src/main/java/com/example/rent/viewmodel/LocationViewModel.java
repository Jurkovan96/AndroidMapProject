package com.example.rent.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rent.model.Location;
import com.example.rent.repository.LocationRepository;

import java.util.Set;


public class LocationViewModel extends ViewModel {

    private LocationRepository locationRepository;

    public LocationViewModel() {
        this.locationRepository = new LocationRepository();
    }


    public MutableLiveData<Set<Location>> getLocations() {
        return locationRepository.getAllLocations();
    }

    public void addLocation(Location location) {
        locationRepository.addNewLocation(location);
    }

    public void deleteLocation(Location location) {
        locationRepository.deleteLocation(location);
    }
}
