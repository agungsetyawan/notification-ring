package com.example.agungsetyawan.testapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by agungsetyawan on 3/21/18.
 */

public class NotificationService extends NotificationListenerService {

    private static String TAG = NotificationService.class.getSimpleName();
    protected Context context;
    AudioManager audioManager;
    private SharedPreferences sharedPreferences;
    private String contactName;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i(TAG, "Create");

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Name")) {
            contactName = sharedPreferences.getString("Name", "");
        }

        sendNotification();
        Log.i(TAG, "NotificationService is running for contact " + contactName);
    }

    public void sendNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notification Ring")
                .setContentText("NotificationService is running for contact " + contactName);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "notification");
        String pack = sbn.getPackageName();

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

            if (title.contains("missed voice calls")) {
                String titleFirstString = title.substring(0,1);
                int i = Integer.valueOf(titleFirstString);
                if ((ticker.equalsIgnoreCase("Missed call from " + contactName))) {
                    if (i >= 3) {
                        if (title.equalsIgnoreCase(titleFirstString + " missed voice calls")) {
                            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                            Log.i(TAG, "Ring : Normal");
                        }
                    }
                } else {
                    if (i >= 3) {
                        if (title.equalsIgnoreCase(titleFirstString + " missed voice calls")) {
                            if (text.contains(contactName)) {
                                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                                Log.i(TAG, "Ring : Normal");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }
}
