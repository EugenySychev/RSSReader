package com.sychev.rss_reader.view;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private boolean useDarkTheme;
    private SharedPreferences preferences;
    private int timeDistance;
    private boolean updateStartupEnabled;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        int nightMode = getSharedPreferences(Utils.APP_SETTINGS, 0).getInt(Utils.NIGHT_MODE_SETTINGS_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences(Utils.APP_SETTINGS, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_activity_toolbar);
        toolbar.setLayoutTransition(new LayoutTransition());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadReference();

        final SwitchCompat switchCompat = findViewById(R.id.dark_theme_switcher);
        switchCompat.setChecked(useDarkTheme);
        final Activity activity = this;
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useDarkTheme = switchCompat.isChecked();

                int nightMode = getSharedPreferences(Utils.APP_SETTINGS, 0).getInt(Utils.NIGHT_MODE_SETTINGS_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                AppCompatDelegate.setDefaultNightMode(nightMode);
                restartActivity(activity);
                updateReference();
            }
        });


        final SwitchCompat switchCompatUpdate = findViewById(R.id.update_startup_switcher);
        switchCompatUpdate.setChecked(updateStartupEnabled);

        switchCompatUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateStartupEnabled = switchCompatUpdate.isChecked();
                updateReference();
            }
        });

        EditText editText = findViewById(R.id.timePeriodDays);
        String timeDistanceString = String.valueOf(timeDistance);
        editText.setText(timeDistanceString);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    timeDistance = Integer.parseInt(v.getText().toString());
                    updateReference();
                }
                return false;
            }
        });
    }

    public void restartActivity(Activity activity) {
        Intent i = getIntent();
        activity.overridePendingTransition(0, 0);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        //restart the activity without animation
        activity.overridePendingTransition(0, 0);
        activity.startActivity(i);
    }

    private void updateReference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Utils.NIGHT_MODE_SETTINGS_NAME, useDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        editor.putInt(Utils.CLEAN_PERIOD_TIME_DISTANCE, timeDistance);
        editor.putBoolean(Utils.UPDATE_ON_STARTUP, updateStartupEnabled);
        editor.apply();

    }

    private void loadReference() {
        useDarkTheme = preferences.getInt(Utils.NIGHT_MODE_SETTINGS_NAME, AppCompatDelegate.MODE_NIGHT_NO) == AppCompatDelegate.MODE_NIGHT_YES;
        timeDistance = preferences.getInt(Utils.CLEAN_PERIOD_TIME_DISTANCE, Utils.defaultTimeDistanceCleaning);
        updateStartupEnabled = preferences.getBoolean(Utils.UPDATE_ON_STARTUP, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


}