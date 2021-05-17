package com.example.rent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent.databinding.ItemReservationBinding;
import com.example.rent.model.Reservation;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> mReservations;

    private String mUserId;

    private DeleteItem mDeleteItem;

    public ReservationAdapter(List<Reservation> reservations, String userId, DeleteItem deleteItem) {
        this.mReservations = reservations;
        this.mUserId = userId;
        this.mDeleteItem = deleteItem;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReservationBinding binding = ItemReservationBinding.inflate(inflater, parent, false);
        return new ReservationAdapter.ReservationViewHolder(binding, mUserId);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = mReservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return mReservations.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {

        private ItemReservationBinding mItemReservationBinding;

        private String mUserId;

        public ReservationViewHolder(ItemReservationBinding itemReservationBinding, String userId) {
            super(itemReservationBinding.getRoot());
            this.mItemReservationBinding = itemReservationBinding;
            this.mUserId = userId;
        }

        public void bind(Reservation reservation) {
            mItemReservationBinding.setReservation(reservation);
            mItemReservationBinding.executePendingBindings();
            mItemReservationBinding.openReservationDescriptionLabel.setOnClickListener(view -> {

                mItemReservationBinding.openReservationDescriptionLabel
                        .setVisibility(View.GONE);
                mItemReservationBinding.descriptionReservation
                        .setVisibility(View.VISIBLE);
                mItemReservationBinding.descriptionReservationHidden
                        .setVisibility(View.VISIBLE);
                mItemReservationBinding.closeReservationDescriptionLabel
                        .setVisibility(View.VISIBLE);
            });

            mItemReservationBinding.closeReservationDescriptionLabel.setOnClickListener(view -> {
                mItemReservationBinding.openReservationDescriptionLabel
                        .setVisibility(View.VISIBLE);
                mItemReservationBinding.descriptionReservation
                        .setVisibility(View.GONE);
                mItemReservationBinding.descriptionReservationHidden
                        .setVisibility(View.GONE);
                mItemReservationBinding.closeReservationDescriptionLabel
                        .setVisibility(View.GONE);
            });
            if (reservation.getUserId().equals(mUserId)) {
                mItemReservationBinding.deleteReservation.setVisibility(View.VISIBLE);
            }
            mItemReservationBinding.deleteReservation.setOnClickListener(v -> mDeleteItem.deleteReservationItem(reservation));
        }
    }

    public interface DeleteItem {
        void deleteReservationItem(Reservation reservation);
    }
}
