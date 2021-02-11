package com.sychev.rss_reader.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.data.ImageCache;
import com.sychev.rss_reader.data.NewsModelItem;

public class NewsViewFragment extends Fragment {

    private View rootView;
    private NewsModelItem item;
    private int font_size;

    public NewsViewFragment() {
    }

    public static NewsViewFragment newInstance(String param1, String param2) {
        NewsViewFragment fragment = new NewsViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_news_view, container, false);
        int font_size = rootView.getContext().getSharedPreferences("ViewPreference", 0).getInt("DigestFontSize", 14);

        TextView contentTextView = rootView.findViewById(R.id.news_text_content);
        if (item != null) {
            if (item.getDescription() != null)
                contentTextView.setText(item.getDescription());
            contentTextView.setTextSize(font_size);

            Bitmap bmp = null;
            if (item.getIconUrl() != null)
                bmp = ImageCache.getInstance().retrieveBitmapFromCache(item.getIconUrl());

            ImageView imageView = rootView.findViewById(R.id.imageNewView);
            if (bmp != null)
                imageView.setImageBitmap(bmp);
            else
                imageView.setVisibility(View.GONE);
            TextView titleTextView = rootView.findViewById(R.id.news_text_title);
            titleTextView.setText(item.getTitle());
            titleTextView.setTextSize(font_size);


            rootView.findViewById(R.id.open_site_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getUrl().length() > 0) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(item.getUrl()));
                        startActivity(i);
                    }
                }
            });
            rootView.findViewById(R.id.share_item_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getUrl().length() > 0) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Url:");
                        i.putExtra(Intent.EXTRA_TEXT, item.getTitle() + " " + item.getUrl().toString());
                        startActivity(i);
                    }
                }
            });

            SharedPreferences pref = rootView.getContext().getSharedPreferences("ViewPreference", 0);
            font_size = pref.getInt("DigestFontSize", 14);

            setFontSize(font_size);
        }
        return rootView;
    }

    public void setNewsItem(NewsModelItem item) {
        this.item = item;
    }

    public void setFontSize(int font_size) {
        if (rootView != null) {
            TextView content = rootView.findViewById(R.id.news_text_content);
            content.setTextSize(font_size);

            TextView title = rootView.findViewById(R.id.news_text_title);
            title.setTextSize(font_size);
        }
        this.font_size = font_size;
    }
}