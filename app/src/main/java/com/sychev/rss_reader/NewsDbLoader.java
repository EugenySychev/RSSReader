package com.sychev.rss_reader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class NewsDbLoader {
    NewsDbHelper dbHelper;

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
            }
        }
        return item;
    }

    public List<NewsModelItem> getFullNewsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<NewsModelItem> list = new ArrayList<>();
        Cursor cursor = db.query(NewsDbHelper.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            list.add(getCursorItem(cursor));
        }
        cursor.close();
        return list;
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
        String havingFilter = "";
        if (begin > 0 && end > 0)
            havingFilter = NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " > " + String.valueOf(begin) + " AND " +
                NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " < " + String.valueOf(end);

        if (onlyNotRead) {
            selection += " AND " + NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD + " = ? ";
            selectionArgs = new String[]{sourceUrl, " 0 "};
        }

        Cursor cursor = db.query(
                NewsDbHelper.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                havingFilter,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while (cursor.moveToNext()) {
            list.add(getCursorItem(cursor));
        }

        cursor.close();
        return list;
    }

    public List<SourceModelItem> getListSource() {
        List<SourceModelItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(NewsDbHelper.SourceEntry.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            SourceModelItem item = new SourceModelItem();
            item.setCategory(NewsModelItem.Categories.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY))));
            item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_URL)));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE)));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    public boolean addSource(SourceModelItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE, item.getTitle());
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY, NewsModelItem.Categories.toInt(item.getCategory()));
        values.put(NewsDbHelper.SourceEntry.COLUMN_NAME_URL, item.getUrl());

        long newRowId = db.insert(NewsDbHelper.SourceEntry.TABLE_NAME, null, values);

        return newRowId >= 0;
    }

    public boolean removeSource(SourceModelItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = NewsDbHelper.SourceEntry.COLUMN_NAME_URL + " LIKE ?";

        String[] selectionArgs = { item.getUrl() };
        return db.delete(NewsDbHelper.SourceEntry.TABLE_NAME, selection, selectionArgs) >= 0;
    }

    public boolean removeNews(SourceModelItem source) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE + " LIKE ?";

        String[] selectionArgs = { source.getUrl() };
        return db.delete(NewsDbHelper.FeedEntry.TABLE_NAME, selection, selectionArgs) >= 0;
    }
}
