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

}
