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
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;

public class MainActivity extends AppCompatActivity {

    private Switch switch1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CloudRail.setAppKey("5aaf59c8524fab28a187ade7");
        a();
    }


    public void onClickWhatsApp(View view) {

        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void a() {
        //Getting the ToggleButton and Button instance from the layout xml file
        switch1 = findViewById(R.id.switch1);
        textView = findViewById(R.id.textView);
        final AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        final int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);

        // 832837512421000

        //Performing action on button click
        switch1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                onClickWhatsApp(view);
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    Intent intent2 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                    startActivity(intent2);
                }
                else {
                    try {
                        if (switch1.isChecked()) {
                            System.out.println("NORMAL");
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
//                        Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
                            textView.setText("Normal");
                        }
                        else  {
                            System.out.println("SILENT");
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//                        Toast.makeText(getApplicationContext(), "Silent", Toast.LENGTH_SHORT).show();
                            textView.setText("Silent");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }
}
