package com.sychev.rss_reader.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.data.NewsListLoader;
import com.sychev.rss_reader.data.NewsModelItem;
import com.sychev.rss_reader.data.NewsNetworkLoader;
import com.sychev.rss_reader.data.SourceModelItem;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

public class NewsListFragment extends Fragment implements NewsListLoader.UpdateNotifier, NewsListAdapter.ItemClickListener, SwipeReadCallback.SwipeActionCallback {

    private View rootView;
    private HashMap<SourceModelItem, List<NewsModelItem>> loadedNewsMap;
    private NewsListAdapter adapter;
    private SourceModelItem selectedSource = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsListLoader.getInstance().addNotifier(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_list_fragment, container, false);


        final RecyclerView listView = rootView.findViewById(R.id.lvMain);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsListAdapter(getContext());
        update();
        adapter.setClickListener(this);
        listView.setAdapter(adapter);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    requestUpdate();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeReadCallback(adapter, this));
        itemTouchHelper.attachToRecyclerView(listView);

        return rootView;
    }

    public void requestUpdate() throws MalformedURLException {
        swipeRefreshLayout.setRefreshing(true);
        if (selectedSource == null) {
            if (!NewsListLoader.getInstance().requestUpdateAllNews())
                swipeRefreshLayout.setRefreshing(false);
        } else {
            NewsListLoader.getInstance().requestUpdateListSource(selectedSource);
        }
    }

    private void openDigest(NewsModelItem item) {
        Intent intent = new Intent(getContext(), NewsViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", item.getUrl());
        intent.putExtras(bundle);

        Log.d("NEWS_LIST", "Pass to start activity with url " + item.getUrl());
        startActivity(intent);
    }

    private void updateUiVisibility(int what) {
        if (what == NewsNetworkLoader.LoadState.LOAD_OK) {
            swipeRefreshLayout.setRefreshing(false);
        } else if (what == NewsNetworkLoader.LoadState.LOAD_PROCESSING) {
            if (!swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(true);
        } else { // Error loading
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void update() {
        selectedSource = NewsListLoader.getInstance().getFilterSource();
        adapter.setList(NewsListLoader.getInstance().getLoadedNewsList());
        adapter.notifyDataSetChanged();

        RecyclerView listView = rootView.findViewById(R.id.lvMain);
        TextView errorText = rootView.findViewById(R.id.textView);
        if (adapter.getItemCount() == 0) {
            listView.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateState(int state) {
        updateUiVisibility(state);
    }

    public void setFilterOnlyNew(boolean onlyNew) {
        NewsListLoader.getInstance().setOnlyNotRead(onlyNew);
        NewsListLoader.getInstance().getAllNewsFromDB(-1);
        update();
    }

    public void setFilterSource(SourceModelItem source) {
        selectedSource = source;
        NewsListLoader.getInstance().setFilterSource(source);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("NewsList", "Cliecked " + position);
        NewsModelItem item = adapter.getItem(position);
        if (item != null) {
            NewsListLoader.getInstance().setItemIsRead(item);
            openDigest(item);
        }
    }

    @Override
    public void processSwipe(int position) {
        NewsListLoader.getInstance().setItemIsReadWithoutUpdate(adapter.getItem(position));
    }

    public void scrollListUp() {
        final RecyclerView listView = rootView.findViewById(R.id.lvMain);
        listView.scrollToPosition(0);
    }
}
