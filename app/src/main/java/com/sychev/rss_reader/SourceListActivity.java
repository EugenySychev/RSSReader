package com.sychev.rss_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends AppCompatActivity {

    List<SourceModelItem> sourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        sourceList = NewsDbLoader.getInstance(this).getSourceList();

        ExpandableListView listView = findViewById(R.id.source_list_view);
        SourceListAdapter adapter = new SourceListAdapter(getApplicationContext(), sourceList);
        listView.setAdapter(adapter);
    }
}