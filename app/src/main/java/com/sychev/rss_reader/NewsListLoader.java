package com.sychev.rss_reader;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NewsListLoader {

    private NewsDbLoader dbLoader;
    private NewsNetworkLoader networkLoader;
    private static Context context;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedHashMap;
    private List<SourceModelItem> sourceList = new ArrayList<>();
    private static NewsListLoader instance;
    private boolean onlyNotRead;
    private SourceModelItem filterSource = null;
    List<UpdateNotifier> notifierList;

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


    interface UpdateNotifier {
        void update();

        void updateState(int state);
    }

    public static synchronized NewsListLoader getInstance() {
        if (instance == null)
            instance = new NewsListLoader();
        return instance;
    }

    public NewsListLoader() {
        loadedHashMap = new HashMap<>();
        notifierList = new ArrayList<>();
    }

    public void init(Context context) {
        this.context = context;
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
                        dbLoader.updateSource(source);
                        boolean updated = false;
                        for (SourceModelItem item: sourceList) {
                            if (item.getUrl() == source.getUrl()) {
                                sourceList.set(sourceList.indexOf(item), source);
                                updated = true;
                            }
                        }

//                        if (!updated)
//                            sourceList.add(source);
                    }
                    List<NewsModelItem> notSavedList = new ArrayList<>();
                    for (NewsModelItem item : loader.getLoadedList()) {
                        boolean notInList = true;
                        if (loadedHashMap.get(source) != null) {
                            for (NewsModelItem dbItem : loadedHashMap.get(source)) {
                                if (dbItem.getUrl().equals(item.getUrl())) {
                                    notInList = false;
                                    break;
                                }
                            }
                        } else {
                            loadedHashMap.put(source, new ArrayList<NewsModelItem>());
                        }
                        if (notInList) {
                            loadedHashMap.get(source).add(item);
                            notSavedList.add(item);
                        }
                    }
                    if (notSavedList.size() > 0) {
                        dbLoader.storeList(notSavedList);
                    }
                    source.setUpdated(true);
                }

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
                    updateUnreadCounter(source);
                }
                boolean allUpdated = true;
                for (SourceModelItem sourceItem : loadedHashMap.keySet()) {
                    if (!sourceItem.isUpdated())
                        allUpdated = false;
                }
                updateAllNotifiers();
                updateAllNotifiersState(allUpdated);
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


    private void updateAllNotifiersState(boolean allUpdated) {
        for (UpdateNotifier notifier : notifierList) {
            if (allUpdated)
                notifier.updateState(NewsNetworkLoader.LoadState.LOAD_OK);
            else
                notifier.updateState(NewsNetworkLoader.LoadState.LOAD_PROCESSING);
        }
    }

    void updateUnreadCounter(SourceModelItem source) {
        int count = 0;
        for (NewsModelItem item : loadedHashMap.get(source)) {
            if (item.getIsRead() == 0)
                count++;
        }
        source.setUnreadCount(count);
    }

    public void getNewsFromDB(SourceModelItem source) {
        if (loadedHashMap.get(source) == null)
            loadedHashMap.put(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
        else
            loadedHashMap.replace(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
        updateUnreadCounter(source);
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

    public void requestUpdateAllNews() {
        if (sourceList.isEmpty())
            loadSourceListFromDB();
        for (SourceModelItem source : sourceList) {
            source.setUpdated(false);
        }
        for (SourceModelItem source : sourceList) {
            requestUpdateListSource(source);
        }
    }

    public HashMap<SourceModelItem, List<NewsModelItem>> getLoadedHashMap() {
        return loadedHashMap;
    }

    public void setItemIsReaded(NewsModelItem item) {
        item.setIsRead(1);
        dbLoader.setItemIsRead(item, true);
        for (SourceModelItem source : sourceList) {
            if (source.getUrl().equals(item.getSource())) {
                source.setUnreadCount(source.getUnreadCount() - 1);
                break;
            }
        }
        updateAllNotifiers();
    }

    public boolean removeSource(SourceModelItem source) {
        if (dbLoader.removeSource(source) && dbLoader.removeNews(source)) {
            loadedHashMap.remove(source);
            sourceList.remove(source);
            updateAllNotifiers();
            return true;
        }
        return false;
    }
}
