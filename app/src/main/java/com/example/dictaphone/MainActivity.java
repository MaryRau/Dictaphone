package com.example.dictaphone;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewpager(viewPager);
    }

    private void setupViewpager(ViewPager2 viewPager) {
        ImageView micIcon = new ImageView(this);
        micIcon.setImageResource(R.drawable.mic);
        ImageView listIcon = new ImageView(this);
        listIcon.setImageResource(R.drawable.list);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPagerAdapter.addFragment(new Recorder(), micIcon);
        viewPagerAdapter.addFragment(new Recordings(), listIcon);

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(viewPagerAdapter.getPageTitle(position));
            }
        }).attach();
    }
}