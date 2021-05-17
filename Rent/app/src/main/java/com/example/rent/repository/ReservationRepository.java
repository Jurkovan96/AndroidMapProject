package com.example.rent.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.rent.FireHelper;
import com.example.rent.model.Reservation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    public static final String TAG = ReservationRepository.class.getName();

    public static final String RESERVATION_COLLECTION = "Reservations";

    private final FirebaseFirestore mFirebaseFirestore;

    public ReservationRepository() {
        this.mFirebaseFirestore = FireHelper.getInstanceOfFirebase();
    }

    public MutableLiveData<List<Reservation>> getAllReservation(String terrainId) {
        MutableLiveData<List<Reservation>> listMutableLiveData = new MutableLiveData<>();
        mFirebaseFirestore
                .collection(RESERVATION_COLLECTION)
                .whereEqualTo("terrainId", terrainId)
                .addSnapshotListener((value, error) -> {
                    List<Reservation> reservations = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Reservation reservation = documentSnapshot.toObject(Reservation.class);
                            if (reservation != null) {
                                reservation.setId(documentSnapshot.getId());
                                reservations.add(reservation);
                            }
                        }
                    }
                    listMutableLiveData.setValue(reservations);
                });
        return listMutableLiveData;
    }

    public void addNewReservation(Reservation reservation) {
        mFirebaseFirestore.collection(RESERVATION_COLLECTION).add(reservation);
    }

    public void deleteReservation(Reservation reservation) {
        mFirebaseFirestore.collection(RESERVATION_COLLECTION)
                .document(reservation.getId()).delete();
    }
}
