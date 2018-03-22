package com.example.agungsetyawan.testapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by agungsetyawan on 3/20/18.
 */

public class MyNotificationListener extends NotificationListenerService {
    public static String TAG = MyNotificationListener.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        System.out.println(sbn.getId());
        System.out.println(sbn.getPackageName());
        System.out.println(sbn.getNotification().tickerText);
        Log.i(TAG, "ID:" + sbn.getId());
        Log.i(TAG, "Posted by:" + sbn.getPackageName());
        Log.i(TAG, "tickerText:" + sbn.getNotification());

//        for (String key : sbn.getNotification().extras.keySet()) {
//            Log.i(TAG, key + "=" + sbn.getNotification().extras.get(key).toString());
//            System.out.println(sbn.getNotification().extras.get(key).toString());
//        }
    }
}
