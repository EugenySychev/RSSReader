package com.sychev.rss_reader.rss_reader;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsModelItem> {
    private Context mContext;
    private List<NewsModelItem> mList;

    private final int MAX_LINE_LENTGTH = 70;

    public NewsAdapter(@NonNull Context context, @LayoutRes ArrayList<NewsModelItem> list) {
        super(context, 0, list);
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.newsviewitem, parent, false);

        NewsModelItem item = mList.get(position);
        ImageView image = listItem.findViewById(R.id.pix);
        image.setImageBitmap(item.getIcon());

        TextView title = listItem.findViewById(R.id.news_title);
        title.setText(cropTextWithPoints(item.getTitle(), MAX_LINE_LENTGTH));

        TextView descr = listItem.findViewById(R.id.news_description);
        String descrText = item.getDescription();
        descr.setText(cropTextWithPoints(item.getDescription(), MAX_LINE_LENTGTH));


        return listItem;
    }

    private String cropTextWithPoints(String source, int length) {
        if (source.length() > length)
        {
            int len = length;
            while(len > 0 && !source.substring(len - 1, len).equals(" ")) len--;

            return source.substring(0, len) + "...";
        }
        return source;
    }
}