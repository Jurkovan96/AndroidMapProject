package com.example.rent.adapter;

import com.google.firebase.auth.FirebaseAuth;

public interface IconInteraction {

    void onLogoutIconClick(FirebaseAuth firebaseAuth);

    void onProfileIconClick();
}
