package com.sychev.rss_reader;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

public class NewsListFragment extends Fragment implements NewsListLoader.UpdateNotifier, NewsListAdapter.ItemClickListener {

    private View rootView;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedNewsMap;
    private NewsListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsListLoader.getInstance().addNotifier(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_list_fragment, container, false);
        NewsListLoader.getInstance().getAllNewsFromDB();

        RecyclerView listView = rootView.findViewById(R.id.lvMain);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsListAdapter(getContext(), NewsListLoader.getInstance().getLoadedNewsList());
        adapter.setClickListener(this);
        listView.setAdapter(adapter);

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
        RecyclerView listView = rootView.findViewById(R.id.lvMain);
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
        adapter.setList(NewsListLoader.getInstance().getLoadedNewsList());
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
        NewsListLoader.getInstance().setFilterSource(source);
    }

    @Override
    public void onItemClick(View view, int position) {
        NewsModelItem item = (NewsModelItem) adapter.getItem(position);
        NewsListLoader.getInstance().setItemIsReaded(item);
        openDigest(item);
    }
}
