package com.example.rent.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    private ActivityProfileBinding mActivityProfileBinding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mFirebaseAuth = FireHelper.getInstanceOfAuth();
        //initializeCustomToolBar();
        setUpToolbar();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setUpToolbar() {
        if (mActivityProfileBinding != null) {
            setSupportActionBar(mActivityProfileBinding.toolBar.toolbarMain);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bottom_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_item && mFirebaseAuth.getCurrentUser() != null) {
            mFirebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}