package com.sychev.rss_reader.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.sychev.rss_reader.MainActivity;
import com.sychev.rss_reader.R;
import com.sychev.rss_reader.Utils;
import com.sychev.rss_reader.data.NewsListLoader;
import com.sychev.rss_reader.data.SourceModelItem;

import java.util.ArrayList;
import java.util.List;

public class SourceListActivity extends AppCompatActivity implements NewsListLoader.UpdateNotifier {

    private List<SourceModelItem> sourceList = new ArrayList<>();
    private SourceListAdapter listAdapter;

    public static void showAddSourceDialog(Context context, @Nullable SourceModelItem source) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        builder.setTitle(R.string.enter_source_title);

        final View v;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.source_dialog, null);

        builder.setView(v);

        final EditText editText = (EditText) v.findViewById(R.id.enter_source_url_edit_text);
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner_category);
        final Spinner updatePeriod = (Spinner) v.findViewById(R.id.update_interval_spinner);
        final SwitchCompat onlyWifiSwitcher = (SwitchCompat) v.findViewById(R.id.update_wifi_only);
        final SourceModelItem finalSource;
        boolean update = false;
        if (source == null)
            finalSource = new SourceModelItem();
        else {
            finalSource = source;
            editText.setText(source.getUrl());
            editText.setEnabled(false);
            spinner.setSelection(NewsListLoader.Categories.toInt(source.getCategory()));
            updatePeriod.setSelection(Utils.getIndexFromUpdatePeriod(source.getUpdateTimePeriod() / 60000));
            onlyWifiSwitcher.setChecked(source.isUpdateOnlyWifi());
            update = true;
        }

        final boolean finalUpdate = update;
        builder.setPositiveButton(R.string.save_button_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long updatePeriodValue = Utils.getTimePeriodFromIndex(updatePeriod.getSelectedItemPosition());
                finalSource.setCategory(NewsListLoader.Categories.fromInteger(spinner.getSelectedItemPosition()));
                finalSource.setUpdateTimePeriod(updatePeriodValue * 60 * 1000);
                finalSource.setUpdateOnlyWifi(onlyWifiSwitcher.isChecked());

                if (finalUpdate)
                    NewsListLoader.getInstance().updateSource(finalSource);
                else
                {
                    finalSource.setUrl(editText.getText().toString());
                    finalSource.setTitle(editText.getText().toString());
                    NewsListLoader.getInstance().addSource(finalSource);
                    NewsListLoader.getInstance().requestUpdateListSource(finalSource);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog d = builder.show();
        if (!update) {
            d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (URLUtil.isValidUrl(editText.getText().toString()))
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    else
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    public static void showRemoveDialog(Context context, final SourceModelItem selectedSource) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.sure_confirm_delete_message)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton(R.string.ok_title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NewsListLoader.getInstance().removeSource(selectedSource);
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

    public static void showEditDialog(Context context, SourceModelItem selectedSource) {
        showAddSourceDialog(context, selectedSource);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int nightMode = getSharedPreferences(Utils.APP_SETTINGS, 0).getInt(Utils.NIGHT_MODE_SETTINGS_NAME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_list);
        sourceList = NewsListLoader.getInstance().getListSource();

        ExpandableListView listView = findViewById(R.id.source_list_view);
        listAdapter = new SourceListAdapter(this, sourceList);
        listView.setAdapter(listAdapter);
        listView.setGroupIndicator(null);

        for (int i = 0; i < listAdapter.getGroupCount(); i++)
            listView.expandGroup(i);

        registerForContextMenu(listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.source_list_view_toolbar);
        toolbar.setTitle(R.string.sources);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NewsListLoader.getInstance().addNotifier(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            showAddSourceDialog(this, null);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.source_context_menu, menu);
        menu.setHeaderTitle(getString(R.string.select_action));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);

        SourceModelItem selectedSource = (SourceModelItem) listAdapter.getChild(groupPos, childPos);

        if (selectedSource != null) {
            if (item.getItemId() == R.id.action_remove_source_context) {
                showRemoveDialog(this, selectedSource);
            } else if (item.getItemId() == R.id.action_edit_source_context) {
                showAddSourceDialog(this, selectedSource);
            } else {
                return false;
            }
        }
        return true;
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