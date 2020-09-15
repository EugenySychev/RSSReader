package com.sychev.rss_reader;;

import android.graphics.Bitmap;

import java.io.Serializable;

public class NewsModelItem implements Serializable {

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
    private long mTime;
    private int mIsRead;
    private Categories mCategory;
    private String mIconUrl;
    private String mSource;

    public NewsModelItem(String title, String description) {
        mTitle = title;
        mDescription = description;
    }

    public NewsModelItem() {}

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

    public int getIsRead() {
        return mIsRead;
    }

    public void setIsRead(int mIsRead) { this.mIsRead = mIsRead; }

    public Categories getCategory() {
        return mCategory;
    }

    public void setCategory(Categories cat) {
        mCategory = cat;
    }

    public void setTime(long timeMils) { mTime = timeMils; }

    public long getTime() { return mTime; }

    public String getIconUrl() { return mIconUrl; }

    public String getSource() { return mSource; }

    public void setSource(String source) { mSource = source; }

    public void setIconUrl(String iconUrl) { mIconUrl = iconUrl; }


}
