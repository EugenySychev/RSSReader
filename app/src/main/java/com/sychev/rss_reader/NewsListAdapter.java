package com.sychev.rss_reader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.RemoteInput;
import androidx.core.text.HtmlCompat;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewsListAdapter extends BaseExpandableListAdapter {
    private final int MAX_LINE_LENTGTH = 70;

    private HashMap<SourceModelItem, List<NewsModelItem>> hashMap;
    private Context context;
    private boolean onlyNew = false;

    public NewsListAdapter(Context context, HashMap<SourceModelItem, List<NewsModelItem>> hashMap) {
        this.context = context;
        this.hashMap = hashMap;
    }

    @Override
    public int getGroupCount() {
        return hashMap.keySet().size();
    }

    @Override
    public int getChildrenCount(int i) {
        Object[] list = hashMap.keySet().toArray();
        return hashMap.get(list[i]).size();
    }

    @Override
    public Object getGroup(int i) {
        Object[] list = hashMap.keySet().toArray();
        return list[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        Object[] list = hashMap.keySet().toArray();
        return hashMap.get(list[i]).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        SourceModelItem item = (SourceModelItem) getGroup(i);
        String title = item.getTitle();
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.newsgroupitem, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.news_group_title);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(title);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.newsviewitem, null);
        }
        Object[] list = hashMap.keySet().toArray();

        NewsModelItem item = hashMap.get(list[i]).get(i1);

        ImageView image = view.findViewById(R.id.pix);
        image.setImageBitmap(item.getIcon());

        TextView title = view.findViewById(R.id.news_title);
        title.setText(cropTextWithPoints(item.getTitle(), MAX_LINE_LENTGTH));

        TextView descr = view.findViewById(R.id.news_description);
        String descrText = item.getDescription();
        descr.setText(Html.fromHtml(cropTextWithPoints(item.getDescription(), MAX_LINE_LENTGTH), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView timeview = view.findViewById(R.id.news_time);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

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

        if (onlyNew && item.getIsRead() > 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
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

    public boolean isOnlyNew() {
        return onlyNew;
    }

    public void setOnlyNew(boolean onlyNew) {
        this.onlyNew = onlyNew;
    }

    public void setMap(HashMap<SourceModelItem, List<NewsModelItem>> croppedMap) {
        hashMap = croppedMap;
        notifyDataSetChanged();
    }

    public HashMap<SourceModelItem, List<NewsModelItem>> getHashMap() {
        return hashMap;
    }
}
