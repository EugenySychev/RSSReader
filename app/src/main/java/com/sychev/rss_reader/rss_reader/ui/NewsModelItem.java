package com.sychev.rss_reader.rss_reader;

import android.graphics.Bitmap;

import java.io.Serializable;

public class NewsModelItem implements Serializable {
    public Categories getmCategory() {
        return mCategory;
    }

    public void setmCategory(Categories mCategory) {
        this.mCategory = mCategory;
    }

    public enum Categories {
        NEWS_CATEGORY,
        FILMS_CATEGORY,
        OTHER_CATEGORY
    }

    private String mTitle;
    private String mDescription;
    private Bitmap mIcon;
    private String mUrl;
    private int mId;
    private boolean mIsRead;
    private Categories mCategory;


    public NewsModelItem(String title, String description) {
        mTitle = title;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean getIsRead() {
        return mIsRead;
    }

    public void setIsRead(boolean mIsRead) {
        this.mIsRead = mIsRead;
    }

    public Categories getCategory() {
        return mCategory;
    }

    public void setCategory(Categories cat) {
        mCategory = cat;
    }
}
