package com.sychev.rss_reader;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sychev.rss_reader.view.LogViewActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String NIGHT_MODE_SETTINGS_NAME = "NightMode";
    public static final String APP_SETTINGS = "RSSReadSettings";
    public static final String CLEAN_PERIOD_TIME_DISTANCE = "CleanCacheTimePeriod";
    public static final String UPDATE_ON_STARTUP = "UpdateOnStartup";
    private static final String FILE_LOG_NAME = "logdata.txt";
    public static int defaultTimeDistanceCleaning = 30;

    public static String cropTextWithPoints(String source, int length) {
        if (source == null)
            return " ";
        if (source.length() > length) {
            int len = length;
            while (len > 0 && !source.startsWith(" ", len - 1)) len--;

            return source.substring(0, len) + "...";
        }
        return source;
    }

    public static String trimString(String source) {
        int len = source.length();
        int st = 0;

        while ((st < len) && (source.charAt(st) == ' ' || source.charAt(st) == '\n')) {
            st++;
        }
        while ((st < len) && (source.charAt(len - 1) == ' ' || source.charAt(len - 1) == '\n')) {
            len--;
        }
        return ((st > 0) || (len < source.length())) ? source.substring(st, len) : source;
    }

    public static String getTimeString(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

    public static long getTimePeriodFromIndex(int index) {
        switch (index) {
            case 0:
                return 0;
            case 1:
                return 15;
            case 2:
                return 30;
            case 3:
                return 60;
            case 4:
                return 120;
            case 5:
                return 360;
            case 6:
                return 720;
            case 7:
                return 1440;
        }
        return 0;
    }

    public static int getIndexFromUpdatePeriod(long period) {
        if (period > 1000)
            return 7;
        if (period > 500)
            return 6;
        if (period > 200)
            return 5;
        if (period > 100)
            return 4;
        if (period > 50)
            return 3;
        if (period > 25)
            return 2;
        if (period > 0)
            return 1;
        return 0;
    }

    public static String getLogText(Context context) {
        StringBuilder sb = new StringBuilder();
        FileInputStream fIn = null;
        try {
            fIn = context.openFileInput(FILE_LOG_NAME);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader reader = new BufferedReader(isr);

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            isr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

//    public static void addLogText(Context context, String text) {
//        FileOutputStream fOut = null;
//        try {
//            fOut = context.openFileOutput(FILE_LOG_NAME,
//                    Context.MODE_APPEND);
//            OutputStreamWriter osw = new OutputStreamWriter(fOut);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
//            String currentDateandTime = sdf.format(new Date());
//            osw.write(currentDateandTime + ": " + text + "\n");
//            osw.flush();
//            osw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void clearLog(Context context) {
//        FileOutputStream fOut = null;
//        try {
//            fOut = context.openFileOutput(FILE_LOG_NAME, Context.MODE_PRIVATE);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        PrintWriter writer = new PrintWriter(fOut);
//        writer.print("");
//        writer.close();
//    }
}
