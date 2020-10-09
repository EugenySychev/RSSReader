package com.sychev.rss_reader;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsItemView> {

    private LayoutInflater inflater;
    private List<NewsModelItem> newsList;
    private ItemClickListener clickListener;

    NewsListAdapter(Context context, List<NewsModelItem> list) {
        inflater = LayoutInflater.from(context);
        setList(list);
    }

    @NonNull
    @Override
    public NewsItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.news_list_item_view, parent, false);
        return new NewsItemView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsItemView holder, int position) {
        NewsModelItem item = newsList.get(position);
        holder.setNewsModelItem(item);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setList(List<NewsModelItem> loadedNewsList) {
        newsList = loadedNewsList;
        Collections.sort(newsList, new Comparator<NewsModelItem>() {
            @Override
            public int compare(NewsModelItem t1, NewsModelItem t2) {
                return Long.compare(t1.getTime(), t2.getTime());
            }
        });
        Collections.reverse(newsList);
    }

    public class NewsItemView extends  RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView titleView;
        private TextView timeView;
        private TextView descrView;

        public NewsItemView(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pix);
            titleView = itemView.findViewById(R.id.news_title);
            timeView = itemView.findViewById(R.id.news_time);
            descrView = itemView.findViewById(R.id.news_description);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }

        }

        public void setNewsModelItem(NewsModelItem item) {
            Bitmap icon = item.getIcon();
            if (icon == null)
                imageView.setVisibility(View.GONE);
            else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(icon);
            }

            titleView.setText(item.getTitle());
            descrView.setText(Html.fromHtml(Utils.cropTextWithPoints(item.getDescription(), 70), Html.FROM_HTML_MODE_COMPACT));
            timeView.setText(Utils.getTimeString(item.getTime()));

            if (item.getIsRead() == 0) {
                titleView.setTypeface(null, Typeface.BOLD);
                descrView.setTypeface(null, Typeface.BOLD);
            } else {
                titleView.setTypeface(null, Typeface.ITALIC);
                descrView.setTypeface(null, Typeface.ITALIC);
            }

        }

    }

    public NewsModelItem getItem(int position) {
        return newsList.get(position);
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
