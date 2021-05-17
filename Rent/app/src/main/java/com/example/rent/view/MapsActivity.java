package com.example.rent.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.adapter.SlideshowAdapter;
import com.example.rent.databinding.ActivityMapsBinding;
import com.example.rent.model.Location;
import com.example.rent.viewmodel.LocationViewModel;
import com.example.rent.viewmodel.TerrainViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener {
    public static final String TAG = MapsActivity.class.getName();

    private GoogleMap mMap;

    private Set<Location> mLocations;

    private ActivityMapsBinding mActivityMapsBinding;

    private SupportMapFragment mMapFragment;

    private LocationViewModel mLocationViewModel;

    private SlideshowAdapter mSlideshowAdapter;

    private Location mCurrentItemReference;

    private FirebaseAuth mFirebaseAuth;

    private Set<String> mLocationObjects;

    private TerrainViewModel mTerrainViewModel;


    boolean hasTennisTerrain;
    boolean hasFootballTerrain;
    boolean hasBasketBallTerrain;
    boolean hasCustomTerrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mLocations = new HashSet<>();
        mLocationObjects = new HashSet<>();
        mFirebaseAuth = FireHelper.getInstanceOfAuth();
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        mTerrainViewModel = new ViewModelProvider(this).get(TerrainViewModel.class);
        initializeCustomToolBar();
        mActivityMapsBinding.hideHolder.setOnClickListener(this);
        mActivityMapsBinding.locationDetails.setOnClickListener(this);


        mLocationViewModel.getLocations().observe(this, locations -> {
            mLocations.clear();
            locations.forEach(location -> mTerrainViewModel
                    .getTerrainsByLocationId(location.getId())
                    .observe(this, location::setTerrains)
            );
            mLocations.addAll(locations);
            addTerrainsToLocations(locations);
            mMapFragment.getMapAsync(this);
        });

        mLocationViewModel.getLocationObjectName().observe(this, strings -> {
            mLocationObjects.clear();
            mLocationObjects.addAll(strings);
        });
        // mLocationViewModel.addLocation(new Location("Location1", "Desc1", "rev1", 47.157859, 27.594512));
    }

    private void addTerrainsToLocations(Set<Location> locations) {
        locations.forEach(location -> {
            mTerrainViewModel
                    .getTerrainsByLocationId(location.getId())
                    .observe(this, location::setTerrains);
        });
    }

    private void initializeCustomToolBar() {
        mActivityMapsBinding.customToolBar.logout.setOnClickListener(v -> {
            if (mFirebaseAuth.getCurrentUser() != null) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
            }
        });
        mActivityMapsBinding.customToolBar.profile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng custom = new LatLng(47.157859, 27.594512);
        //mMap.addMarker(new MarkerOptions().position(custom).title("Custom marker").icon(getBitMapFromImage(getApplicationContext())));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        objectToMarkerConversion().forEach(markerOptions -> mMap.addMarker(markerOptions));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(point -> {
            mMap.addMarker(new MarkerOptions().position(point));
        });
    }

    private Set<MarkerOptions> objectToMarkerConversion() {
        Set<MarkerOptions> markerLocations = new HashSet<>();
        mLocations.forEach(location -> {
            markerLocations.add(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(location.getName()));
        });
        return markerLocations;
    }

    private BitmapDescriptor getBitMapFromImage(Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_place);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        hasFootballTerrain = false;
        hasBasketBallTerrain = false;
        hasTennisTerrain = false;
        hasCustomTerrain = false;

        List<String> terrainsType = new ArrayList<>();
        if (!mActivityMapsBinding.componentHolder.isShown()
                && locationIsValid(marker.getTitle())) {
            mActivityMapsBinding.componentHolder.setVisibility(View.VISIBLE);
        }

        mActivityMapsBinding.locationName.setText(marker.getTitle());
        mActivityMapsBinding.locationDescriptionTextView.setText("");
        mLocations.forEach(location -> {
            if (location.getName().equals(marker.getTitle())) {
                mCurrentItemReference = location;
                mActivityMapsBinding.locationDescriptionTextView.setText(location.getDescription());
                location.getTerrains().forEach(terrain -> {
                    terrainsType.add(terrain.getTerrainType());
                });
            }
        });
        if (terrainsType.contains("FOOTBALL")) {
            hasFootballTerrain = true;
        }
        if (terrainsType.contains("BASKETBALL")) {
            hasBasketBallTerrain = true;
        }
        if (terrainsType.contains("TENNIS")) {
            hasTennisTerrain = true;
        }
        if (terrainsType.contains("CUSTOM")) {
            hasCustomTerrain = true;
        }

        if (!hasFootballTerrain) {
            mActivityMapsBinding.locationFootballTerrain.setVisibility(View.GONE);
        } else {
            mActivityMapsBinding.locationFootballTerrain.setVisibility(View.VISIBLE);
        }
        if (!hasBasketBallTerrain) {
            mActivityMapsBinding.locationBasketballTerrain.setVisibility(View.GONE);
        } else {
            mActivityMapsBinding.locationBasketballTerrain.setVisibility(View.VISIBLE);
        }
        if (!hasTennisTerrain) {
            mActivityMapsBinding.locationTennisTerrain.setVisibility(View.GONE);
        } else {
            mActivityMapsBinding.locationTennisTerrain.setVisibility(View.VISIBLE);
        }
        if (!hasCustomTerrain) {
            mActivityMapsBinding.locationCustomTerrain.setVisibility(View.GONE);
        } else {
            mActivityMapsBinding.locationCustomTerrain.setVisibility(View.VISIBLE);
        }


        //final ViewPager viewPager_images = mActivityMapsBinding.viewPager;

//        mLocations.forEach(location -> {
//            if (location.getName().equals(marker.getTitle())) {
//                mCurrentItemReference = location;
//                mSlideshowAdapter = new SlideshowAdapter(this, populateImageList(location));
//                viewPager_images.setAdapter(mSlideshowAdapter);
//                populateImageList(location);
//                Handler mHandler = new Handler();
//                Runnable mRunnable = () -> {
//                    int item = viewPager_images.getCurrentItem();
//                    if (item == mSlideshowAdapter.mImages.size() - 1) {
//                        item = 0;
//                    } else {
//                        item++;
//                    }
//                    viewPager_images.setCurrentItem(item, true);
//                };
//                Timer mTimer = new Timer();
//                mTimer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        mHandler.post(mRunnable);
//                    }
//                }, 5000, 5000);
//            }
//        });

        return false;
    }

    private boolean locationIsValid(String title) {
        return mLocationObjects.contains(title);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hideHolder:
                if (mActivityMapsBinding.componentHolder.isShown()) {
                    mActivityMapsBinding.componentHolder.setVisibility(View.GONE);
                }
                break;
            case R.id.locationDetails:
                Intent goToLocationDetails = new Intent(this, TerrainActivity.class);
                if (mCurrentItemReference != null) {
                    goToLocationDetails.putExtra("CURRENT_ID", mCurrentItemReference.getId());
                    startActivity(goToLocationDetails);
                } else {
                    Log.d(TAG, "null reference on click!");
                }
                break;

        }
    }

    private ArrayList<String> populateImageList(Location location) {
        ArrayList<String> locationImages = new ArrayList<>();
        if (location.getImageList().size() == 0 || location.getImageList() == null) {
            locationImages.add(String.valueOf(R.drawable.no_image_available));
        } else {
            locationImages.addAll(location.getImageList());
        }
        return locationImages;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}