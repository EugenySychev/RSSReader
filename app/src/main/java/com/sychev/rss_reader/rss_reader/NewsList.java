package com.sychev.rss_reader.rss_reader;

import org.w3c.dom.Document;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NewsList {
    interface Progress {
        int IDLE = 0;
        int LOAD_NETWORK = 1;
        int LOAD_NETWORK_ERROR = 2;
        int LOAD_NETWORK_OK = 3;
    }

    private Map<URL, List<NewsModelItem> > newsMap;

//    List<NewsModelItem> getList(URL source, Date beginDateTime) {
//        Calendar cal = Calendar.getInstance();
//        Date dt = cal.getTime();
//        return getList(source, beginDateTime, dt);
//    }
//
//    List<NewsModelItem> getList(URL source, Date beginDateTime, Date endDateTime) {
//
//        ret
//    }
//
//
//    boolean requestUpdateList(URL source) {
//
//    }
//
//    List<NewsModelItem> parseLoadedNews(Document doc) {
//
//    }
}