package com.sychev.rss_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.collection.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCache {

    private static ImageCache instance;
    private LruCache<Object, Object> lru;
    private File cacheDir;

    private ImageCache() {
        lru = new LruCache<Object, Object>(1024);
        cacheDir = new File("/");
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

            if (cacheDir.getAbsolutePath() != "/") {
                String imageName = key.replace("/", "");
                try (FileOutputStream out = new FileOutputStream(cacheDir.getAbsolutePath() + imageName)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    public Bitmap retrieveBitmapFromCache(String key) {

        try {
            Bitmap bitmap = (Bitmap) ImageCache.getInstance().getLru().get(key);
            if (bitmap == null && cacheDir.getAbsolutePath() != "/") {
                String imageName = key.replace("/", "");
                bitmap = BitmapFactory.decodeFile(cacheDir.getAbsolutePath() + imageName);
                System.out.println("Read from cache " + cacheDir.getAbsolutePath() + imageName);
            }
            return bitmap;
        } catch (Exception e) {
        }
        return null;
    }

    public void setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
    }


}
