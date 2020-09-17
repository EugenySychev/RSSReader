package com.sychev.rss_reader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                final LayoutInflater inflater = getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.source_dialog, null))
                        .setPositiveButton(R.string.save_button_title, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SourceModelItem item = new SourceModelItem();
                                EditText editText = (EditText) inflater.inflate(R.id.enter_source_url_edit_text, null);
                                System.out.println(editText.getText());
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
            }
        });

        sourceList = NewsDbLoader.getInstance(this).getSourceList();

        ExpandableListView listView = findViewById(R.id.source_list_view);
        SourceListAdapter adapter = new SourceListAdapter(getApplicationContext(), sourceList);
        listView.setAdapter(adapter);
    }
}