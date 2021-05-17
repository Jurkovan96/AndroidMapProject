package com.example.rent.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.adapter.ReservationAdapter;
import com.example.rent.databinding.ActivityTerrainDetailsBinding;
import com.example.rent.model.Reservation;
import com.example.rent.model.Terrain;
import com.example.rent.viewmodel.ReservationViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class TerrainDetailsActivity extends AppCompatActivity implements ReservationAdapter.DeleteItem {

    private ActivityTerrainDetailsBinding mActivityTerrainDetailsBinding;

    private ReservationAdapter mReservationAdapter;

    private ReservationViewModel mReservationViewModel;

    private List<Reservation> mReservationList;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTerrainDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_terrain_details);
        mReservationList = new ArrayList<>();
        mReservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
        FirebaseAuth firebaseAuth = FireHelper.getInstanceOfAuth();
        mUserId = firebaseAuth.getCurrentUser().getUid();
        Intent intent = this.getIntent();
        if (intent.hasExtra("TERRAIN_ITEM")) {
            Terrain terrain = (Terrain) intent.getSerializableExtra("TERRAIN_ITEM");
            mReservationViewModel
                    .getAllReservationsByTerrainId(terrain.getId())
                    .observe(this, reservations -> {
                        mReservationList.clear();
                        mReservationList.addAll(reservations);
                        initRecyclerView(mReservationList);
                        terrain.setReservations(mReservationList);
                        initPageComponents(terrain);
                    });
        }

    }

    private void initPageComponents(Terrain terrain) {
        mActivityTerrainDetailsBinding.terrainName.setText(terrain.getName());
        mActivityTerrainDetailsBinding.terrainDescription.setText(terrain.getDescription());
        mActivityTerrainDetailsBinding.terrainHours.setText(terrain.getHours());
        mActivityTerrainDetailsBinding.terrainPrice.setText(String.valueOf(terrain.getPrice()).concat("  ron/hour"));
        mActivityTerrainDetailsBinding.viewReservations.setOnClickListener(v -> {
            Intent intent = new Intent(this, TerrainReservationActivity.class);
            intent.putExtra("TERRAIN_OBJECT", terrain);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void initRecyclerView(List<Reservation> mReservationList) {
        if (mReservationList.size() == 0) {
            changeLayout();
        } else {
            mReservationAdapter = new ReservationAdapter(mReservationList, mUserId, this);
            mActivityTerrainDetailsBinding.reservationHolder.setHasFixedSize(true);
            mActivityTerrainDetailsBinding.reservationHolder.setLayoutManager(new LinearLayoutManager(this));
            mActivityTerrainDetailsBinding.reservationHolder.setAdapter(mReservationAdapter);
        }
    }

    private void changeLayout() {
        mActivityTerrainDetailsBinding.reservationHolder.setVisibility(View.GONE);
        mActivityTerrainDetailsBinding.noReservations.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteReservationItem(Reservation reservation) {
        mReservationList.remove(reservation);
        mReservationViewModel.deleteReservation(reservation);
        mReservationAdapter.notifyDataSetChanged();
    }
}