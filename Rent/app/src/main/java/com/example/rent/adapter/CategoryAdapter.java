package com.example.rent.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent.databinding.ItemTerrainCategoryBinding;
import com.example.rent.model.Reservation;
import com.example.rent.model.Terrain;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public static final String VENDOR_INTENT_FIELD = "vendorIntent";

    private Context mContext;

    private List<Terrain> mTerrainList;


    public CategoryAdapter(Context context, List<Terrain> userList) {
        this.mContext = context;
        this.mTerrainList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTerrainCategoryBinding binding = ItemTerrainCategoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Terrain terrain = mTerrainList.get(position);
        holder.bind(terrain);
    }

    @Override
    public int getItemCount() {
        return mTerrainList == null ? 0 : mTerrainList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemTerrainCategoryBinding mBinding;

        public ViewHolder(ItemTerrainCategoryBinding itemTerrainCategoryBinding) {
            super(itemTerrainCategoryBinding.getRoot());
            this.mBinding = itemTerrainCategoryBinding;
        }

        public void bind(Terrain terrain) {
            mBinding.setItemTerrain(terrain);
            mBinding.executePendingBindings();
            mBinding.cardViewContainer.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, Reservation.class);
                intent.putExtra(VENDOR_INTENT_FIELD, mBinding.getItemTerrain());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            });
        }
    }
}