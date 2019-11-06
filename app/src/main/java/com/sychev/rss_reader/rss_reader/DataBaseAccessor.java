package com.sychev.rss_reader.rss_reader;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.sql.Array;
import java.util.List;
import java.util.Map;


public class NewsReaderDBContract {
    private  NewsReaderDBContract() {}

    public static class NewsSourceDBEntry implements BaseColumns {
        public static final String TABLE_NAME_SOURCE = "NEWS_SOURCES_TABLE";
        public static final String COLUMN_NAME_SOURCE = "NEWS_SOURCE_TITLE";
        public static final String COLUMN_URI_SOURCE = "NEWS_SOURCE_URI";
        public static final String COLUMN_LDATE_SOURCE = "NEWS_SOURCE_LASTDATE";
    }

    public static class NewsDBEntry implements BaseColumns {
        public static final String TABLE_NAME_NEWS = "NEWS_ITEMS_TABLE";
        public static final String COLUMN_NAME_NEWS_TITLE = "NEWS_TITLE";

    }
}

public class NewsDbHelper extends SQLiteOpenHelper
{
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

}

public class DataBaseAccessor {
    private static String TABLE_NAME = "";
    SQLiteDatabase database;
    public void InsertUriList(List<String> stringList)
    {
        TABLE_NAME = "UriNewsList";
        String INSERT_QUERY = "insert into " + TABLE_NAME + " (ID,URI) values (?, ?)";
        try {

        }

    }

}
