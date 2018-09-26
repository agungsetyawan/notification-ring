package com.example.agungsetyawan.notif;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
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
    private SharedPreferences sharedPreferences;
    int currentInterruptionFilter;
    int currentRingerMode;
    private String contactName;
    AudioManager audioManager;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i(TAG, "Create");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        currentRingerMode = audioManager.getRingerMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                currentInterruptionFilter = notificationManager.getCurrentInterruptionFilter();
            }
        }
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("Name")) {
            contactName = sharedPreferences.getString("Name", "");
        }
        else {
            contactName = " ";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                currentInterruptionFilter = notificationManager.getCurrentInterruptionFilter();
            }
        }

        sendNotification();
        Log.i(TAG, "NotificationService is running for contact " + contactName);
    }

    public void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Notification Ring")
                .setContentText("NotificationService is running for contact " + contactName);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "New Notification");
        String pack = sbn.getPackageName();
//        int timeCall = 3;

        if (pack.equalsIgnoreCase("com.whatsapp")) {
            String ticker = "";
            String title = "";
            String text = "";

            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            if (extras.getString("android.title") != null ) {
                title = extras.getString("android.title");
            }
            if (extras.getCharSequence("android.text") != null) {
                text = extras.getCharSequence("android.text").toString();
            }

            assert title != null;
            if (!title.equalsIgnoreCase("")) {
                Log.i(TAG, "Package: " + pack);
                Log.i(TAG, "Ticker: " + ticker);
                Log.i(TAG, "Title: " + title);
                Log.i(TAG, "Text: " + text);

                if (title.contains("Missed voice call") || title.contains("missed voice calls")) {
                    if (ticker.equalsIgnoreCase("Missed call from " + contactName) || ticker.equalsIgnoreCase("")) {
                        if (text.contains(contactName)) {
                            ringerModeNormal();
                        }
                    }
                } else if (text.equalsIgnoreCase("Incoming voice call")) {
                    if (title.equalsIgnoreCase(contactName)) {
                        ringerModeNormal();
                    }
                }
            }
//            if (title.contains("missed voice calls")) {
//                String titleFirstString = title.substring(0,1);
//                int i = Integer.valueOf(titleFirstString);
//                if ((ticker.equalsIgnoreCase("Missed call from " + contactName))) {
//                    if (i >= timeCall) {
//                        if (title.equalsIgnoreCase(titleFirstString + " missed voice calls")) {
//                            if (currentRingerMode == 1 && currentInterruptionFilter == 2) {
//                                ringerModeNormal();
//                            } else {
//                                ringerModeNormal();
//                            }
//                        }
//                    }
//                } else {
//                    if (i >= timeCall) {
//                        if (title.equalsIgnoreCase(titleFirstString + " missed voice calls")) {
//                            if (text.contains(contactName)) {
//                                if (currentRingerMode == 1 && currentInterruptionFilter == 2) {
//                                    ringerModeNormal();
//                                } else {
//                                    ringerModeNormal();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("msg", "Notification Removed");
    }

    private void ringerModeNormal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Ring : Normal");
    }
}
