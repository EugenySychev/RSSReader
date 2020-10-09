package com.sychev.rss_reader;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    public static final String NIGHT_MODE_SETTINGS_NAME = "NightMode";
    public static final String APP_SETTINGS = "RSSReadSettings";

    public static String cropTextWithPoints(String source, int length) {
        if (source == null)
            return "Some shit";
        if (source.length() > length) {
            int len = length;
            while (len > 0 && !source.substring(len - 1, len).equals(" ")) len--;

            return source.substring(0, len) + "...";
        }
        return source;
    }

    public static String getTimeString(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

}
