package com.sychev.rss_reader.rss_reader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ListMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsList extends Fragment {

    private Handler mHandler;
    private NewsDataLoader loader;
    private View rootView;
    private List<NewsModelItem> loadedList;
    private MainActivity top;

    public NewsList() {

    }

    public static NewsList newInstance() {
        return new NewsList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

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
                updateUiVisability(msg.what);
                if (msg.what == NewsDataLoader.LoadState.LOAD_OK) {
                    NewsDataLoader loader = (NewsDataLoader) msg.obj;
                    loadedList = loader.loadedList;
                    updateList();
                }
                super.handleMessage(msg);
            }
        };

        loader.setHandler(mHandler);
        loader.start();

        rootView = inflater.inflate(R.layout.fragment_news_list, container, false);

        ListView listView = rootView.findViewById(R.id.lvMain);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsModelItem item = loadedList.get(i);
                Intent intent = new Intent(getContext(), NewsViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("content", item.getDescription());
                bundle.putString("url", item.getUrl());
                bundle.putString("title", item.getTitle());

                intent.putExtra("BitmapImage", item.getIcon());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateUiVisability(int what) {
        ListView listView = rootView.findViewById(R.id.lvMain);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
        TextView errorText = rootView.findViewById(R.id.textView);
        if (what == NewsDataLoader.LoadState.LOAD_OK) {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);

            System.out.println("Set visible LIST");
        } else if (what == NewsDataLoader.LoadState.LOAD_PROCESSING) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);

            System.out.println("Set visible LOAD");
        } else {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
            System.out.println("Set visible ERROR");
        }
    }

    private void updateList() {
        ListView lvMain = rootView.findViewById(R.id.lvMain);
        NewsAdapter adapter = new NewsAdapter(getContext(), (ArrayList<NewsModelItem>) loadedList);
        lvMain.setAdapter(adapter);
    }

    void setTopView(MainActivity top) {
        this.top = top;
    }
}