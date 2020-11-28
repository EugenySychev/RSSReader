package com.sychev.rss_reader.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewsListLoader {

    private static final String TAG = "NewsLoader";
    private static Context context = null;
    private static NewsListLoader instance;
    private final HashMap<SourceModelItem, List<NewsModelItem>> loadedHashMap;
    List<UpdateNotifier> notifierList;
    private NewsDbLoader dbLoader;
    private List<SourceModelItem> sourceList = new ArrayList<>();
    private boolean onlyNotRead;
    private SourceModelItem filterSource = null;

    public NewsListLoader() {
        loadedHashMap = new HashMap<>();
        notifierList = new ArrayList<>();
    }

    public static synchronized NewsListLoader getInstance() {
        if (instance == null)
            instance = new NewsListLoader();
        return instance;
    }

    public boolean isReady() {
        return context != null;
    }

    public void setCurrentNewsListAsRead() {
        List<NewsModelItem> list = getLoadedNewsList();
        for (NewsModelItem item : list) {
            item.setIsRead(1);
            dbLoader.setItemIsRead(item, true);
        }
        updateAllUnreadCounters();
        updateAllNotifiers();
    }

    public void checkNetworkAndUpdate() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return;
        boolean updateEnabled = context.getSharedPreferences(Utils.APP_SETTINGS, 0).getBoolean(Utils.UPDATE_ON_STARTUP, true);
        if (info.getType() == ConnectivityManager.TYPE_WIFI && updateEnabled) {
            if (filterSource == null) {
                NewsListLoader.getInstance().requestUpdateAllNews();
            } else {
                NewsListLoader.getInstance().requestUpdateListSource(filterSource);
            }
        }
    }

    public void setTodayNewsListAsRead() {
        Date currentTime = Calendar.getInstance().getTime();
        long timeMils = currentTime.toInstant().toEpochMilli();
        Log.d("NEWSLOADER", "setPTodayNewsListAsRead: " + timeMils);
        timeMils -= 24 * 60 * 60 * 1000;

        Log.d("NEWSLOADER", "setPTodayNewsListAsRead: " + timeMils);
        List<NewsModelItem> list = getLoadedNewsList();
        for (NewsModelItem item : list) {
            if (item.getTime() > timeMils) {
                item.setIsRead(1);
                dbLoader.setItemIsRead(item, true);
            }
        }
        updateAllUnreadCounters();
        updateAllNotifiers();
    }

    public void setPTodayNewsListAsRead() {
        Date currentTime = Calendar.getInstance().getTime();
        long timeMils = currentTime.toInstant().toEpochMilli();
        Log.d("NEWSLOADER", "setPTodayNewsListAsRead: " + timeMils);
        timeMils -= 24 * 60 * 60 * 1000;
        List<NewsModelItem> list = getLoadedNewsList();
        for (NewsModelItem item : list) {
            if (item.getTime() < timeMils) {
                item.setIsRead(1);
                dbLoader.setItemIsRead(item, true);
            }
        }
        updateAllUnreadCounters();
        updateAllNotifiers();
    }

    public SourceModelItem getFilterSource() {
        return filterSource;
    }

    public void setFilterSource(SourceModelItem filterSource) {
        this.filterSource = filterSource;
        updateAllNotifiers();
    }

    public void addSource(SourceModelItem item) {
        if (dbLoader != null)
            if (dbLoader.addSource(item)) {
                updateAllNotifiers();
            }
    }

    public List<NewsModelItem> getLoadedNewsList() {
        if (filterSource != null)
            return loadedHashMap.get(filterSource);

        List<NewsModelItem> fullList = new ArrayList<>();
        for (SourceModelItem source : loadedHashMap.keySet()) {
            fullList.addAll(loadedHashMap.get(source));
        }
        return fullList;
    }

    public void init(Context context) {
        NewsListLoader.context = context;
        dbLoader = new NewsDbLoader(context);
    }

    public List<SourceModelItem> getListSource() {
        if (sourceList.isEmpty())
            loadSourceListFromDB();
        return sourceList;
    }

    public void loadSourceListFromDB() {
        sourceList = dbLoader.getListSource();
    }

    public void addNotifier(UpdateNotifier notifier) {
        notifierList.add(notifier);
    }

    public void removeNotifier(UpdateNotifier notifier) {
        notifierList.remove(notifier);
    }

    public void requestUpdateListSource(final SourceModelItem source) {
        if (!sourceList.contains(source) && source.getTitle() != null)
            sourceList.add(source);

        final NewsNetworkLoader loader = new NewsNetworkLoader(source);
        getNewsFromDB(source);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NewsNetworkLoader.LoadState.LOAD_OK) {
                    NewsNetworkLoader loader = (NewsNetworkLoader) msg.obj;
                    if (((NewsNetworkLoader) msg.obj).isNeedUpdateSource()) {
                        for (SourceModelItem item : sourceList) {
                            if (item.getUrl().equals(source.getUrl())) {
                                sourceList.set(sourceList.indexOf(item), source);
                            }
                        }
                    }
                    List<NewsModelItem> notSavedList = new ArrayList<>();

                    long lastDigestTime = source.getLastDigestTime();

                    for (NewsModelItem item : loader.getLoadedList()) {
                        boolean notInList = true;
                        if (loadedHashMap.get(source) != null) {

                            Log.d(TAG, "Check digest " + item.getTitle() + " from " + item.getTime());
                            Log.d(TAG, "Source loaded last time at " + source.getLastUpdated());
                            if (source.getLastUpdated() == 0) {
                                for (NewsModelItem dbItem : Objects.requireNonNull(loadedHashMap.get(source))) {
                                    if (dbItem.getUrl().equals(item.getUrl())) {
                                        notInList = false;
                                        break;
                                    }
                                }
                            } else {
                                if (item.getTime() <= source.getLastDigestTime()) {
                                    notInList = false;
                                    if (item.getTime() > lastDigestTime)
                                        lastDigestTime = item.getTime();
                                }
                            }
                            Log.d(TAG, "Item not in list is " + notInList);
                        } else {
                            loadedHashMap.put(source, new ArrayList<NewsModelItem>());
                        }
                        if (notInList && item != null) {
                            loadedHashMap.get(source).add(item);
                            notSavedList.add(item);
                        }
                    }
                    if (notSavedList.size() > 0) {
                        dbLoader.storeList(notSavedList);
                    }
                    source.setUpdated(true);
                    source.setLastUpdated(Calendar.getInstance().getTimeInMillis());
                    source.setLastDigestTime(lastDigestTime);
                    dbLoader.updateSource(source);

                    List<NewsModelItem> list = loadedHashMap.get(source);
                    if (list != null) {
                        Collections.sort(list, new Comparator<NewsModelItem>() {
                            @Override
                            public int compare(NewsModelItem t1, NewsModelItem t2) {
                                return Long.compare(t1.getTime(), t2.getTime());
                            }
                        });

                        Collections.reverse(list);
                        loadedHashMap.replace(source, list);
                        updateUnreadCounterAndLastTime(source);
                    }
                    boolean allUpdated = true;
                    for (SourceModelItem sourceItem : loadedHashMap.keySet()) {
                        if (!sourceItem.isUpdated())
                            allUpdated = false;
                    }
                    updateAllNotifiers();
                    updateAllNotifiersState(allUpdated ? NewsNetworkLoader.LoadState.LOAD_OK : NewsNetworkLoader.LoadState.LOAD_PROCESSING);

                } else if (msg.what == NewsNetworkLoader.LoadState.LOAD_ERROR) {
                    updateAllNotifiersState(NewsNetworkLoader.LoadState.LOAD_ERROR);
                }


                super.handleMessage(msg);
            }
        };

        loader.setHandler(handler);
        loader.start();
    }

    private void updateAllNotifiers() {
        for (UpdateNotifier notifier : notifierList) {
            notifier.update();
        }
    }

    private void updateAllNotifiersState(int state) {
        for (UpdateNotifier notifier : notifierList) {
            notifier.updateState(state);
        }
    }

    private void updateAllUnreadCounters() {
        for (SourceModelItem source : sourceList)
            updateUnreadCounterAndLastTime(source);
    }

    public void updateUnreadCounterAndLastTime(SourceModelItem source) {
        int count = 0;
        for (NewsModelItem item : loadedHashMap.get(source)) {
            if (item.getIsRead() == 0)
                count++;
            if (item.getTime() > source.getLastDigestTime())
                source.setLastDigestTime(item.getTime());
        }
        source.setUnreadCount(count);
    }

    public void getNewsFromDB(SourceModelItem source) {
        if (loadedHashMap.get(source) == null)
            loadedHashMap.put(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
        else
            loadedHashMap.replace(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
        updateUnreadCounterAndLastTime(source);
    }

    public void setOnlyNotRead(boolean onlyNotRead) {
        this.onlyNotRead = onlyNotRead;
    }

    public void getAllNewsFromDB() {
        if (filterSource == null) {
            for (SourceModelItem source : getListSource())
                getNewsFromDB(source);
        } else {
            getNewsFromDB(filterSource);
        }
    }

    public boolean requestUpdateAllNews() {
        if (sourceList.isEmpty()) {
            loadSourceListFromDB();
            if (sourceList.isEmpty())
                return false;
        }
        for (SourceModelItem source : sourceList) {
            source.setUpdated(false);
        }
        for (SourceModelItem source : sourceList) {
            requestUpdateListSource(source);
        }
        return true;
    }

    public HashMap<SourceModelItem, List<NewsModelItem>> getLoadedHashMap() {
        return loadedHashMap;
    }

    public void setItemIsRead(NewsModelItem item) {
        setItemIsReadWithoutUpdate(item);
        updateAllNotifiers();
    }

    public void setItemIsReadWithoutUpdate(NewsModelItem item) {
        item.setIsRead(1);
        dbLoader.setItemIsRead(item, true);
        for (SourceModelItem source : sourceList) {
            if (source.getUrl().equals(item.getSource())) {
                source.setUnreadCount(source.getUnreadCount() - 1);
                break;
            }
        }
    }

    public boolean removeSource(SourceModelItem source) {
        if (filterSource == source)
            filterSource = null;
        if (dbLoader.removeSource(source) && dbLoader.removeNews(source)) {
            for (NewsModelItem item : loadedHashMap.get(source))
                ImageCache.getInstance().removeBitmap(item.getIconUrl());
            loadedHashMap.remove(source);
            sourceList.remove(source);
            updateAllNotifiers();
            return true;
        }
        return false;
    }

    public void requestUpdateAllNewsTimer() {
        if (sourceList.isEmpty())
            loadSourceListFromDB();

        long current = Calendar.getInstance().getTimeInMillis();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        for (SourceModelItem item : sourceList) {
            Log.d(TAG, "Checking " + item.getTitle() + " last updated " + (current - item.getLastUpdated()) / 60000 + " minutes ");
            Log.d(TAG, "UpdatePeriod is " + (item.getUpdateTimePeriod() - 120000) / 60000);
            if ((current - item.getLastUpdated() > item.getUpdateTimePeriod() - 120000) &&
                    (info.getType() == ConnectivityManager.TYPE_WIFI || !item.isUpdateOnlyWifi()))
                requestUpdateListSource(item);
        }
    }

    public void updateSource(SourceModelItem source) {
        if (dbLoader != null)
            if (dbLoader.updateSource(source)) {
                updateAllNotifiers();
            }
    }

    public enum Categories {
        NEWS_CATEGORY,
        FILMS_CATEGORY,
        OTHER_CATEGORY;

        public static Categories fromInteger(int val) {
            switch (val) {
                case 0:
                    return NEWS_CATEGORY;
                case 1:
                    return FILMS_CATEGORY;
                case 2:
                    return OTHER_CATEGORY;
            }
            return OTHER_CATEGORY;
        }

        public static int toInt(Categories cat) {
            switch (cat) {
                case NEWS_CATEGORY:
                    return 0;
                case FILMS_CATEGORY:
                    return 1;
                case OTHER_CATEGORY:
                    return 2;
            }
            return 2;
        }

        public static String toString(Categories cat) {
            if (context != null) {
                switch (cat) {
                    case NEWS_CATEGORY:
                        return context.getResources().getString(R.string.news_category_string);
                    case FILMS_CATEGORY:
                        return context.getResources().getString(R.string.films_category_title);
                    case OTHER_CATEGORY:
                        return context.getResources().getString(R.string.others_category_title);
                }
            } else {
                switch (cat) {
                    case NEWS_CATEGORY:
                        return "News";
                    case FILMS_CATEGORY:
                        return "Films";
                    case OTHER_CATEGORY:
                        return "Other";
                }
            }
            return "";
        }

    }

    public interface UpdateNotifier {
        void update();

        void updateState(int state);
    }
}
