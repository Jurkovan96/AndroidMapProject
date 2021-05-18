package com.example.rent.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.rent.R;
import com.example.rent.adapter.TerrainAdapter;
import com.example.rent.databinding.ActivityCustomBinding;
import com.example.rent.model.Terrain;
import com.example.rent.viewmodel.LocationViewModel;
import com.example.rent.viewmodel.TerrainViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.rent.ConstantsUtils.LOCATION_ID;

public class TerrainActivity extends AppCompatActivity {

    private ActivityCustomBinding mActivityCustomBinding;

    private LocationViewModel mLocationViewModel;

    private TerrainViewModel mTerrainViewModel;

    private List<Terrain> mTerrainList;

    private TerrainAdapter terrainAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCustomBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom);
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        mTerrainViewModel = new ViewModelProvider(this).get(TerrainViewModel.class);
        mTerrainList = new ArrayList<>();
        Intent intent = this.getIntent();
        if (intent.hasExtra(LOCATION_ID)) {
            mLocationViewModel
                    .getLocationById(intent.getStringExtra(LOCATION_ID))
                    .observe(this, location -> {
                        mTerrainViewModel.getTerrainsByLocationId(location.getId())
                                .observe(this, terrains -> {
                                    mTerrainList.clear();
                                    mTerrainList.addAll(terrains);
                                    location.setTerrains(terrains);
                                    initRecyclerView(mTerrainList);
                                });
                    });
        }

    }

    private void initRecyclerView(List<Terrain> terrains) {
        if (terrains.size() == 0) {
            changeLayout();
        } else {
            final int GRID_COLUMN_NUMBER = 2;
            terrainAdapter = new TerrainAdapter(getApplicationContext(), mTerrainList);
            mActivityCustomBinding.terrainRecyclerView.setHasFixedSize(true);
            mActivityCustomBinding.terrainRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_COLUMN_NUMBER));
            mActivityCustomBinding.terrainRecyclerView.setAdapter(terrainAdapter);
        }
    }

    private void changeLayout() {
        mActivityCustomBinding.terrainRecyclerView.setVisibility(View.GONE);
        mActivityCustomBinding.noVendorsFoundImageView.setVisibility(View.VISIBLE);
    }


//    private void showDetails(Location location) {
//        mActivityCustomBinding.locationName.setText(location.getName());
//        if (location.getTerrains() != null) {
//            Log.d(TAG, "Location Terrains List : " + location.getTerrains().size());
//        } else {
//            Log.d(TAG, "Check again for null!" + location.getTerrains());
//        }
//    }


}