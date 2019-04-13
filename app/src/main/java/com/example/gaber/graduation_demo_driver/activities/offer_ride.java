package com.example.gaber.graduation_demo_driver.activities;



import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.custom.DataPassListener;
import com.example.gaber.graduation_demo_driver.fragments.carpooling.*;
import com.shuhart.stepview.StepView;
import java.util.ArrayList;
import java.util.List;

public class offer_ride extends AppCompatActivity implements DataPassListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
        Fragment car_pooling=new carpooling_leaving_from_step1();
        loadFragment(car_pooling);
    }
    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();    }

    @Override
    public void passData(Fragment fragment,Bundle data) {
        fragment.setArguments(data);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}