package com.example.agungsetyawan.notif;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.CloudRail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private Switch switch1;
    private TextView textView1;
    private TextView textView2;
    AudioManager audioManager;
    NotificationManager notificationManager;

    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    private String contactName = null;
    private String contactNumber = null;

    private SharedPreferences sharedPreferences;
    int currentInterruptionFilter;
    int currentRingerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CloudRail.setAppKey("5aaf59c8524fab28a187ade7");

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        switch1 = findViewById(R.id.switch1);
        switch1.setEnabled(false);
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
            loadData();
        }

        getPermissionToReadUserContacts();
        getPermissionToNotificationListener();
        checkRingerMode();
        onCheckedSwitch();
        startService();
    }

    public void onClickPermissionAutoStart(View view) {
        getPermissionAutoStart();
    }

    private void getPermissionAutoStart() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;

            Log.i("Manufacturer", manufacturer);

            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            }

            List<ResolveInfo> list = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                this.startActivity(intent);
            }
        } catch (Exception e) {
            Log.d("Error", "" + e.toString());
        }
    }

    public void startService() {
        Intent intent = new Intent(this, MyNotificationService.class);
        Intent intent2 = new Intent(this, NotificationService.class);
        startService(intent);
        startService(intent2);
    }

    private void getPermissionToNotificationListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "Ringer Mode = " + currentRingerMode);
            Log.i(TAG, "currentInterruptionFilter = " + currentInterruptionFilter);

            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent);
            } else {
                switch1.setEnabled(true);
            }
        }
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

    private void ringerModeNormal() {
        if (currentRingerMode == 1 && currentInterruptionFilter == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
            Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ring : Normal");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
            Toast.makeText(getApplicationContext(), "Normal", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ring : Normal");
        }
    }

    private void ringerModeSilent() {
        if (currentRingerMode == 1 && currentInterruptionFilter == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getApplicationContext(), "Silent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ring : Silent");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS);
            }
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(getApplicationContext(), "Silent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Ring : Silent");
        }
    }

    public void ringerModeVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        }
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        Toast.makeText(getApplicationContext(), "Vibrate", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Ring : Vibrate");
    }

    public void onClickRingerModeVibrate(View view) {
        ringerModeVibrate();
    }

    private void onCheckedSwitch() {
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        ringerModeNormal();
                        switch1.setText(R.string.normal);
                    } else {
                        ringerModeSilent();
                        switch1.setText(R.string.silent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClickSettingButton(View view) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        }
        startActivity(intent);
    }

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts

    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    // Show our own UI to explain to the user why we need to read the contacts
                    // before actually requesting the permission and showing the default UI
                }
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSIONS_REQUEST);
                requestPermissions(new String[]{Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE}, READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClickSelectContact(View btnSelectContact) {
        getPermissionToReadUserContacts();
        loadData();

        // using native contacts selection
        // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
//            retrieveContactPhoto();
            saveData();
            startService();
        }
    }

    private void retrieveContactPhoto() {
        Bitmap photo;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
//                ImageView imageView = findViewById(R.id.img_contact);
//                imageView.setImageBitmap(photo);
                inputStream.close();
            } else {
//                ImageView imageView = findViewById(R.id.img_contact);
//                imageView.setImageResource(R.mipmap.ic_launcher);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void retrieveContactNumber() {

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID != null && cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            cursorID.close();
        }


        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone != null && cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            cursorPhone.close();
        }

        textView2.setText(contactNumber);
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    private void retrieveContactName() {

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }

        textView1.setText(contactName);
        Log.d(TAG, "Contact Name: " + contactName);
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", contactName);
        editor.putString("Number", contactNumber);
        editor.apply();
    }

    public void loadData() {
        contactName = sharedPreferences.getString("Name", "");
        contactNumber = sharedPreferences.getString("Number", "");
        textView1.setText(contactName);
        textView2.setText(contactNumber);
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
