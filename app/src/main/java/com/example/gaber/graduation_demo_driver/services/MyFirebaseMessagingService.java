package com.example.gaber.graduation_demo_driver.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.Mainview;
import com.example.gaber.graduation_demo_driver.activities.uber_ride;
import com.example.gaber.graduation_demo_driver.models.user_data_model;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.gaber.graduation_demo_driver.custom.App.CHANNEL_1_ID;

/**
 * Created by gaber on 26/08/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {
            try {
                notificationManager = NotificationManagerCompat.from(this);
                find_user(remoteMessage);
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
    }
    private void find_user(RemoteMessage remoteMessage) throws ParseException {
        Map<String,String> data=remoteMessage.getData();
        final String pick_up_lat =  data.get("pick_up_lat");
        final String pick_up_lng =  data.get("pick_up_lng");
        final String drop_off_lat =  data.get("drop_off_lat");
        final String drop_off_lng =  data.get("drop_off_lng");
        final String text =  data.get("text");
        final String type =  data.get("type");
        final String key =  data.get("key");
        final int time = Integer.parseInt(data.get("time"));
         String from_city=null,from_state=null,from_address=null,going_to_city=null,going_to_state=null,going_to_address=null;
        Log.w("hvh",type);

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(Double.parseDouble(pick_up_lat),Double.parseDouble(pick_up_lng) , 1);
            from_city=addresses.get(0).getLocality();
            from_state=addresses.get(0).getAdminArea();
            from_address = addresses.get(0).getAddressLine(0);
            List<Address> addresses2 = gcd.getFromLocation(Double.parseDouble(drop_off_lat),Double.parseDouble(drop_off_lng) , 1);
            going_to_city=addresses2.get(0).getLocality();
            going_to_state=addresses2.get(0).getAdminArea();
            going_to_address = addresses2.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }


        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = mFirebaseDatabaseReference.orderByKey().equalTo(key);

        final String finalAddress = from_address;
        final String final_G_Address = going_to_address;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String from_name=dataSnapshot1.getValue(user_data_model.class).name;
                    String from_token=dataSnapshot1.getValue(user_data_model.class).user_token;

                    notification_uber(text,from_name,time,finalAddress,final_G_Address,new LatLng(Double.parseDouble(pick_up_lat),Double.parseDouble(pick_up_lng))
                            ,new LatLng(Double.parseDouble(drop_off_lat),Double.parseDouble(drop_off_lng)),from_token);




                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

 private void notification_uber(String phone, String name , int time, String finalAddress
         ,String final_G_Address, LatLng pick_up,LatLng drop_off,String from_token){
     Intent activityIntent = new Intent(this, Mainview.class);
     PendingIntent contentIntent = PendingIntent.getActivity(this,
             0, activityIntent, 0);

     Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
     broadcastIntent.putExtra("toastMessage", "you rejected ride with "+name);

     Intent ride_accepted = new Intent(this, uber_ride.class);
     ride_accepted.putExtra(" pick_up_latitude", pick_up.latitude);
     ride_accepted.putExtra("pick_up_longitude", pick_up.longitude);
     ride_accepted.putExtra("drop_off_latitude", drop_off.latitude);
     ride_accepted.putExtra("drop_off_longitude", drop_off.longitude);
     ride_accepted.putExtra("name",name);
     ride_accepted.putExtra("time", time);
     ride_accepted.putExtra("phone", phone);
     ride_accepted.putExtra("from_Address", finalAddress);
     ride_accepted.putExtra("going_to_Address", final_G_Address);
     ride_accepted.putExtra("from_token", from_token);


     PendingIntent actionIntent = PendingIntent.getBroadcast(this,
             0, broadcastIntent, PendingIntent.FLAG_ONE_SHOT);
     PendingIntent ride_accepted_actionIntent = PendingIntent.getActivity(this,
             0, ride_accepted, PendingIntent.FLAG_ONE_SHOT);

     Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
             .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
             .setContentTitle(name+" wants to book ride ")
             .setContentInfo("From :"+finalAddress+"\n"+"Going to :"+final_G_Address)
             .setPriority(NotificationCompat.PRIORITY_HIGH)
             .setCategory(NotificationCompat.CATEGORY_MESSAGE)
             .setColor(Color.WHITE)
             .setContentIntent(contentIntent)
             .setAutoCancel(false)
             .setOnlyAlertOnce(true)
             .addAction(R.drawable.ic_thumb_down_black_24dp, "ٌReject", actionIntent)
             .addAction(R.drawable.ic_location_on_black_24dp, "Accept", ride_accepted_actionIntent)
             .build();

     notificationManager.notify(1, notification);
 }

}

