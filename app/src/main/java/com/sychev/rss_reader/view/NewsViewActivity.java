package com.sychev.rss_reader.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.sychev.rss_reader.NewsViewAdapter;
import com.sychev.rss_reader.R;
import com.sychev.rss_reader.data.NewsListLoader;
import com.sychev.rss_reader.data.NewsModelItem;

import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    private final String title = "";
    private final String imageUrl = "";
    SharedPreferences pref;
    int font_size;
    private String urlString;
    private NewsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        pref = getApplicationContext().getSharedPreferences("ViewPreference", 0);
        font_size = pref.getInt("DigestFontSize", 14);

        Intent intent = new Intent(this, NewsViewActivity.class);
        Bundle bundle = intent.getExtras();

        urlString = getIntent().getStringExtra("url");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.news_view_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new NewsViewAdapter(getSupportFragmentManager(), 0);
        ViewPager pager = findViewById(R.id.newsViewPager);
        final List<NewsModelItem> newsList = NewsListLoader.getInstance().getLoadedNewsList();
        adapter.setNewsModelItemList(newsList);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                NewsModelItem item = newsList.get(position);
                NewsListLoader.getInstance().setItemIsRead(item);
                toolbar.setTitle(item.getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        int index;
        for (index = 0; index < newsList.size(); index++) {

            if (newsList.get(index).getUrl().equals(urlString))
                break;
        }

        if (index < newsList.size()) {
            pager.setCurrentItem(index);
            toolbar.setTitle(newsList.get(index).getTitle());
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adNewsView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_increase_size) {
            font_size++;
        } else if (id == R.id.action_decrease_size) {
            font_size--;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("DigestFontSize", font_size);
        editor.commit();

        for (int i = 0; i < adapter.getCount(); i++) {
            NewsViewFragment fragment = (NewsViewFragment) adapter.getItem(i);
            fragment.setFontSize(font_size);
        }
        return super.onOptionsItemSelected(item);
    }
}