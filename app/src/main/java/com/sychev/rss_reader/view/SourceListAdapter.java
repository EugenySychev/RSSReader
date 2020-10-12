package com.sychev.rss_reader.view;

import android.content.Context;
import android.graphics.Typeface;
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

public class SourceListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<String> expandableListTitle = new ArrayList<>();// = Arrays.asList("News", "Films", "Others");
    private HashMap<String, List<SourceModelItem>> expandableListDetail = new HashMap<>();
    private List<SourceModelItem> loadedSourceList;

    public SourceListAdapter(Context context, List<SourceModelItem> sourceList) {
        this.context = context;
        this.loadedSourceList = sourceList;
        updateContent();
    }

    private void updateContent() {
        expandableListTitle.clear();
        expandableListDetail.clear();
        for(SourceModelItem item: loadedSourceList) {
            String categoryString = NewsListLoader.Categories.toString(item.getCategory());
            if (expandableListDetail.get(categoryString) == null)
                expandableListDetail.put(categoryString, new ArrayList<SourceModelItem>());
            expandableListDetail.get(categoryString).add(item);
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
            view = layoutInflater.inflate(R.layout.source_setup_group_view, null);
        }
        TextView listTitleTextView = (TextView) view.findViewById(R.id.categoryTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        ImageView imageView = view.findViewById(R.id.setup_source_group_exp_icon);
        if (b)
            imageView.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
        else
            imageView.setImageResource(R.drawable.ic_baseline_arrow_right_24);

        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        SourceModelItem item = (SourceModelItem) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.source_setup_item_view, null);
        }

        if (item.getIcon() != null) {
            ImageView imageView = convertView.findViewById(R.id.source_setup_icon_view);
            imageView.setImageBitmap(item.getIcon());
        }

        TextView sourceTitle = (TextView) convertView.findViewById(R.id.source_title);
        sourceTitle.setText(item.getTitle());

        TextView sourceUrl = (TextView) convertView.findViewById(R.id.source_url);
        sourceUrl.setText(item.getUrl());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
