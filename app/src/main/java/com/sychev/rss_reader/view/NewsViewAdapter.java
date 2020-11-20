package com.sychev.rss_reader.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sychev.rss_reader.data.NewsModelItem;
import com.sychev.rss_reader.view.NewsViewFragment;

import java.util.ArrayList;
import java.util.List;

public class NewsViewAdapter extends FragmentPagerAdapter {

    List<NewsViewFragment> fragmentList = new ArrayList<>();
    List<NewsModelItem> newsModelItemList = new ArrayList<>();
    public NewsViewAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setNewsModelItemList(List<NewsModelItem> list) {
        newsModelItemList = list;
        fragmentList.clear();
        for (NewsModelItem item : list) {
            NewsViewFragment fragment = new NewsViewFragment();
            fragment.setNewsItem(item);
            fragmentList.add(fragment);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
