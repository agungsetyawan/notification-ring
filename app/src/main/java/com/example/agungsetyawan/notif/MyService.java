package com.example.agungsetyawan.notif;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by agungsetyawan on 3/22/18.
 */

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        Toast.makeText(getApplicationContext(), "this is service ", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartIntentService = new Intent(getApplicationContext(), this.getClass());
        restartIntentService.setPackage(getPackageName());
        startService(restartIntentService);
        super.onTaskRemoved(rootIntent);
    }
}
