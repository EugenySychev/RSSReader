package com.sychev.rss_reader;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<String> expandableListTitle = new ArrayList<>();// = Arrays.asList("News", "Films", "Others");
    private HashMap<String, List<Pair<String, String>>> expandableListDetail = new HashMap<>();
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
            String categoryString = NewsModelItem.Categories.toString(item.getCategory());
            if (expandableListDetail.get(categoryString) == null)
                expandableListDetail.put(categoryString, new ArrayList<Pair<String, String>>());
            expandableListDetail.get(categoryString).add(Pair.create(item.getTitle(), item.getUrl()));
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
        return view;
    }

    @Override
    public View getChildView(int listPosition, int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Pair<String, String> expandedListText = (Pair<String, String>) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.source_setup_item_view, null);
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

    public void addItem(String category, Pair<String, String> item) {

        if (!expandableListTitle.contains(category)) {
            expandableListTitle.add(category);
            expandableListDetail.put(category, new ArrayList<Pair<String, String>>());
        }

        List<Pair<String, String>> list = expandableListDetail.get(category);
        list.add(item);
        expandableListDetail.put(category, list);
    }

    public void removeItem(String category, Pair<String, String> item) {
        for (Map.Entry<String, List<Pair<String, String>>> listItems : expandableListDetail.entrySet()) {
            if (listItems.getValue().contains(item)) {
                expandableListDetail.remove(listItems.getKey(), listItems.getValue());
                if (expandableListDetail.get(listItems.getKey()).size() == 0) {
                    expandableListDetail.remove(listItems.getKey());
                    expandableListTitle.remove(listItems.getKey());
                }
            }
        }
    }
}
