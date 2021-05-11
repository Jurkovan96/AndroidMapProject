package com.example.rent.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.rent.FireHelper;
import com.example.rent.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserRepository {

    public static final String TAG = UserRepository.class.getName();

    private final String USER_COLLECTION = "Users";

    private FirebaseFirestore mFirebaseFirestore;

    public UserRepository() {
        this.mFirebaseFirestore = FireHelper.getInstanceOfFirebase();
    }

    public MutableLiveData<User> getUser(final String userId) {
        final MutableLiveData<User> userResponse = new MutableLiveData<>();
        if (userId.isEmpty()) {
            return null;
        }
        mFirebaseFirestore.collection(USER_COLLECTION).document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    User user = snapshot.toObject(User.class);
                    if (user != null) {
                        user.setId(snapshot.getId());
                        userResponse.setValue(user);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting a user's account details", e);
                    userResponse.setValue(null);
                });
        return userResponse;
    }

    public void updateUser(User user) {
        final MutableLiveData<Boolean> boolResponse = new MutableLiveData<>();
        if (user.getId().isEmpty() || user.getName().isEmpty()
                || user.getPassword().isEmpty()
                || user.getEmail().isEmpty()) {
            boolResponse.setValue(false);
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("id", user.getId());
        updates.put("email", user.getEmail());
        updates.put("name", user.getName());
        updates.put("password", user.getPassword());
        mFirebaseFirestore.collection(USER_COLLECTION).document(user.getId()).update(updates)
                .addOnSuccessListener(aVoid -> boolResponse.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user account", e);
                    boolResponse.setValue(false);
                });
    }

    public MutableLiveData<User> getCurrentUser(final String userId) {
        final MutableLiveData<User> userResponse = new MutableLiveData<>();
        if (userId.isEmpty()) {
            return null;
        }
        mFirebaseFirestore.collection(USER_COLLECTION).document(userId)
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    User user = value.toObject(User.class);
                    assert user != null;
                    user.setId(value.getId());
                    userResponse.setValue(user);
                });
        return userResponse;
    }

    public MutableLiveData<List<User>> getAllUsers() {
        final MutableLiveData<List<User>> listUsers = new MutableLiveData<>();
        mFirebaseFirestore.collection(USER_COLLECTION)
                .addSnapshotListener((value, error) -> {
                    List<User> users = new ArrayList<>();
                    assert value != null;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        user.setId(documentSnapshot.getId());
                        users.add(user);
                    }
                    listUsers.setValue(users);
                });
        return listUsers;
    }

    public void createUserObject(User user) {
        final MutableLiveData<Boolean> boolResponse = new MutableLiveData<>();
        if (user == null || user.getEmail().isEmpty()
                || user.getName().isEmpty()
                || user.getPassword().isEmpty()
                || user.getId().isEmpty()) {
            boolResponse.setValue(false);
            return;
        }
        mFirebaseFirestore.collection(USER_COLLECTION).document(user.getId()).set(user).
                addOnSuccessListener(aVoid -> boolResponse.setValue(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding user to the database", e);
                    boolResponse.setValue(false);
                });
    }
}
