package com.example.gaber.graduation_demo_driver.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.adapters.ViewPagerAdapter_with_titles;
import com.example.gaber.graduation_demo_driver.fragments.uber.Main_map;
import com.example.gaber.graduation_demo_driver.fragments.carpooling.car_pooling;
import com.example.gaber.graduation_demo_driver.fragments.settings;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class Mainview extends AppCompatActivity {
     private ViewPager viewPager;
    MenuItem prevMenuItem;
    BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient mFusedLocationClient;
    private int MY_PERMISSIONS_REQUEST_locations=00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_mainview);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        request_location();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.contacts:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.conversations:
                                viewPager.setCurrentItem(1);
                                break;
                            /*case R.id.delayed_messages:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.hold_messages:
                                viewPager.setCurrentItem(3);
                                break;
                                */
                            case R.id.settings:
                                viewPager.setCurrentItem(4);
                                break;

                        }
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       /*   //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
        */
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter_with_titles adapter = new ViewPagerAdapter_with_titles(getSupportFragmentManager());
        Main_map mainmap_fragment =new Main_map();
        //drive_requests driverequests_fragment =new drive_requests();
        //deliveries deliveries_fragment =new deliveries();
        car_pooling carpooling_fragment =new car_pooling();
        settings settings_fragment =new settings();
        adapter.addFragment(mainmap_fragment);
       // adapter.addFragment(driverequests_fragment);
        //adapter.addFragment(deliveries_fragment);
        adapter.addFragment(carpooling_fragment);
        adapter.addFragment(settings_fragment);
        viewPager.setAdapter(adapter);
    }
    @SuppressLint("MissingPermission")
    public void request_location(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener( this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            getSharedPreferences("driver_data",MODE_PRIVATE)
                                    .edit().putString("driver_lat", String.valueOf(location.getLatitude()))
                                    .putString("driver_long", String.valueOf(location.getLongitude()))
                                    .apply();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        request_location();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSharedPreferences("driver_data",MODE_PRIVATE)
                .edit().putString("driver_lat","")
                .putString("driver_long","")
                .apply();
    }
}
