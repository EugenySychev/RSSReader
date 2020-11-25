package com.sychev.rss_reader.view;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sychev.rss_reader.MainActivity;
import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;

public class SplashActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int nightMode = getSharedPreferences(Utils.APP_SETTINGS, 0).getInt(Utils.NIGHT_MODE_SETTINGS_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = findViewById(R.id.splashImage);
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES)
            imageView.setImageResource(R.mipmap.ic_app_logo);
        else
            imageView.setImageResource(R.mipmap.ic_app_logo_white);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        mTextView = (TextView) findViewById(R.id.text);

    }
}