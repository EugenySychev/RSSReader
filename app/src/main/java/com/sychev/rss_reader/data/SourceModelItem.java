package com.sychev.rss_reader.data;

import android.graphics.Bitmap;

import java.io.Serializable;

public class SourceModelItem implements Serializable {

    private String url;
    private String title;
    private NewsListLoader.Categories category;
    private Bitmap icon;
    private String iconUrl;
    private int unreadCount;
    private boolean updated;
    private long lastUpdated;
    private boolean updateOnlyWifi;
    private long updateTimePeriod;
    private boolean showNotifications;

    private long lastDigestTime;

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

    public NewsListLoader.Categories getCategory() {
        return category;
    }

    public void setCategory(NewsListLoader.Categories category) {
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

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isUpdateOnlyWifi() {
        return updateOnlyWifi;
    }

    public void setUpdateOnlyWifi(boolean updateOnlyWifi) {
        this.updateOnlyWifi = updateOnlyWifi;
    }

    public long getUpdateTimePeriod() {
        return updateTimePeriod;
    }

    public void setUpdateTimePeriod(long updateTimePeriod) {
        this.updateTimePeriod = updateTimePeriod;
    }

    public boolean isShowNotifications() {
        return showNotifications;
    }

    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    public long getLastDigestTime() {
        return lastDigestTime;
    }

    public void setLastDigestTime(long lastDigestTime) {
        this.lastDigestTime = lastDigestTime;
    }
}
