package com.sychev.rss_reader;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    public static final String NIGHT_MODE_SETTINGS_NAME = "NightMode";
    public static final String APP_SETTINGS = "RSSReadSettings";
    public static final String CLEAN_PERIOD_TIME_DISTANCE = "CleanCacheTimePeriod";
    public static final String UPDATE_ON_STARTUP = "UpdateOnStartup";
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
}
