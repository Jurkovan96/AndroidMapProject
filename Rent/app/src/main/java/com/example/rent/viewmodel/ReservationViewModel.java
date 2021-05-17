package com.example.rent.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rent.model.Reservation;
import com.example.rent.repository.ReservationRepository;

import java.util.List;

public class ReservationViewModel extends ViewModel {

    private final ReservationRepository reservationRepository;

    public ReservationViewModel() {
        this.reservationRepository = new ReservationRepository();
    }

    public MutableLiveData<List<Reservation>> getAllReservationsByTerrainId(String terrainId) {
        return reservationRepository.getAllReservation(terrainId);
    }


    public void addReservation(Reservation reservation) {
        reservationRepository.addNewReservation(reservation);
    }

    public void deleteReservation(Reservation reservation) {
        reservationRepository.deleteReservation(reservation);
    }
}
