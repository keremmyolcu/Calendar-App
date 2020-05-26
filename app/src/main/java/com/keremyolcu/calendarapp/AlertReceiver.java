package com.keremyolcu.calendarapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    private Ringtone ringtone = null;
    int id;
    String baslik;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("sharedpreferences",Context.MODE_PRIVATE);
        String ringType = prefs.getString("RING","");

        if(ringType.compareTo("ALARM") == 0){
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(context, uri);
        }

        else if(ringType.compareTo("NOTIF") == 0){
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(context, uri);
        }

        else{
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(context, uri);
        }

        ringtone.play();
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ringtone.stop();
            }
        }, 3000);                                                                   //ringtone cal



        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);                                    //titret
        vibrator.vibrate(1500);

        Toast.makeText(context, "ALARM CALIYOR!", Toast.LENGTH_SHORT).show();




        Bundle extras = intent.getExtras();
        if(extras != null){
            id = extras.getInt("ID");
            baslik = extras.getString("BASLIK");

        }

        NotificationTool notificationHelper = new NotificationTool(context);                    //notification belirt
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(id,baslik);
        nb.setAutoCancel(true);
        notificationHelper.getManager().notify(id,nb.build());



    }



}
