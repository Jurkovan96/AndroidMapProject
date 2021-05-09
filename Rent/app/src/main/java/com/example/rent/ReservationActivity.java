package com.example.rent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.example.rent.databinding.ActivityCustomBinding;
import com.example.rent.model.Location;
import com.example.rent.viewmodel.LocationViewModel;

public class ReservationActivity extends AppCompatActivity {

    private ActivityCustomBinding mActivityCustomBinding;

    private LocationViewModel mLocationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCustomBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom);
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        Intent intent = this.getIntent();
        if (intent.hasExtra("CURRENT_ID")) {
            mLocationViewModel
                    .getLocationById(intent.getStringExtra("CURRENT_ID"))
                    .observe(this, this::showDetails);
        }
    }


    private void showDetails(Location location) {
        mActivityCustomBinding.locationName.setText(location.getName());

    }
}