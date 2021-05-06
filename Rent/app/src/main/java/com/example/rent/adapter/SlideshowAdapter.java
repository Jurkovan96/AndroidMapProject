package com.example.rent.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.rent.R;
import com.example.rent.databinding.SlideshowLayoutBinding;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Class used to create an adapter to populate the ViewPager that displays the images of a product
 */
public class SlideshowAdapter extends PagerAdapter {
    /**
     * The context of the application
     */
    private Context mContext;
    /**
     * The inflater
     */
    LayoutInflater mInflater;
    /**
     * The list of images used to populate the ViewPager
     */
    public ArrayList<String> mImages;

    /**
     * The constructor of the class
     *
     * @param mContext : the context of the application
     * @param mImages  : the list of images that will be displayed inside the ViewPager
     */
    public SlideshowAdapter(Context mContext, ArrayList<String> mImages) {
        this.mContext = mContext;
        this.mImages = mImages;
    }

    /**
     * Method that gets the size of the list containing the images
     *
     * @return : the size of the list
     */
    @Override
    public int getCount() {
        return mImages.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object as returned by instantiateItem method
     *
     * @param view   :  Page View to check for association with object
     * @param object :  Object to check for association with view
     * @return : true if view is associated with the key object object, false otherwise
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);
    }

    /**
     * Method that creates the page for the given position. The adapter is responsible for
     * adding the view to the container given here
     *
     * @param container : The containing View in which the page will Sbe shown
     * @param position  : The page position to be instantiated
     * @return : Returns an Object representing the new page
     */
    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SlideshowLayoutBinding binding = DataBindingUtil.inflate(mInflater, R.layout.slideshow_layout,
                container, false);
        ImageView img = binding.imageviewId;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference().child(mImages.get(position)).getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(mContext.getApplicationContext()).load(uri).into(img));
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    /**
     * Method that removes a page for the given position
     *
     * @param container : The containing View from which the page will be removed
     * @param position  : The page position to be removed
     * @param object    :  The same object that was returned by instantiateItem method
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
