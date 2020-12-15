package com.sychev.rss_reader.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sychev.rss_reader.MainActivity;
import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;
import com.sychev.rss_reader.data.NewsListLoader;

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
        final AppCompatActivity activity = this;
        Thread thread = new Thread(){
            @Override
            public void run() {
                NewsListLoader.getInstance().init(activity);
                NewsListLoader.getInstance().setOnlyNotRead(true);
                NewsListLoader.getInstance().getAllNewsFromDB(24 * 60 * 60 * 1000);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        thread.start();
        mTextView = (TextView) findViewById(R.id.text);

    }
}