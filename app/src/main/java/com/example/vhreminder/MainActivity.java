package com.example.vhreminder;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editTextNote;
    EditText editTextTime;
    Button buttonOk;
    Button buttonStopService;
    Button buttonTime;
    Button buttonNotification;
    TextView textView;
    int myHourOfDay = 0;
    int myMinute = 0;
    int dialogId = 1001;
    String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "MainActivity : onCreate");

        editTextNote = findViewById(R.id.editTextNote);
        editTextTime = findViewById(R.id.editTextTime);
        editTextTime.setVisibility(View.INVISIBLE);
        textView = findViewById(R.id.textView);

        buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(this);

        buttonStopService = findViewById(R.id.buttonStopService);
        buttonStopService.setOnClickListener(this);
        buttonStopService.performClick();

        buttonTime = findViewById(R.id.buttonTime);
        buttonTime.setOnClickListener(this);

        buttonNotification = findViewById(R.id.buttonNotification);
        buttonNotification.setOnClickListener(this);

        textView.setText(getCurrentTime());

        try {
            String s = getIntent().getExtras().getString("note");
            editTextNote.setText(s);
            Log.d(LOG_TAG, "s = " + s);
        }catch (NullPointerException n){}

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true){
                        runOnUiThread(runnable);
                        TimeUnit.SECONDS.sleep(1);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "MainActivity : onClick");
        switch (v.getId()){
            case R.id.buttonOk : {
                Log.d(LOG_TAG, "MainActivity : onClick : buttonOk");
                Intent intent = new Intent();
                intent.putExtra("note", editTextNote.getText().toString());
                intent.putExtra("time", editTextTime.getText().toString());
                intent.setAction("ReminderService");
                intent.setPackage("com.example.vhreminder");
                startService(intent);
                finish();
                break;
            }

            case R.id.buttonStopService : {
                Log.d(LOG_TAG, "MainActivity : onClick : buttonStopService");
                Intent intent = new Intent();
                intent.setAction("ReminderService");
                intent.setPackage("com.example.vhreminder");
                stopService(intent);
                break;
            }

            case R.id.buttonTime : {
                Log.d(LOG_TAG, "MainActivity : onClick : buttonTime");
                showDialog(dialogId);
                break;
            }

            case R.id.buttonNotification : {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("TempNotification")
                                .setContentText("is showing now...")
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                Notification notification = builder.build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
                break;
            }
        }
    }

    public String getCurrentTime(){
        //Log.d(LOG_TAG, "MainActivity : getCurrentTime");
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        String result = simpleDateFormat.format(calendar.getTime());

        return result;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(LOG_TAG, "MainActivity : onCreateDialog : dialogId = " + id);
        if(id == 1001){
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, 12, 12, true);
            return timePickerDialog;
        }

        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHourOfDay = hourOfDay;
            myMinute = minute;
            String resultHour = String.valueOf(myHourOfDay);
            String resultMunite = String.valueOf(myMinute);

            if(myHourOfDay < 10){
                resultHour = "0" + myHourOfDay;
            }

            if(myMinute < 10){
                resultMunite = "0" + myMinute;
            }

            String result = resultHour + ":" + resultMunite + ":00";
            Log.d(LOG_TAG, "RESULT = " + result);
            editTextTime.setText(result);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            textView.setText(getCurrentTime());
        }
    };
}