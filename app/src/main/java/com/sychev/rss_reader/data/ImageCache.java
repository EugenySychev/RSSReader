package com.sychev.rss_reader.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.collection.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ImageCache {

    private static ImageCache instance;
    private final LruCache<Object, Object> lru;
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
                String imageName = cacheDir.getAbsolutePath() + "/" + key.replace("/", "").replace(":", "");
                try (FileOutputStream out = new FileOutputStream(imageName)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap retrieveBitmapFromCache(String key) {

        try {
            if (key == null || key.equals(""))
                return null;
            Bitmap bitmap = (Bitmap) ImageCache.getInstance().getLru().get(key);
            if (bitmap == null && cacheDir.getAbsolutePath() != "/") {
                String imageName = cacheDir.getAbsolutePath() + "/" + key.replace("/", "").replace(":", "");
                if (!new File(imageName).exists())
                    return  null;
                bitmap = BitmapFactory.decodeFile(imageName);
                System.out.println("Read from cache " + imageName);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeBitmap(String key) {
        if (key == null)
            return;
        if (cacheDir.getAbsolutePath() != "/" && !key.isEmpty()) {
            String imageName = cacheDir.getAbsolutePath() + "/" + key.replace("/", "").replace(":", "");
            File file = new File(imageName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    public void cleanCacheDir(int timePeriod) {
        final long timePeriodMilli = timePeriod * 1000 * 60 * 60 * 24;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = cacheDir.listFiles();
                long currentTime = Calendar.getInstance().getTime().toInstant().toEpochMilli();
                currentTime -= timePeriodMilli;
                for (File file : files) {
                    Log.d("ImageCache", "Checking " + file.getName() + " as modified at " + file.lastModified() + " and " + currentTime);
                    if (file.lastModified() < currentTime) {
                        Log.d("ImageCache", "Remove " + file.getName());
                        file.delete();
                    }
                }
            }
        });
        thread.start();
    }

}
