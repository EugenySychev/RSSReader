package com.sychev.rss_reader.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sychev.rss_reader.Utils;
import com.sychev.rss_reader.data.NewsListLoader;

import java.util.concurrent.TimeUnit;

public class UpdateWorker extends Worker {
    private static final String uniqueWorkName = "com.sychev.rss_reader.UpdateWorker";
    private static final long repeatIntervalMin = 15;
    private static final long flexIntervalMin = 5;
    private static Context context;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static void setContext(Context context) {
        UpdateWorker.context = context;
    }

    private static PeriodicWorkRequest getOwnWorkRequest() {
        return new PeriodicWorkRequest.Builder(
                UpdateWorker.class, repeatIntervalMin, TimeUnit.MINUTES, flexIntervalMin, TimeUnit.MINUTES
        ).build();
    }

    public static void enqueueSelf() {
//        Utils.addLogText(context,"Enqueue worker ");
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(uniqueWorkName, ExistingPeriodicWorkPolicy.KEEP, getOwnWorkRequest());
    }

    public static void runOnce() {
        WorkRequest request = new OneTimeWorkRequest.Builder(UpdateWorker.class).build();
        WorkManager.getInstance(context).enqueue(request);
    }

    @SuppressLint("RestrictedApi")
    public Worker.Result doWork() {
//        Utils.addLogText(context,"Call do work");

        if (!NewsListLoader.getInstance().isReady())
            NewsListLoader.getInstance().init(context);
        NewsListLoader.getInstance().requestUpdateAllNewsTimer();
        return new Result.Success();
    }
}