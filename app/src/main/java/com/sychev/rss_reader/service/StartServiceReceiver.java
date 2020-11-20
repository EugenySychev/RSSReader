package com.sychev.rss_reader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class StartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "StartReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Rec start job RSS reader", Toast.LENGTH_LONG).show();
        UpdateWorker.setContext(context);
        UpdateWorker.enqueueSelf();
    }
}
