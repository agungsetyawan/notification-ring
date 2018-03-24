package com.example.agungsetyawan.testapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by agungsetyawan on 3/21/18.
 */

public class NotificationService extends NotificationListenerService {

    private static String TAG = NotificationService.class.getSimpleName();
    protected Context context;
    AudioManager audioManager;
    MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private String contactName;
    private String contactNumber;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mainActivity = new MainActivity();
        Log.i("NotificationService", "Create");

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Name")) {
            loadData();
        }

        sendNotification();
        Log.i(TAG, "ContactName: " + contactName);
    }

    public void loadData() {
        contactName = sharedPreferences.getString("Name", "");
        contactNumber = sharedPreferences.getString("Number", "");
    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notification Ring")
                .setContentText("Service is running for contact " + contactName);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Log.i(TAG, "ada notif baru!");

        if (pack.equalsIgnoreCase("com.whatsapp")) {

            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }

            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();

            Log.i(TAG, "Package: " + pack);
            Log.i(TAG, "Ticker: " + ticker);
            Log.i(TAG, "Title: " + title);
            Log.i(TAG, "Text: " + text);

            if ((ticker.equalsIgnoreCase("Missed call from Ageng Mirum")) || (ticker.equalsIgnoreCase("Missed call from " + contactName))) {
                if (title.equalsIgnoreCase("3 missed voice calls")) {
                    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Ring : Normal");
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}
