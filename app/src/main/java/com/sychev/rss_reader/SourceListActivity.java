package com.sychev.rss_reader;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.MalformedURLException;
import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends AppCompatActivity implements NewsListLoader.updateNotifier {

    private List<SourceModelItem> sourceList = new ArrayList<>();
    private SourceListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
        sourceList = NewsListLoader.getInstance().getListSource();

        ExpandableListView listView = findViewById(R.id.source_list_view);
        listAdapter = new SourceListAdapter(getApplicationContext(), sourceList);
        listView.setAdapter(listAdapter);

        registerForContextMenu(listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.source_list_view_toolbar);
        toolbar.setTitle(R.string.sources);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.source_list_view_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_add_source) {
            showAddSourceDialog(this);
        }
        return true;
    }

    private void showAddSourceDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.enter_source_title);

        final View v;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.source_dialog, null);

        builder.setView(v);

        builder.setPositiveButton(R.string.save_button_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = (EditText) v.findViewById(R.id.enter_source_url_edit_text);
                Spinner spinner = (Spinner) v.findViewById(R.id.spinner_category);

                try {
                    addSource(editText.getText().toString(), NewsModelItem.Categories.fromInteger(spinner.getSelectedItemPosition()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.source_context_menu, menu);
        menu.setHeaderTitle("Select The Action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ExpandableListView listView = findViewById(R.id.source_list_view);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);

        Pair<String, String> pair = (Pair<String, String>) listAdapter.getChild(groupPos, childPos);

        SourceModelItem selectedSource = null;
        if (pair != null) {
            for (SourceModelItem sourceItem : sourceList) {
                if (sourceItem.getUrl().equals(pair.second) &&
                        sourceItem.getTitle().equals(pair.first) &&
                        listAdapter.getGroup(groupPos).equals(NewsModelItem.Categories.toString(sourceItem.getCategory()))) {
                    selectedSource = sourceItem;
                }
            }
        }

        if (selectedSource != null) {
            if (item.getItemId() == R.id.action_remove_source_context) {
                showRemoveDialog(this, selectedSource);
            } else {
                return false;
            }
        }
        return true;
    }

    private void showRemoveDialog(Context context, final SourceModelItem selectedSource) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.sure_confirm_delete_message)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton(R.string.ok_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NewsListLoader.getInstance().removeSource(selectedSource);
                        listAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        builder.show();
    }

    private void addSource(final String source, final NewsModelItem.Categories category) throws MalformedURLException {
        final SourceNetworkLoader networkLoader = new SourceNetworkLoader(source);
        final SourceModelItem item = new SourceModelItem();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == NewsNetworkLoader.LoadState.LOAD_OK) {
                    item.setTitle(networkLoader.getTitle());
                    item.setCategory(category);
                    item.setUrl(source);
                    NewsListLoader.getInstance().addSource(item);
                    listAdapter.notifyDataSetChanged();
                    try {
                        NewsListLoader.getInstance().requestUpdateListSource(item);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                super.handleMessage(msg);
            }
        };
        networkLoader.setHandler(handler);
        networkLoader.start();
    }

    @Override
    public void update() {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateState(int state) {
        listAdapter.notifyDataSetChanged();
    }
}