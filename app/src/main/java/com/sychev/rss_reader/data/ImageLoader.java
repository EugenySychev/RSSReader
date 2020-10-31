package com.sychev.rss_reader.data;

import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader extends Thread {

    private final NewsModelItem newsModelItem;

    public ImageLoader(NewsModelItem item) {
        newsModelItem = item;
    }

    @Override
    public void run() {
        if (newsModelItem != null) {
            if (newsModelItem.getIcon() == null && newsModelItem.getIconUrl() != null) {
                URL url = null;
                try {
                    url = new URL(newsModelItem.getIconUrl());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (url != null) {
                    try {
                        newsModelItem.setIcon(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                        ImageCache.getInstance().saveBitmapToCahche(newsModelItem.getIconUrl(), newsModelItem.getIcon());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
