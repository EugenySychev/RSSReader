package com.sychev.rss_reader.rss_reader;

import android.graphics.Bitmap;

import androidx.collection.LruCache;

public class ImageCache {

    private static ImageCache instance;
    private LruCache<Object, Object> lru;

    private ImageCache() {

        lru = new LruCache<Object, Object>(1024);

    }

    public static ImageCache getInstance() {

        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;

    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }

    public void saveBitmapToCahche(String key, Bitmap bitmap) {

        try {
            ImageCache.getInstance().getLru().put(key, bitmap);
        } catch (Exception e) {
        }
    }

    public Bitmap retrieveBitmapFromCache(String key) {

        try {
            Bitmap bitmap = (Bitmap) ImageCache.getInstance().getLru().get(key);
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }

}
