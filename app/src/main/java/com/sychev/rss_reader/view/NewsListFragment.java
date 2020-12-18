package com.sychev.rss_reader.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
    private ListActions listActionsHandler = null;

    public void setListActionsHandler(ListActions listActionsHandler) {
        this.listActionsHandler = listActionsHandler;
    }

    public interface ListActions {
        void callAddSourceDialog();
        void updateSourceList();
    }

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

        Button showReadButton = rootView.findViewById(R.id.buttonShowReadNewsList);
        showReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilterOnlyNew(false);
            }
        });

        Button addSourceButton = rootView.findViewById(R.id.buttonAddSourceNewsList);
        addSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listActionsHandler != null)
                    listActionsHandler.callAddSourceDialog();
            }
        });

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

//        RecyclerView listView = rootView.findViewById(R.id.lvMain);
//        TextView errorText = rootView.findViewById(R.id.textView);
        LinearLayout emptyLayout = rootView.findViewById(R.id.nothingToShowLayout);
        LinearLayout newsListLayout = rootView.findViewById(R.id.newsListLayout);
        Button showReadButton = rootView.findViewById(R.id.buttonShowReadNewsList);
        Button addSourceButton = rootView.findViewById(R.id.buttonAddSourceNewsList);
        if (adapter.getItemCount() == 0) {
            newsListLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            if (NewsListLoader.getInstance().getListSource().size() > 0) {
                showReadButton.setVisibility(View.VISIBLE);
                addSourceButton.setVisibility(View.GONE);
            } else {
                showReadButton.setVisibility(View.GONE);
                addSourceButton.setVisibility(View.VISIBLE);
            }
        } else {
            newsListLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateState(int state) {
        updateUiVisibility(state);
    }

    public void setFilterOnlyNew(boolean onlyNew) {
        NewsListLoader.getInstance().setOnlyNotRead(onlyNew);
        NewsListLoader.getInstance().getAllNewsFromDB();
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
    public void onMarkAsReadClick() {
        NewsListLoader.getInstance().setCurrentNewsListAsRead();
    }

    @Override
    public void processSwipe(int position) {
        NewsListLoader.getInstance().setItemIsReadWithoutUpdate(adapter.getItem(position));
        if (listActionsHandler != null)
            listActionsHandler.updateSourceList();

    }

    public void scrollListUp() {
        final RecyclerView listView = rootView.findViewById(R.id.lvMain);
        listView.scrollToPosition(0);
    }
}
