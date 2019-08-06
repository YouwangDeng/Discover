package com.youwangd.productsearch;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private NavTabAdapter navAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        navAdapter = new NavTabAdapter(getSupportFragmentManager());
        navAdapter.addFragment(new SearchForm(), "SEARCH");
        navAdapter.addFragment(new WishList(), "WISH LIST");
        viewPager.setAdapter(navAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
