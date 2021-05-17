package com.example.rent.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rent.databinding.RecyclerviewRowBinding;


import java.util.List;

/**
 * Adapter used for recycler views from timpe picker dialog
 */
public class TimePickerRecyclerAdapter extends RecyclerView.Adapter<TimePickerRecyclerAdapter.ViewHolder> {

    /**
     * List of strings ( minutes or hours)
     */
    private List<String> mData;

    /**
     * Constructor for adapter
     *
     * @param data List of strings ( minutes or hours)
     */
    public TimePickerRecyclerAdapter(List<String> data) {
        this.mData = data;
    }

    /**
     * Called when RecyclerView needs a new view holder to represent
     * an item.
     *
     * @param parent   the ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType the view type of the new View
     * @return the viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(recyclerviewRowBinding);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the item to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set
     * @param position position of the item within the adapter
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String hourOrMinute = mData.get(position);
        holder.bind(hourOrMinute);
    }

    /**
     * Getter for the size of the list of minutes/hours
     *
     * @return size of the list
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * ViewHolder class holding a hour or minute string
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The binding object used to access the bounded data
         */
        private RecyclerviewRowBinding mBinding;

        /**
         * Public constructor
         *
         * @param binding the binding object for the specific item
         */
        ViewHolder(RecyclerviewRowBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        /**
         * Bind the data of the property object to the view
         *
         * @param hourOrMinute the object to be bound
         */
        public void bind(String hourOrMinute) {
            mBinding.setHourOrMinuteString(hourOrMinute);
            mBinding.executePendingBindings();
        }

    }
}
