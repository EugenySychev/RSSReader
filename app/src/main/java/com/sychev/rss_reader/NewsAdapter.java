package com.sychev.rss_reader;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<com.sychev.rss_reader.NewsModelItem> {
    private final int MAX_LINE_LENTGTH = 70;
    private Context mContext;
    private List<NewsModelItem> mList;

    public NewsAdapter(Context context,  ArrayList<NewsModelItem> list) {
        super(context, 0, list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.news_list_item_view, parent, false);

        NewsModelItem item = mList.get(position);
        ImageView image = listItem.findViewById(R.id.pix);
        image.setImageBitmap(item.getIcon());

        TextView title = listItem.findViewById(R.id.news_title);
        title.setText(cropTextWithPoints(item.getTitle(), MAX_LINE_LENTGTH));

        TextView descr = listItem.findViewById(R.id.news_description);
        String descrText = item.getDescription();
        descr.setText(Html.fromHtml(cropTextWithPoints(item.getDescription(), MAX_LINE_LENTGTH), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView timeview = listItem.findViewById(R.id.news_time);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getTime());
        String timeText = formatter.format(calendar.getTime());

        timeview.setText(timeText);

        if (item.getIsRead() == 0) {
            title.setTypeface(null, Typeface.BOLD);
            descr.setTypeface(null, Typeface.BOLD);
        } else {
            title.setTypeface(null, Typeface.ITALIC);
            descr.setTypeface(null, Typeface.ITALIC);
        }

        return listItem;
    }

    private String cropTextWithPoints(String source, int length) {
        if (source == null)
            return "Some shit";
        if (source.length() > length) {
            int len = length;
            while (len > 0 && !source.substring(len - 1, len).equals(" ")) len--;

            return source.substring(0, len) + "...";
        }
        return source;
    }
}