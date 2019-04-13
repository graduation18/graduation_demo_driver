package com.example.gaber.graduation_demo_driver.fragments.carpooling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.Mainview;
import com.example.gaber.graduation_demo_driver.custom.DataPassListener;
import com.example.gaber.graduation_demo_driver.custom.DirectionsJSONParser;
import com.example.gaber.graduation_demo_driver.models.trip_model;
import com.example.gaber.graduation_demo_driver.services.CustomAlarmReceiver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class carpooling_cost_step5 extends Fragment  {
    View view;
    String dist_durat= null;
    Button Confirm_Leaving_from;
    private double Leave_from_latt,Leave_from_longt,going_to_latt,going_to_longt;
    private String Leave_from_state,going_to_state,Leave_from_city,going_to_city,Leave_from_address,going_to_address,driver_id,distance,duration;
    private long time;
    private int seats_number,cost;
    private RequestQueue queue;
    private ProgressWindow progressWindow ;
    private TextView back,number_of_notifications,notifications,number,minus,plus;

    DataPassListener mCallback;





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DataPassListener) context;

        Bundle args = getArguments();
        going_to_latt=args.getDouble("going_to_latt");
        going_to_longt=args.getDouble("going_to_longt");
        going_to_city=args.getString("going_to_city");
        going_to_address=args.getString("going_to_address");
        Leave_from_latt=args.getDouble("Leave_from_latt");
        Leave_from_longt=args.getDouble("Leave_from_longt");
        Leave_from_city=args.getString("Leave_from_city");
        Leave_from_address=args.getString("Leave_from_address");
        Leave_from_state=args.getString("Leave_from_state");
        going_to_state=args.getString("going_to_state");
        seats_number=args.getInt("seats_number");
        time=args.getLong("time");
        driver_id=getActivity().getSharedPreferences("logged_in",MODE_PRIVATE).getString("driver_key","");
        Log.w("ssss",Leave_from_city+" "+going_to_city+" "+time+" "+seats_number);

    }





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.confirm_price, container, false);
        progressConfigurations();
        Confirm_Leaving_from=(Button)view.findViewById(R.id.Confirm_Leaving_from);
        minus=(TextView)view.findViewById(R.id.cash_minus);
        plus=(TextView)view.findViewById(R.id.cash_plus);
        number=(TextView)view.findViewById(R.id.cash);
        back=(TextView)view.findViewById(R.id.back);
        get_direction_json(new LatLng(Leave_from_latt,Leave_from_longt),new LatLng(going_to_latt,going_to_longt));



        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seats_number= Integer.parseInt(number.getText().toString());
                if (seats_number!=1){
                    number.setText(String.valueOf(seats_number-1));
                }else {
                    Toast.makeText(getActivity(),"that's the minimum of seat",Toast.LENGTH_LONG).show();
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seats_number= Integer.parseInt(number.getText().toString());

                    number.setText(String.valueOf(seats_number+1));

            }
        });


        Confirm_Leaving_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cost= Integer.parseInt(number.getText().toString());
                add_trip( driver_id,Leave_from_latt, Leave_from_longt, going_to_latt, going_to_longt,
                 Leave_from_city, going_to_city, Leave_from_address, going_to_address,Leave_from_state,going_to_state,
                 time, seats_number, cost);

            }
        });





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putDouble("going_to_latt",going_to_latt);
                args.putDouble("going_to_longt",going_to_longt);
                args.putString("going_to_city",going_to_city);
                args.putString("going_to_state",going_to_state);
                args.putString("going_to_address",going_to_address);
                args.putDouble("Leave_from_latt",Leave_from_latt);
                args.putDouble("Leave_from_longt",Leave_from_longt);
                args.putString("Leave_from_city",Leave_from_city);
                args.putString("Leave_from_address",Leave_from_address);
                args.putString("Leave_from_state",Leave_from_state);
                args.putLong("time",time);
                args.putInt("seats_number",seats_number);
                mCallback.passData(new carpooling_seats_step4(),args);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Bundle args = new Bundle();
                    args.putDouble("going_to_latt",going_to_latt);
                    args.putDouble("going_to_longt",going_to_longt);
                    args.putString("going_to_city",going_to_city);
                    args.putString("going_to_state",going_to_state);
                    args.putString("going_to_address",going_to_address);
                    args.putDouble("Leave_from_latt",Leave_from_latt);
                    args.putDouble("Leave_from_longt",Leave_from_longt);
                    args.putString("Leave_from_city",Leave_from_city);
                    args.putString("Leave_from_address",Leave_from_address);
                    args.putString("Leave_from_state",Leave_from_state);
                    args.putLong("time",time);
                    args.putInt("seats_number",seats_number);
                    mCallback.passData(new carpooling_seats_step4(),args);
                    return true;
                }
                return false;
            }
        });
        return view;
    }
    private void add_trip(String driver_id,double Leave_from_latt,double Leave_from_longt,double going_to_latt,double going_to_longt,
                            String Leave_from_city,String going_to_city,String Leave_from_address,String going_to_address,
                            String Leave_from_state,String going_to_state, long time,int seats_number,int cost) {
        trip_model trip=new trip_model(driver_id,Leave_from_latt,Leave_from_longt,going_to_latt,going_to_longt,Leave_from_city,going_to_city
        ,Leave_from_address,going_to_address,Leave_from_state,going_to_state,time,seats_number,cost);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("trips").push().setValue(trip);
        set_alarm(new Date(time),Leave_from_city,going_to_city,(int)time);
        startActivity(new Intent(getActivity(),Mainview.class));
        getActivity().finish();
    }
    private void set_alarm(Date date, String Leave_from_city,String going_to_city,int id){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String dateText = df2.format(date);

        Intent alarmIntent = new Intent(getActivity(), CustomAlarmReceiver.class);

        //pass extra data to CustomAlarmReceiver intent to be handled when the alarm goes off
        alarmIntent.putExtra("Leave_from_city", Leave_from_city);
        alarmIntent.putExtra("going_to_city", going_to_city);
        alarmIntent.putExtra("date", dateText);

        // creates a new PendingIntent using the static variable eventID.
        // using eventID allows you to create multiple events with the same code
        // without a unique id the intent would just be updated with new extras each time its created
        //
        PendingIntent pendingAlarm = PendingIntent.getBroadcast(
                getActivity(), id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingAlarm);

    }
    private void get_direction_json(LatLng origin, LatLng destination)
    {
        showProgress();


        try {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin.latitude+","+origin.longitude+
                    "&destination="+destination.latitude+","+destination.longitude+"&mode=driving&key=AIzaSyDxy0ndkDYovy6TEo71pnhMhfopHbX4fpg";
            if (queue == null) {
                queue = Volley.newRequestQueue(getActivity());
            }
            // Request a string response from the provided URL.
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parse_direction_json(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {

        }


    }
    private void parse_direction_json(JSONObject object)
    {

        DirectionsJSONParser parser = new DirectionsJSONParser();
        dist_durat = parser.get_duration_distance(object);
        Log.w("lshdkjsa",dist_durat);
        get_seat_cost(dist_durat);

    }
    private void get_seat_cost(String dist_durat){
        distance=dist_durat.split(",")[0].split("km")[0].replaceAll("\\s+","");
        duration=dist_durat.split(",")[1];
        double distanceInkilos = Double.parseDouble(distance);
        double trip_cost=distanceInkilos*0.6;
        number.setText(String.valueOf(Math.round(trip_cost*2/4)));
        hideProgress();

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
