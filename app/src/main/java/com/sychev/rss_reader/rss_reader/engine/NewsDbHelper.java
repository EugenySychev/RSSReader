package com.sychev.rss_reader.rss_reader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class NewsDbHelper extends SQLiteOpenHelper {
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "NewsTable";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCR = "description";
        public static final String COLUMN_NAME_IMAGE = "image_url";
        public static final String COLUMN_NAME_ISREAD = "is_read";
        public static final String COLUMN_NAME_SOURCE = "source_url";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_URL + " TEXT," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_DESCR + " TEXT," +
                    FeedEntry.COLUMN_NAME_IMAGE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SOURCE + " TEXT," +
                    FeedEntry.COLUMN_NAME_ISREAD + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "News.db";


    public NewsDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}

