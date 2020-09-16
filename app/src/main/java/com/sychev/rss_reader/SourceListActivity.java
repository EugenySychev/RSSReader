package com.sychev.rss_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends AppCompatActivity {

    List<SourceModelItem> sourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);

        ExpandableListView listView = findViewById(R.id.source_list_view);
        SourceListAdapter adapter = new SourceListAdapter(getApplicationContext(), sourceList);
        listView.setAdapter(adapter);
    }
}