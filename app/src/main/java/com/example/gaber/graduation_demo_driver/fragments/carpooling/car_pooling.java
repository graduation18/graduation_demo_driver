package com.example.gaber.graduation_demo_driver.fragments.carpooling;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.offer_ride;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.gms.common.api.Status;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class car_pooling extends Fragment  {
    View view;
    Button offer_ride_button;
    private ProgressWindow progressWindow ;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.car_pooling_fragment, container, false);
        progressConfigurations();
        offer_ride_button=(Button)view.findViewById(R.id.offer_ride);
        offer_ride_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offer_ride_intent=new Intent(getActivity(),offer_ride.class);
                startActivity(offer_ride_intent);
            }
        });





        return view;
    }
    private void progressConfigurations(){
        progressWindow = ProgressWindow.getInstance(getActivity());
        ProgressWindowConfiguration progressWindowConfiguration = new ProgressWindowConfiguration();
        progressWindowConfiguration.backgroundColor = Color.parseColor("#32000000") ;
        progressWindowConfiguration.progressColor = Color.WHITE ;
        progressWindow.setConfiguration(progressWindowConfiguration);
    }
    public void showProgress(){
        progressWindow.showProgress();
    }
    public void hideProgress(){
        progressWindow.hideProgress();
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgress();

    }



}
