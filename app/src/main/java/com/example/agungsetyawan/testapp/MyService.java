package com.example.agungsetyawan.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by agungsetyawan on 3/22/18.
 */

public class MyService extends Service {

    NotificationService notificationService;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationService = new NotificationService();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
