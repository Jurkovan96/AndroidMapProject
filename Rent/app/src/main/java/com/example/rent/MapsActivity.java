package com.example.rent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.rent.adapter.SlideshowAdapter;
import com.example.rent.databinding.ActivityMapsBinding;
import com.example.rent.model.Location;
import com.example.rent.viewmodel.LocationViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, View.OnClickListener {
    public static final String TAG = MapsActivity.class.getName();

    private GoogleMap mMap;

    private Button button;

    private Set<Location> mLocations;

    private ActivityMapsBinding mActivityMapsBinding;

    private SupportMapFragment mMapFragment;

    private LocationViewModel mLocationViewModel;

    private SlideshowAdapter mSlideshowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mLocations = new HashSet<>();
        mLocationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        mActivityMapsBinding.hideHolder.setOnClickListener(this);

        mLocationViewModel.getLocations().observe(this, locations -> {
            mLocations.clear();
            mLocations.addAll(locations);
            Log.d(TAG, "The list of the locations: " + mLocations.size());
            mMapFragment.getMapAsync(this);
        });

        // mLocationViewModel.addLocation(new Location("Location1", "Desc1", "rev1", 47.157859, 27.594512));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng custom = new LatLng(47.157859, 27.594512);
        //mMap.addMarker(new MarkerOptions().position(custom).title("Custom marker").icon(getBitMapFromImage(getApplicationContext())));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(custom));
        objectToMarkerConversion().forEach(markerOptions -> mMap.addMarker(markerOptions));
        mMap.setOnMarkerClickListener(this);
    }


    private Set<MarkerOptions> objectToMarkerConversion() {
        Set<MarkerOptions> markerLocations = new HashSet<>();
        mLocations.forEach(location -> markerLocations.add(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(location.getName())));
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
        if (mActivityMapsBinding.componentHolder.getVisibility() != View.VISIBLE) {
            mActivityMapsBinding.componentHolder.setVisibility(View.VISIBLE);
        }
        mActivityMapsBinding.locationName.setText(marker.getTitle());
        mLocations.forEach(location -> {
            if (location.getName().equals(marker.getTitle())) {
                final ViewPager viewPager_images = mActivityMapsBinding.viewPager;
                mSlideshowAdapter = new SlideshowAdapter(this, populateImageList(location));
                viewPager_images.setAdapter(mSlideshowAdapter);
                populateImageList(location);
                Handler mHandler = new Handler();
                Runnable mRunnable = () -> {
                    int item = viewPager_images.getCurrentItem();
                    if (item == mSlideshowAdapter.mImages.size() - 1) {
                        item = 0;
                    } else {
                        item++;
                    }
                    viewPager_images.setCurrentItem(item, true);
                };
                Timer mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(mRunnable);
                    }
                }, 4000, 4000);
            }
        });

        return false;
    }

    @Override
    public void onClick(View v) {
        if (mActivityMapsBinding.componentHolder.getVisibility() == View.VISIBLE) {
            mActivityMapsBinding.componentHolder.setVisibility(View.GONE);
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

}