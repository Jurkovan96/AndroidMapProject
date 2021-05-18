package com.example.rent.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent.databinding.RecyclerviewRowBinding;

import java.util.List;

public class TimePickerRecyclerAdapter extends RecyclerView.Adapter<TimePickerRecyclerAdapter.ViewHolder> {

    private List<String> mData;

    public TimePickerRecyclerAdapter(List<String> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String hourOrMinute = mData.get(position);
        holder.bind(hourOrMinute);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RecyclerviewRowBinding mBinding;

        ViewHolder(RecyclerviewRowBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bind(String hourOrMinute) {
            mBinding.setHourOrMinuteString(hourOrMinute);
            mBinding.executePendingBindings();
        }
    }
}
