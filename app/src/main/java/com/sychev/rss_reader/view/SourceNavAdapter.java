package com.sychev.rss_reader.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sychev.rss_reader.R;
import com.sychev.rss_reader.data.NewsListLoader;
import com.sychev.rss_reader.data.SourceModelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SourceNavAdapter extends BaseExpandableListAdapter {

    private final Context context;

    private final List<String> expandableListTitle = new ArrayList<>();// = Arrays.asList("News", "Films", "Others");
    private final HashMap<String, List<Pair<String, Integer>>> expandableListDetail = new HashMap<>();
    private final List<SourceModelItem> loadedSourceList;

    public SourceNavAdapter(Context context, List<SourceModelItem> sourceList) {
        this.context = context;
        this.loadedSourceList = sourceList;
        updateContent();
    }

    private void updateContent() {
        expandableListTitle.clear();
        expandableListDetail.clear();
        String allTitle = context.getResources().getString(R.string.all_title);
        expandableListTitle.add(allTitle);
        expandableListDetail.put(allTitle, new ArrayList<Pair<String, Integer>>());
        for (SourceModelItem item : loadedSourceList) {
            String categoryString = NewsListLoader.Categories.toString(item.getCategory());
            if (expandableListDetail.get(categoryString) == null)
                expandableListDetail.put(categoryString, new ArrayList<Pair<String, Integer>>());
            expandableListDetail.get(categoryString).add(Pair.create(item.getTitle(), item.getUnreadCount()));
            if (!expandableListTitle.contains(categoryString))
                expandableListTitle.add(categoryString);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        updateContent();
        super.notifyDataSetChanged();
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
        for (SourceModelItem item : loadedSourceList) {
            if (NewsListLoader.Categories.toString(item.getCategory()).equals(getGroup(i)) &&
                    item.getTitle().equals(expandableListDetail.get(this.expandableListTitle.get(i)).get(i1).first))
                return item;
        }
        return null;
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
            view = layoutInflater.inflate(R.layout.source_nav_group_view, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.nav_category_title);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        ImageView imageView = view.findViewById(R.id.nav_expanded_icon);
        if (b)
            imageView.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        else
            imageView.setImageResource(R.drawable.ic_baseline_arrow_right_24);
        if (i == 0)
            imageView.setVisibility(View.GONE);
        else
            imageView.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        SourceModelItem source = (SourceModelItem) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.source_nav_item_view, null);
        }

        if (source != null) {
            if (source.getIcon() != null) {
                ImageView imageView = convertView.findViewById(R.id.source_nav_item_icon);
                imageView.setImageBitmap(source.getIcon());
            }

            TextView sourceTitle = (TextView) convertView.findViewById(R.id.source_nav_item_title);
            sourceTitle.setText(source.getTitle());

            TextView sourceCounter = (TextView) convertView.findViewById(R.id.source_nav_item_counter);
            sourceCounter.setText(String.valueOf(source.getUnreadCount()));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
