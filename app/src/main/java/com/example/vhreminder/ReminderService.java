package com.example.vhreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ReminderService extends Service {
    final String LOG_TAG = "myLogs";
    String notePendingIntent = "";
    String time = "";
    boolean isCycleWorking = true;
    boolean isHeadUpNotification = true;
    NotificationCompat.Builder builder;

    public ReminderService() {
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "SERVICE : onCreate");
        super.onCreate();
        //NotificationCompat.Builder builder =
        builder = new NotificationCompat.Builder(this)
                .setContentTitle("ReminderService")
                .setContentText("is started...")
                .setPriority(NotificationCompat.PRIORITY_MIN);
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Log.d(LOG_TAG, "SERVICE : onStartCommand");
        //createNotification("ReminderService", "is running", 1,false, false);

        final String note = intent.getStringExtra("note");
        final String time = intent.getStringExtra("time");

        notePendingIntent = note;

        Log.d(LOG_TAG, "TIME = " + time);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCycleWorking){
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String currentTime = getCurrentTime();
                    Log.d(LOG_TAG, "currentTime = " + currentTime);
                    if(time.equals(currentTime)){
                        Log.d(LOG_TAG, "EQUALS, startId = " + startId);
                        Intent intent = new Intent(ReminderService.this, MainActivity.class);
                        intent.putExtra("note", note);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(intent);
                        isCycleWorking = false;
                        createNotification("Notification", note, 1, true, false);
                        //stopForeground(true);
                        //stopSelf(startId);
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Log.d(LOG_TAG, "SERVICE : onDestroy");
        //createNotification("ReminderService ", "is destroyed", 1,true, false);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String getCurrentTime(){
        //Log.d(LOG_TAG, "ReminderService : getCurrentTime");

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));

        return simpleDateFormat.format(calendar.getTime());
    }

    void createNotification(String title, String text, int id, boolean flagAutoCancel, boolean isHeadUpNotification2){
        //NotificationCompat.Builder builder =
        builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (!isHeadUpNotification2){
            runnableDefaultsAll.run();
        }
        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        //stopForeground(false);
        //stopSelf();

    }

    Runnable runnableDefaultsAll = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "runnableDefaultsAll");
            Intent intent = new Intent(ReminderService.this, MainActivity.class);
            Intent intent1 = new Intent();
            intent.putExtra("note", notePendingIntent);
            PendingIntent pendingIntentShow =
                    PendingIntent.getActivity(ReminderService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntentClose =
                    PendingIntent.getActivity(ReminderService.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.addAction(android.R.drawable.star_on, "show", pendingIntentShow);
            builder.addAction(android.R.drawable.star_on, "close", pendingIntentClose);
            builder.setAutoCancel(true);
            builder.setOngoing(true);
        }
    };
}
