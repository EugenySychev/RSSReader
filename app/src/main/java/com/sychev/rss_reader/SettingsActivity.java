package com.sychev.rss_reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;

import java.util.Objects;
import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    private static final String APP_PREFERENCES = "Settings";
    private boolean useDarkTheme;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_activity_toolbar);
        toolbar.setTitle(R.string.action_settings);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadReference();

        final SwitchCompat switchCompat = findViewById(R.id.dark_theme_switcher);
        switchCompat.setChecked(useDarkTheme);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useDarkTheme = switchCompat.isChecked();
                updateReference();
            }
        });
    }

    private void updateReference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DarkTheme", useDarkTheme);
        editor.commit();

    }

    private void loadReference() {
        useDarkTheme = preferences.getBoolean("DarkTheme", false);
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