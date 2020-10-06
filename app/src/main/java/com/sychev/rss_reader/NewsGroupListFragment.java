package com.sychev.rss_reader;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

public class NewsGroupListFragment extends Fragment implements NewsListLoader.UpdateNotifier {

    private View rootView;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedNewsMap;
    private NewsGroupsListAdapter adapter;

    public NewsGroupListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        NewsListLoader.getInstance().init(this.getContext());
        NewsListLoader.getInstance().addNotifier(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_list_fragment, container, false);

        ExpandableListView listView = rootView.findViewById(R.id.lvMain);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                NewsModelItem item = (NewsModelItem) adapter.getChild(i, i1);
                SourceModelItem source = (SourceModelItem) adapter.getGroup(i);
                loadedNewsMap.get(source).get(i1).setIsRead(1);
                NewsListLoader.getInstance().setItemIsReaded(item);
                openDigest(item);
                return false;
            }
        });
        loadedNewsMap = NewsListLoader.getInstance().getLoadedHashMap();
        adapter = new NewsGroupsListAdapter(getContext(), loadedNewsMap);
        listView.setAdapter(adapter);
        NewsListLoader.getInstance().getAllNewsFromDB();
        for (int i = 0; i < adapter.getGroupCount(); i++)
            listView.expandGroup(i);
        return rootView;
    }

    public void requestUpdate() throws MalformedURLException {
        NewsListLoader.getInstance().requestUpdateAllNews();
    }

    public void requestLoad() {
        NewsListLoader.getInstance().getAllNewsFromDB();
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateState(int state) {
        updateUiVisibility(state);
    }

    public void setFilterOnlyNew(boolean onlyNew) {
        NewsListLoader.getInstance().setOnlyNotRead(onlyNew);
        NewsListLoader.getInstance().getAllNewsFromDB();
        adapter.notifyDataSetChanged();
    }

    public void setFilterSource(SourceModelItem source) {
        // TODO show only source items list, null for all sources
    }
}