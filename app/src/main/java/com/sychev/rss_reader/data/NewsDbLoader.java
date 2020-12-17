package com.sychev.rss_reader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsDbLoader {
    NewsDbHelper dbHelper;
    List<SourceModelItem> sourceModelItems = new ArrayList<>();
    List<NewsModelItem> newsModelItems = new ArrayList<>();

    public NewsDbLoader(Context context) {
        dbHelper = new NewsDbHelper(context);
    }

    public boolean storeList(List<NewsModelItem> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (NewsModelItem item : list) {
            ContentValues values = new ContentValues();

            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_TITLE, item.getTitle());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_DESCR, item.getDescription());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_IMAGE, item.getIconUrl());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE, item.getSource());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_TIME, item.getTime());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_URL, item.getUrl());
            values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD, item.getIsRead());

            long newRowId = db.insert(NewsDbHelper.FeedEntry.TABLE_NAME, null, values);
            if (newRowId < 0)
                return false;
        }
        return true;
    }

    public boolean setItemIsRead(NewsModelItem item, boolean isRead) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD, isRead);
        String selection = NewsDbHelper.FeedEntry.COLUMN_NAME_URL + " LIKE ?";
        String[] selectionArgs = {item.getUrl()};

        int count = db.update(NewsDbHelper.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
        return count > 0;
    }

    private NewsModelItem getCursorItem(Cursor cursor) {
        NewsModelItem item = new NewsModelItem();
        item.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_IMAGE)));
        item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_DESCR)));
        item.setSource(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE)));
        item.setIsRead(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD)));
        item.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_TIME)));
        item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_TITLE)));
        item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_URL)));
        if (item.getIconUrl() != null) {
            Bitmap loadedBitmap = ImageCache.getInstance().retrieveBitmapFromCache(item.getIconUrl());
            if (loadedBitmap != null) {
                item.setIcon(loadedBitmap);
            } else {
                ImageLoader loader = new ImageLoader(item);
                loader.start();
                try {
                    loader.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return item;
    }

    public List<NewsModelItem> getFullNewsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        newsModelItems.clear();
        Cursor cursor = db.query(NewsDbHelper.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            newsModelItems.add(getCursorItem(cursor));
        }
        cursor.close();
        return newsModelItems;
    }

    public List<NewsModelItem> getNewsListForSourceAndTime(String sourceUrl, long begin, long end) {
        return getNewsListForSourceAndTime(sourceUrl, begin, end, false);
    }

    public List<NewsModelItem> getNewsListForSourceAndTime(String sourceUrl, long begin, long end, boolean onlyNotRead) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<NewsModelItem> list = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                NewsDbHelper.FeedEntry.COLUMN_NAME_DESCR,
                NewsDbHelper.FeedEntry.COLUMN_NAME_URL,
                NewsDbHelper.FeedEntry.COLUMN_NAME_TITLE,
                NewsDbHelper.FeedEntry.COLUMN_NAME_DESCR,
                NewsDbHelper.FeedEntry.COLUMN_NAME_TIME,
                NewsDbHelper.FeedEntry.COLUMN_NAME_IMAGE,
                NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD,
                NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE
        };

        String selection = NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE + " = ?";
        String[] selectionArgs = {sourceUrl};

        String sortOrder =
                NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " DESC";

        if (onlyNotRead) {
            selection += " AND " + NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD + " = ? ";
            selectionArgs = new String[]{sourceUrl, " 0 "};
        }
        if (begin > 0 && end > 0) {
            selection += " AND " + NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " >= ? AND " +
                    NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " <= ? ";
            selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 2);
            selectionArgs[selectionArgs.length - 2] = String.valueOf(begin);
            selectionArgs[selectionArgs.length - 1] = String.valueOf(end);
        }


        Cursor cursor = db.query(
                NewsDbHelper.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while (cursor.moveToNext()) {
            list.add(getCursorItem(cursor));
        }

        cursor.close();
        return list;
    }

    public List<SourceModelItem> getListSource() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        sourceModelItems.clear();
        Cursor cursor = db.query(NewsDbHelper.SourceEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            SourceModelItem item = new SourceModelItem();
            item.setCategory(NewsListLoader.Categories.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY))));
            item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_URL)));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE)));
            item.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_ICON_URL)));
            item.setLastUpdated(cursor.getLong(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_LAST_UPDATED)));
            item.setUpdateOnlyWifi(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_WIFI_ONLY)) > 0);
            item.setUpdateTimePeriod(cursor.getLong(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_PERIOD)));
            item.setShowNotifications(cursor.getLong(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_SHOW_NOTIFICATION)) > 0);
            if (item.getTitle() == null)
                item.setTitle(item.getUrl());
            if (item.getIconUrl() != null) {
                Bitmap loadedBitmap = ImageCache.getInstance().retrieveBitmapFromCache(item.getIconUrl());
                if (loadedBitmap != null) {
                    item.setIcon(loadedBitmap);
                }
            }
            item.setUpdated(true);
            sourceModelItems.add(item);
        }
        cursor.close();
        return sourceModelItems;
    }

    public boolean addSource(SourceModelItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE, item.getTitle());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY, NewsListLoader.Categories.toInt(item.getCategory()));
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_URL, item.getUrl());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_ICON_URL, item.getIconUrl());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_LAST_UPDATED, item.getLastUpdated());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_WIFI_ONLY, item.isUpdateOnlyWifi());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_PERIOD, item.getUpdateTimePeriod());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_SHOW_NOTIFICATION, item.isShowNotifications());

        long newRowId = db.insert(NewsDbHelper.SourceEntry.TABLE_NAME, null, values);
        sourceModelItems.add(item);
        return newRowId >= 0;
    }

    public boolean removeSource(SourceModelItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = NewsDbHelper.SourceEntry.COLUMN_NAME_URL + " LIKE ?";

        String[] selectionArgs = {item.getUrl()};
        sourceModelItems.remove(item);
        return db.delete(NewsDbHelper.SourceEntry.TABLE_NAME, selection, selectionArgs) >= 0;
    }

    public boolean removeNews(SourceModelItem source) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE + " LIKE ?";

        String[] selectionArgs = {source.getUrl()};

        return db.delete(NewsDbHelper.FeedEntry.TABLE_NAME, selection, selectionArgs) >= 0;
    }

    public boolean updateSource(SourceModelItem source) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE, source.getTitle());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY, NewsListLoader.Categories.toInt(source.getCategory()));
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_URL, source.getUrl());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_ICON_URL, source.getIconUrl());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_LAST_UPDATED, source.getLastUpdated());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_WIFI_ONLY, source.isUpdateOnlyWifi());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_UPDATE_PERIOD, source.getUpdateTimePeriod());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_SHOW_NOTIFICATION, source.isShowNotifications());

        String selection = NewsDbHelper.SourceEntry.COLUMN_NAME_URL + " LIKE ?";
        String[] selectionArgs = {source.getUrl()};
        long newRowId = db.update(NewsDbHelper.SourceEntry.TABLE_NAME, values, selection, selectionArgs);

        return newRowId > 0;
    }

}
