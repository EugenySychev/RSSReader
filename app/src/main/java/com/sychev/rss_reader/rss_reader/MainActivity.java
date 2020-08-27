package com.sychev.rss_reader.rss_reader;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private NewsDataLoader loader;
    Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL url = null;
        try {
            url = new URL("https://www.gazeta.ru/export/rss/first.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        loader = new NewsDataLoader(url, 100);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                NewsDataLoader loader = (NewsDataLoader) msg.obj;
                List<NewsModelItem> list = loader.loadedList;
                updateList(list);
                super.handleMessage(msg);
            }
        };

        loader.setHandler(mHandler);
        loader.start();

    }

    private void updateList(List<NewsModelItem> list) {
        ListView lvMain = findViewById(R.id.lvMain);
        NewsAdapter adapter = new NewsAdapter(this, (ArrayList<NewsModelItem>) list);
        lvMain.setAdapter(adapter);
    }
}
