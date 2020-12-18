package com.sychev.rss_reader.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;
import com.sychev.rss_reader.data.NewsModelItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sychev.rss_reader.Utils.cropTextWithPoints;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsItemView> {

    private final LayoutInflater inflater;
    private List<NewsModelItem> newsList;
    private ItemClickListener clickListener;

    NewsListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NewsItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.news_list_item_view, parent, false);
        return new NewsItemView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsItemView holder, int position) {
        if (position < newsList.size()) {
            NewsModelItem item = newsList.get(position);
            holder.setNewsModelItem(item);
        } else {
            holder.setButtonAsRead();
        }
    }

    @Override
    public int getItemCount() {
        if (newsList.size() > 0)
            return newsList.size() + 1;
        else
            return 0;
    }

    public void setList(List<NewsModelItem> loadedNewsList) {
        newsList = loadedNewsList;
    }

    public Context getContext() {
        return inflater.getContext();
    }

    public NewsModelItem getItem(int position) {
        if (position >= 0)
            return newsList.get(position);
        return null;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

        void onMarkAsReadClick();
    }

    public class NewsItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView timeView;
        private final TextView descrView;
        private final Button markAsReadButton;

        public NewsItemView(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pix);
            titleView = itemView.findViewById(R.id.news_title);
            timeView = itemView.findViewById(R.id.news_time);
            descrView = itemView.findViewById(R.id.news_description);
            markAsReadButton = itemView.findViewById(R.id.mark_as_read_button);
            markAsReadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        clickListener.onMarkAsReadClick();
                }
            });
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }

        }

        public void setNewsModelItem(NewsModelItem item) {

            titleView.setVisibility(View.VISIBLE);
            descrView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            markAsReadButton.setVisibility(View.GONE);

            if (item.getIconUrl() == null || item.getIconUrl().isEmpty())
                imageView.setVisibility(View.GONE);
            else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(item.getIcon());
            }

            titleView.setText(item.getTitle());
            descrView.setText(cropTextWithPoints(item.getDescription(), 70));
            timeView.setText(Utils.getTimeString(item.getTime()));

            if (item.getIsRead() == 0) {
                titleView.setTypeface(null, Typeface.BOLD);
                descrView.setTypeface(null, Typeface.BOLD);
            } else {
                titleView.setTypeface(null, Typeface.ITALIC);
                descrView.setTypeface(null, Typeface.ITALIC);
            }

        }

        public void setButtonAsRead() {
            titleView.setVisibility(View.GONE);
            descrView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            markAsReadButton.setVisibility(View.VISIBLE);
        }
    }
}
