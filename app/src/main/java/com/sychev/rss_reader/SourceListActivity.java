package com.sychev.rss_reader;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.zip.Inflater;

import javax.xml.transform.Source;

public class SourceListActivity extends AppCompatActivity {

    private List<SourceModelItem> sourceList = new ArrayList<>();
    private SourceListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
        sourceList = NewsDbLoader.getInstance(this).getSourceList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Title");

                final View v;
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.source_dialog, null);

//                final EditText input = new EditText(view.getContext());
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                builder.setView(v);

                builder.setPositiveButton(R.string.save_button_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText)v.findViewById(R.id.enter_source_url_edit_text);
                        Spinner spinner = (Spinner)v.findViewById(R.id.spinner_category);

                        addSource(editText.getText().toString(), NewsModelItem.Categories.fromInteger(spinner.getSelectedItemPosition()));
                    }
                });
                builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        ExpandableListView listView = findViewById(R.id.source_list_view);
        listAdapter = new SourceListAdapter(getApplicationContext());
        listAdapter.setList(sourceList);
        listView.setAdapter(listAdapter);
    }

    private void addSource(final String source, final NewsModelItem.Categories category) {
        final SourceNetworkLoader loader = new SourceNetworkLoader(source);
        final SourceModelItem item = new SourceModelItem();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NewsNetworkLoader.LoadState.LOAD_OK) {
                    item.setTitle(loader.getTitle());
                    item.setCategory(category);
                    item.setUrl(source);
                    sourceList.add(item);
                    listAdapter.setList(sourceList);
                    listAdapter.notifyDataSetChanged();
                }
                super.handleMessage(msg);
            }
        };
        loader.setHandler(handler);
        loader.start();
    }
}