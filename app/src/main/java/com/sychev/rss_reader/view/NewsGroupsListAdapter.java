package com.sychev.rss_reader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;
import com.sychev.rss_reader.data.NewsModelItem;
import com.sychev.rss_reader.data.SourceModelItem;

import java.util.HashMap;
import java.util.List;

public class NewsGroupsListAdapter extends BaseExpandableListAdapter {
    private final int MAX_LINE_LENTGTH = 70;

    private final HashMap<SourceModelItem, List<NewsModelItem>> hashMap;
    private final Context context;
    private final boolean onlyNew = false;

    public NewsGroupsListAdapter(Context context, HashMap<SourceModelItem, List<NewsModelItem>> hashMap) {
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
            view = layoutInflater.inflate(R.layout.news_list_group_view, viewGroup);
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
            view = layoutInflater.inflate(R.layout.news_list_item_view, viewGroup);
        }
        Object[] list = hashMap.keySet().toArray();

        NewsModelItem item = hashMap.get(list[i]).get(i1);

        Bitmap bmp = item.getIcon();
        ImageView image = view.findViewById(R.id.pix);
        if (bmp != null) {
            image.setImageBitmap(item.getIcon());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }

        TextView title = view.findViewById(R.id.news_title);
        title.setText(Utils.cropTextWithPoints(item.getTitle(), MAX_LINE_LENTGTH));

        TextView descr = view.findViewById(R.id.news_description);
        String descrText = item.getDescription();
        descr.setText(Html.fromHtml(Utils.cropTextWithPoints(item.getDescription(), MAX_LINE_LENTGTH), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView timeview = view.findViewById(R.id.news_time);
        timeview.setText(Utils.getTimeString(item.getTime()));

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


    public HashMap<SourceModelItem, List<NewsModelItem>> getHashMap() {
        return hashMap;
    }
}
