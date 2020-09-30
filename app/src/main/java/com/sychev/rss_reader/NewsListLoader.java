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
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedList;
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
        loadedList = new HashMap<>();
        dbLoader = NewsDbLoader.getInstance(context);
    }

    public List<SourceModelItem> getListSource() {
        return dbLoader.getSourceList();
    }

    public void setNotifier(updateNotifier notifier) {
        this.notifier = notifier;
    }

    public void requestLoadListSource(final SourceModelItem source) throws MalformedURLException {
        final NewsNetworkLoader loader = new NewsNetworkLoader(new URL(source.getUrl()));
        final String sourceUrl = source.getUrl();
        loadedList.put(source, dbLoader.getNewsListForSourceAndTime(sourceUrl, 0, 0));

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NewsNetworkLoader.LoadState.LOAD_OK) {
                    NewsNetworkLoader loader = (NewsNetworkLoader) msg.obj;
                    List<NewsModelItem> notSavedList = new ArrayList<>();
                    for (NewsModelItem item : loader.getLoadedList()) {
                        boolean notInList = true;
                        for (NewsModelItem dbItem : loadedList.get(source)) {
                            if (dbItem.getUrl().equals(item.getUrl())) {
                                notInList = false;
                                break;
                            }
                        }
                        if (notInList) {
                            loadedList.get(source).add(item);
                            notSavedList.add(item);
                        }
                    }
                    if (notSavedList.size() > 0) {
                        dbLoader.storeList(notSavedList);
                    }
                    source.setUpdated(true);
                }

                List<NewsModelItem> list = loadedList.get(source);
                Collections.sort(list, new Comparator<NewsModelItem>() {
                    @Override
                    public int compare(NewsModelItem t1, NewsModelItem t2) {
                        return Long.compare(t1.getTime(), t2.getTime());
                    }
                });
                loadedList.replace(source, list);
                boolean allUpdated = true;
                for (SourceModelItem sourceItem : loadedList.keySet()) {
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

    public void requestLoadNews() throws MalformedURLException {
        List<SourceModelItem> sourceList = getListSource();

        for (SourceModelItem source : sourceList) {
            requestLoadListSource(source);
        }
    }

    public List<NewsModelItem> getNewsList() {
        List<NewsModelItem> list = new ArrayList<>();
        for (SourceModelItem sourceItem : loadedList.keySet()) {
            list.addAll(loadedList.get(sourceItem));
        }
        return list;
    }

    public HashMap<SourceModelItem, List<NewsModelItem>> getLoadedList() {
        return loadedList;
    }
}
