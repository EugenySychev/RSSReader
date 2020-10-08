package com.sychev.rss_reader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    private boolean useDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_activity_toolbar);
        toolbar.setTitle(R.string.action_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadReference();
    }

    private void loadReference() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.getBoolean("DarkTheme", useDarkTheme);
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