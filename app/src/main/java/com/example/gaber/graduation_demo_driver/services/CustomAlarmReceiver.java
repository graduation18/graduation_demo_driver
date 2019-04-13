package com.example.gaber.graduation_demo_driver.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.gaber.graduation_demo_driver.R;
import com.example.gaber.graduation_demo_driver.activities.Mainview;

import java.util.List;

public class CustomAlarmReceiver extends BroadcastReceiver {
    private NotificationManager notifManager;
    private NotificationChannel mChannel;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String Leave_from_city = bundle.getString("Leave_from_city");
            String going_to_city = bundle.getString("going_to_city");
            String  date = bundle.getString("date");
            notification(Leave_from_city,going_to_city,date,context);
        //this will update the UI with message


    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void notification(String Leave_from_city, String going_to_city,String date, Context context){

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }

        intent = new Intent (context, Mainview.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                NotificationChannel mChannel = new NotificationChannel
                        ("0","trip on "+date,importance);
                mChannel.setDescription ("you have trip hour from now from"+Leave_from_city+" to "+going_to_city);
                mChannel.enableVibration (true);
                mChannel.setVibrationPattern (new long[]
                        {100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel (mChannel);
            }
            builder = new NotificationCompat.Builder (context,"0");

            intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity (context, 0, intent, 0);
            builder.setContentTitle ("trip")  // flare_icon_30
                    .setSmallIcon (R.drawable.common_google_signin_btn_icon_dark) // required
                    .setContentText ("you have trip hour from now from")
                    .setSubText("you have trip hour from now from"+Leave_from_city+" to "+going_to_city)// required
                    .setDefaults (Notification.DEFAULT_ALL)
                    .setAutoCancel (true)
                    .setContentIntent (pendingIntent)
                    .setSound (RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate (new long[]{100, 200, 300, 400,
                            500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder (context);


            Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
            builder.setContentTitle("trip");
            builder.setContentText("you have trip hour from now from"+Leave_from_city+" to "+going_to_city);
            builder.setColor((context.getResources().getColor(R.color.colorAccent)));
            builder.setSound(sound);
            builder.setVibrate (new long[]{100, 200, 300, 400,
                    500, 400, 300, 200, 400});
            Intent resultIntent = new Intent(context, Mainview.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(Mainview.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

// notificationID allows you to update the notification later on.


        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification notification = builder.build ();
        int id = (int) System.currentTimeMillis();
        notifManager.notify (id, notification);

    }





}