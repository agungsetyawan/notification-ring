package com.example.agungsetyawan.testapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private Switch switch1;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey("5aaf59c8524fab28a187ade7");

        switch1 = findViewById(R.id.switch1);
        switch1.setEnabled(checkPermission());
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.i(TAG, "Ringer Mode " + audioManager.getRingerMode());

        checkRingerMode();
        onCheckedSwitch();
    }

    public void startService(View view) {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    public void stopService(View view) {
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private boolean checkPermission() {
        boolean permission = false;
        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
//                Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                Intent intent2 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent2);
            } else {
                switch1.setEnabled(true);
                permission = true;
            }
        }

        return permission;
    }

    private void checkRingerMode() {
        if ((audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) && (!switch1.isChecked())) {
            switch1.setChecked(true);
            switch1.setText(R.string.normal);
        } else {
            switch1.setChecked(false);
            switch1.setText(R.string.silent);
        }
    }

    public void ringerModeNormal() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
        switch1.setText(R.string.normal);
        Log.i(TAG,"Ring : Normal");
    }

    public void ringerModeSilent() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Toast.makeText(getApplicationContext(), "Silent", Toast.LENGTH_SHORT).show();
        switch1.setText(R.string.silent);
        Log.i(TAG,"Ring : Silent");
    }

    private void onCheckedSwitch() {
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        ringerModeNormal();
                    } else {
                        ringerModeSilent();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClickWhatsApp(View view) {
        PackageManager pm = getPackageManager();

        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }
}
