package com.example.rent.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.rent.R;
import com.example.rent.databinding.ActivityCustomBinding;
import com.example.rent.model.Location;
import com.example.rent.model.Terrain;
import com.example.rent.viewmodel.LocationViewModel;
import com.example.rent.viewmodel.TerrainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    public static final String TAG = ReservationActivity.class.getName();

    private ActivityCustomBinding mActivityCustomBinding;

    private LocationViewModel mLocationViewModel;

    private TerrainViewModel mTerrainViewModel;

    private List<Terrain> mTerrainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCustomBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom);
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        mTerrainViewModel = new ViewModelProvider(this).get(TerrainViewModel.class);
        mTerrainList = new ArrayList<>();

        Intent intent = this.getIntent();
        if (intent.hasExtra("CURRENT_ID")) {
            mLocationViewModel
                    .getLocationById(intent.getStringExtra("CURRENT_ID"))
                    .observe(this, location -> {

                        mTerrainViewModel.getTerrainsByLocationId(location.getId())
                                .observe(this, terrains -> {
                                    mTerrainList.clear();
                                    mTerrainList.addAll(terrains);
                                    location.setTerrains(terrains);
                                    showDetails(location);
                                });
                    });
        }
    }

    private void showTerrain(List<Terrain> terrains) {
        if (terrains.size() > 0) {
            Log.d(TAG, "The size of the terrains " + terrains.size());
        } else {
            Log.d(TAG, "Check again for null!");
        }
    }


    private void showDetails(Location location) {
        mActivityCustomBinding.locationName.setText(location.getName());
        if (location.getTerrains() != null) {
            Log.d(TAG, "Location Terrains List : " + location.getTerrains().size());
        } else {
            Log.d(TAG, "Check again for null!" + location.getTerrains());
        }
    }


}