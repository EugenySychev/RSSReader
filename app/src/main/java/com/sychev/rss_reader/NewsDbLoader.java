package com.sychev.rss_reader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class NewsDbLoader {
    NewsDbHelper dbHelper;

    public boolean storeList(List<NewsModelItem> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (NewsModelItem item: list) {
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

    public boolean setItemIsRead(String url, boolean isRead) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD, isRead);
        String selection = NewsDbHelper.FeedEntry.COLUMN_NAME_URL + " LIKE ?";
        String[] selectionArgs = { url };

        int count = db.update(NewsDbHelper.FeedEntry.TABLE_NAME, values, selection, selectionArgs);
        return count > 0;
    }

    public List<NewsModelItem> getFullNewsList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<NewsModelItem> list = new ArrayList<>();
        Cursor cursor = db.query(NewsDbHelper.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            NewsModelItem item = new NewsModelItem();
            item.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_IMAGE)));
            item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_DESCR)));
            item.setSource(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_SOURCE)));
            item.setIsRead(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_ISREAD)));
            item.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_TIME)));
            item.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_TITLE)));
            item.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.FeedEntry.COLUMN_NAME_URL)));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    public List<NewsModelItem> getNewsListForSourceAndTime(String sourceUrl, long begin, long end) {
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
        String[] selectionArgs = { sourceUrl };

        String sortOrder =
                NewsDbHelper.FeedEntry.COLUMN_NAME_TIME + " DESC";
        // TODO: add some logic. You should.
        Cursor cursor = db.query(
                NewsDbHelper.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        return list;
    }

    public List<SourceModelItem> getSourceList() {
        List<SourceModelItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(NewsDbHelper.SourceEntry.TABLE_NAME, null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            SourceModelItem item = new SourceModelItem();
            item.setCategory(NewsModelItem.Categories.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_CATEGORY))));
            item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_URL)));
            item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NewsDbHelper.SourceEntry.COLUMN_NAME_TITLE)));
            list.add(item);
        }
        return list;
    }
}
