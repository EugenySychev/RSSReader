package com.sychev.rss_reader;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Source;

public class SourceListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<String> expandableListTitle = new ArrayList<>();// = Arrays.asList("News", "Films", "Others");
    private HashMap<String, List<Pair<String, String>> > expandableListDetail;

    public SourceListAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<SourceModelItem> sourceList) {
        if (expandableListDetail == null)
            expandableListDetail = new HashMap<String, List<Pair<String, String>> >();
        for (SourceModelItem item: sourceList) {
            String itemCategory = NewsModelItem.Categories.toString(item.getCategory());

            if (!expandableListTitle.contains(itemCategory))
            {
                expandableListTitle.add(itemCategory);
                expandableListDetail.put(itemCategory, new ArrayList<Pair<String, String>>());
            }

            Pair<String, String> pair = Pair.create(item.getTitle(), item.getUrl());
            List<Pair<String, String>> list = expandableListDetail.get(itemCategory);
            list.add(pair);
            expandableListDetail.put(itemCategory, list);
        }
    }
    @Override
    public int getGroupCount() {
        return expandableListDetail.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (expandableListTitle.size() > 0)
            return expandableListDetail.get(expandableListTitle.get(i))
                .size();
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return expandableListTitle.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return expandableListDetail.get(this.expandableListTitle.get(i))
                .get(i1);
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
        String listTitle = (String) getGroup(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.sourcegroupitem, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.categoryTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Pair<String, String> expandedListText = (Pair<String, String>) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.sourceviewitem, null);
        }

        TextView sourceTitle = (TextView) convertView.findViewById(R.id.source_title);
        sourceTitle.setText(expandedListText.first);

        TextView sourceUrl = (TextView) convertView.findViewById(R.id.source_url);
        sourceUrl.setText(expandedListText.second);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
