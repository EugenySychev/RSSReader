package com.sychev.rss_reader;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Source;

public class NewsListLoader {

    private NewsDbLoader dbLoader;
    private NewsNetworkLoader networkLoader;
    private Context context;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedList;
    private static NewsListLoader instance;
    updateNotifier notifier;

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
        NewsNetworkLoader loader = new NewsNetworkLoader(new URL(source.getUrl()));
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
                        for(NewsModelItem dbItem : loadedList.get(source))
                        {
                            if (dbItem.getUrl() == item.getUrl()) {
                                notInList = false;
                                break;
                            }
                        }
                        if (notInList) {
                            loadedList.get(source).add(item);
                            notSavedList.add(item);
                            notifier.update();
                        }
                    }
                    if (notSavedList.size() > 0) {
                        dbLoader.storeList(notSavedList);
                    }
                    source.setUpdated(true);
                }
                boolean allUpdated = true;
                for (SourceModelItem sourceItem : loadedList.keySet())
                {
                    if (!sourceItem.isUpdated())
                        allUpdated = false;
                }
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

        for(SourceModelItem source: sourceList) {
            requestLoadListSource(source);
        }
    }

    public List<NewsModelItem> getNewsList() {
        List<NewsModelItem> list = new ArrayList<>();
        for(SourceModelItem sourceItem : loadedList.keySet()) {
            list.addAll(loadedList.get(sourceItem));
        }
        return list;
    }
}
