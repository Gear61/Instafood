package com.randomappsinc.instafood.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.instafood.fragments.PictureFullViewFragment;

import java.util.List;

public class PictureFullViewGalleryAdapter extends FragmentStatePagerAdapter {

    private List<String> imageUrls;

    public PictureFullViewGalleryAdapter(FragmentManager fragmentManager, List<String> imageUrls) {
        super(fragmentManager);
        this.imageUrls = imageUrls;
    }

    @Override
    public Fragment getItem(int position) {
        return PictureFullViewFragment.newInstance(imageUrls.get(position));
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }
}
