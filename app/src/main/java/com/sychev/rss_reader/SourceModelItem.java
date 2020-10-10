package com.sychev.rss_reader;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;

public class SourceModelItem implements Serializable {

    private String url;
    private String title;
    private NewsModelItem.Categories category;
    private Bitmap icon;
    private String iconUrl;
    private int unreadCount;
    private boolean updated;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NewsModelItem.Categories getCategory() {
        return category;
    }

    public void setCategory(NewsModelItem.Categories category) {
        this.category = category;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
