package com.sychev.rss_reader;

import android.content.Context;
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
    private Context context;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedHashMap;
    private static NewsListLoader instance;
    updateNotifier notifier;

    public void setItemIsReaded(NewsModelItem item) {
        dbLoader.setItemIsRead(item, true);
    }

    interface updateNotifier {
        void update();

        void updateState(int state);
    }

    public static synchronized NewsListLoader getInstance(Context context) {
        if (instance == null)
            instance = new NewsListLoader(context);
        return instance;
    }

    public NewsListLoader(Context context) {
        this.context = context;
        loadedHashMap = new HashMap<>();
        dbLoader = NewsDbLoader.getInstance(context);
    }

    public List<SourceModelItem> getListSource() {
        return dbLoader.getSourceList();
    }

    public void setNotifier(updateNotifier notifier) {
        this.notifier = notifier;
    }

    public void requestUpdateListSource(final SourceModelItem source) throws MalformedURLException {
        final NewsNetworkLoader loader = new NewsNetworkLoader(new URL(source.getUrl()));
        getNewsFromDB(source);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NewsNetworkLoader.LoadState.LOAD_OK) {
                    NewsNetworkLoader loader = (NewsNetworkLoader) msg.obj;
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
                }
                boolean allUpdated = true;
                for (SourceModelItem sourceItem : loadedHashMap.keySet()) {
                    if (!sourceItem.isUpdated())
                        allUpdated = false;
                }
                notifier.update();
                if (allUpdated)
                    notifier.updateState(NewsNetworkLoader.LoadState.LOAD_OK);
                else
                    notifier.updateState(NewsNetworkLoader.LoadState.LOAD_PROCESSING);
                super.handleMessage(msg);
            }
        };

        loader.setHandler(handler);
        loader.start();
    }

    private void getNewsFromDB(SourceModelItem source) {
        getNewsFromDB(source, false);
    }

    public void getNewsFromDB(SourceModelItem source, boolean onlyNotRead) {
        if (loadedHashMap.get(source) == null)
            loadedHashMap.put(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
        else
            loadedHashMap.replace(source, dbLoader.getNewsListForSourceAndTime(source.getUrl(), 0, 0, onlyNotRead));
    }

    public void getNewsFromDB(boolean onlyNotRead) {
        if (loadedHashMap.keySet().size() > 0) {
            for(SourceModelItem source : loadedHashMap.keySet())
                getNewsFromDB(source, onlyNotRead);
        } else {
            for (SourceModelItem source : getListSource())
                getNewsFromDB(source, onlyNotRead);
        }
    }

    public void requestUpdateAllNews() throws MalformedURLException {
        List<SourceModelItem> sourceList = getListSource();

        for (SourceModelItem source : sourceList) {
            requestUpdateListSource(source);
        }
    }

    public List<NewsModelItem> getNewsList() {
        List<NewsModelItem> list = new ArrayList<>();
        for (SourceModelItem sourceItem : loadedHashMap.keySet()) {
            list.addAll(loadedHashMap.get(sourceItem));
        }
        return list;
    }

    public HashMap<SourceModelItem, List<NewsModelItem>> getLoadedHashMap() {
        return loadedHashMap;
    }
}
