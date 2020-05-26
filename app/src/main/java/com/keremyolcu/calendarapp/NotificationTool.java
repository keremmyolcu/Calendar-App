package com.keremyolcu.calendarapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationTool extends ContextWrapper {
    public static final String channelID = "CHANNEL_ID";
    public static final String channelName = "CHANNEL_NAME";
    private NotificationManager notificationManager;



    public NotificationTool(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(){
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationCompat.Builder getChannelNotification(int pendid, String baslik){
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(baslik)
                .setContentText(baslik + " etkinligini unutmayin :)")
                .setSmallIcon(R.drawable.ic_alert_ikon);
    }


    public NotificationManager getManager() {
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
