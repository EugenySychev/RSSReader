package com.sychev.rss_reader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment implements NewsListLoader.updateNotifier {

    private Handler mHandler;
    private NewsListLoader loader;
    private View rootView;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedNewsMap;
    private HashMap<SourceModelItem, List<NewsModelItem>> croppedNewsMap;
    private MainActivity top;
    private NewsListAdapter adapter;
    public NewsListFragment() {

    }

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new NewsListLoader(getContext());
        loader.setNotifier(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_list_fragment, container, false);

        ExpandableListView listView = rootView.findViewById(R.id.lvMain);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                HashMap<SourceModelItem, List<NewsModelItem>> map = adapter.getHashMap();
                Object[] listSource = map.keySet().toArray();
                List<NewsModelItem> listNews = map.get(listSource[i]);
                NewsModelItem item = listNews.get(i1);
                item.setIsRead(1);
                listNews.set(i1, item);
                map.replace((SourceModelItem) listSource[i], listNews);
                adapter.notifyDataSetChanged();
                loader.setItemIsReaded(item);
                openDigest(item);
                return false;
            }
        });

        try {
            loader.requestLoadNews();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void requestUpdate() throws MalformedURLException {
        if (loader != null)
            loader.requestLoadNews();
    }

    private void openDigest(NewsModelItem item) {
        Intent intent = new Intent(getContext(), NewsViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("content", item.getDescription());
        bundle.putString("url", item.getUrl());
        bundle.putString("title", item.getTitle());
        bundle.putString("image", item.getIconUrl());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateUiVisibility(int what) {
        ListView listView = rootView.findViewById(R.id.lvMain);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
        TextView errorText = rootView.findViewById(R.id.textView);
        if (what == NewsNetworkLoader.LoadState.LOAD_OK) {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            errorText.setVisibility(View.GONE);
        } else if (what == NewsNetworkLoader.LoadState.LOAD_PROCESSING) {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void update() {
        ExpandableListView lvMain = rootView.findViewById(R.id.lvMain);
        loadedNewsMap = loader.getLoadedList();
        adapter = new NewsListAdapter(getContext(), loadedNewsMap);
        lvMain.setAdapter(adapter);
    }

    private void filterMap() {

    }

    @Override
    public void updateState(int state) {
        updateUiVisibility(state);
    }

    public void setFilterOnlyNew(boolean b) {
        if (b) {
            HashMap<SourceModelItem, List<NewsModelItem> > croppedMap = (HashMap<SourceModelItem, List<NewsModelItem>>) loadedNewsMap.clone();
            Object[] sources = croppedMap.keySet().toArray();
            int sourcesCount = croppedMap.keySet().toArray().length;
            for (int i = 0; i < sourcesCount; i++)
            {
                List<NewsModelItem> list = croppedMap.get(sources[i]);
                List<NewsModelItem> forRemove = new ArrayList<>();
                for (NewsModelItem item : list) {
                    if (item.getIsRead() > 0) {
                        forRemove.add(item);
                    }
                }
                if (forRemove.size() > 0)
                    list.removeAll(forRemove);

                croppedMap.replace((SourceModelItem) sources[i], list);
            }
            adapter.setMap(croppedMap);
        } else {
            adapter.setMap(loadedNewsMap);
        }
    }
}