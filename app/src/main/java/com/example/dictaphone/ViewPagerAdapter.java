package com.example.dictaphone;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final ArrayList<ImageView> fragTitles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull MainActivity fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment, ImageView title) {
        fragments.add(fragment);
        fragTitles.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public ImageView getPageTitle(int position) {
        return fragTitles.get(position);
    }
}
