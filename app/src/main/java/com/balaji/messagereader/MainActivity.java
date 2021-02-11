package com.balaji.messagereader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public Switch speak;
    public TextToSpeech tts;
    private String starttxt = "Okay! I will read your messages out loud for you now.";
    private  String stoptxt = "Okay! I will stay silent now";
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Button changeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        speak = (Switch) findViewById(R.id.speakSwitch);
        changeSettings = (Button) findViewById(R.id.settings);
        tts = new TextToSpeech(this,null);
        tts.setLanguage(Locale.getDefault());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean silent = settings.getBoolean("switchkey", false);
        speak.setChecked(silent);
        speak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tts.speak(starttxt,TextToSpeech.QUEUE_ADD,null);
                    startService(new Intent(MainActivity.this, MyService.class));
//                    Intent intent1 = new Intent(MainActivity.this, Speaker.class);
//                    intent1.putExtra("Speed", speed);
//                    intent1.putExtra("Pitch", pitch);
//                    startService(intent1);
                }
                else{
                    tts.speak(stoptxt,TextToSpeech.QUEUE_ADD,null);
                    stopService(new Intent(MainActivity.this, MyService.class));
                    stopService(new Intent(MainActivity.this, Speaker.class));
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
                }
            });

        changeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingintent = new Intent();
                settingintent.setAction("com.android.settings.TTS_SETTINGS");
                settingintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingintent);
            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionReadMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        int PermissionReadContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionReceiveMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (PermissionReadContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissionReadMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionReceiveMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
