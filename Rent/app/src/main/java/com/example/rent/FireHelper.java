package com.example.rent;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireHelper {

    @SuppressLint("StaticFieldLeak")
    private static volatile FirebaseFirestore instanceOfFireStore;

    private static volatile FirebaseAuth firebaseAuth;

    public static FirebaseFirestore getInstanceOfFirebase() {
        if (instanceOfFireStore == null) {
            synchronized (FirebaseFirestore.class) {
                if (instanceOfFireStore == null) {
                    instanceOfFireStore = FirebaseFirestore.getInstance();
                }
            }
        }
        return instanceOfFireStore;
    }

    public static FirebaseAuth getInstanceOfAuth() {
        if (firebaseAuth == null) {
            synchronized (FirebaseFirestore.class) {
                if (firebaseAuth == null) {
                    firebaseAuth = FirebaseAuth.getInstance();
                }
            }
        }
        return firebaseAuth;
    }
}
