package com.sychev.rss_reader;

import java.io.Serializable;

public class SourceModelItem implements Serializable {

    private String url;
    private String title;
    private NewsModelItem.Categories category;

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
}
