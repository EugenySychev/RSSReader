package com.sychev.rss_reader;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.sychev.rss_reader.data.ImageCache;
import com.sychev.rss_reader.data.NewsListLoader;
import com.sychev.rss_reader.data.NewsNetworkLoader;
import com.sychev.rss_reader.data.SourceModelItem;
import com.sychev.rss_reader.service.UpdateWorker;
import com.sychev.rss_reader.view.NewsListFragment;
import com.sychev.rss_reader.view.SettingsActivity;
import com.sychev.rss_reader.view.SourceListActivity;
import com.sychev.rss_reader.view.SourceNavAdapter;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NewsListLoader.UpdateNotifier {

    private static final int SETUP_ACTIVITY_REQUEST_CODE = 0;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawerLayout;
    private SourceNavAdapter navAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NewsListLoader.getInstance().init(this);
        NewsListLoader.getInstance().addNotifier(this);
        ImageCache.getInstance().setCacheDir(getCacheDir());

        int cleanTimePeriod = getSharedPreferences(Utils.APP_SETTINGS, 0).getInt(Utils.CLEAN_PERIOD_TIME_DISTANCE, Utils.defaultTimeDistanceCleaning);
        ImageCache.getInstance().cleanCacheDir(cleanTimePeriod);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createNavigationList();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        UpdateWorker.setContext(this);
        UpdateWorker.enqueueSelf();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        MenuItem item_filter = menu.findItem(R.id.filter_only_new);
        item_filter.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NewsListFragment fragment = null;


        switch (item.getItemId()) {
            case R.id.action_sources:
                intent = new Intent(this, SourceListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                callUpdateNews();
                break;
            case R.id.filter_all:
                item.setChecked(true);

                if (navHostFragment != null) {
                    fragment = (NewsListFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
                }
                if (fragment != null)
                    fragment.setFilterOnlyNew(false);

                break;
            case R.id.filter_only_new:
                item.setChecked(true);
                if (navHostFragment != null) {
                    fragment = (NewsListFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
                }
                if (fragment != null)
                    fragment.setFilterOnlyNew(true);
                break;
            case R.id.mark_read_all:
                NewsListLoader.getInstance().setCurrentNewsListAsRead();
                break;
            case R.id.mark_read_previous_today:
                NewsListLoader.getInstance().setPTodayNewsListAsRead();
                break;
            case R.id.mark_read_today:
                NewsListLoader.getInstance().setTodayNewsListAsRead();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                Log.d("Main", "Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void callUpdateNews() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NewsListFragment fragment = null;
        if (navHostFragment != null) {
            fragment = (NewsListFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
        }
        try {
            if (fragment != null)
                fragment.requestUpdate();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MAIN_ACT", "Return from some other activities");
        if (requestCode == SETUP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                System.out.println(data.getStringExtra("url"));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void createNavigationList() {
        navAdapter = new SourceNavAdapter(this, NewsListLoader.getInstance().getListSource());
        final ExpandableListView listView = findViewById(R.id.nav_list_view);
        listView.setAdapter(navAdapter);
        for (int i = 0; i < navAdapter.getGroupCount(); i++)
            listView.expandGroup(i);

        listView.setGroupIndicator(null);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                             @Override
                                             public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                                                 SourceModelItem source = (SourceModelItem) navAdapter.getChild(i, i1);

                                                 NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                                                 NewsListFragment fragment = null;
                                                 if (navHostFragment != null) {
                                                     fragment = (NewsListFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
                                                 }
                                                 if (fragment != null) {
                                                     fragment.setFilterSource(source);
                                                     drawerLayout.close();
                                                     fragment.scrollListUp();
                                                     return true;
                                                 }
                                                 return false;
                                             }
                                         }
        );
        registerForContextMenu(listView);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.source_context_menu, menu);
                menu.setHeaderTitle(getString(R.string.select_action));
            }
        });
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (i == 0) {
                    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    NewsListFragment fragment = null;
                    if (navHostFragment != null) {
                        fragment = (NewsListFragment) navHostFragment.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    }
                    if (fragment != null) {
                        fragment.setFilterSource(null);
                    }
                    drawerLayout.close();
                }

                return false;
            }
        });

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", getString(R.string.action_add));
        hashMap.put("image", String.valueOf(R.drawable.ic_baseline_add_24));
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("title", getString(R.string.action_settings));
        hashMap.put("image", String.valueOf(R.drawable.ic_baseline_settings_24));
        arrayList.add(hashMap);

        String[] from = {"title", "image"};
        int[] to = {R.id.nav_menu_item_title, R.id.nav_menu_item_icon};//int array of views id's
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.nav_menu_list_item_view, from, to);//Create object and set the parameters for simpleAdapter
        final Activity activity = this;
        ListView navListView = findViewById(R.id.nav_menu_list);
        navListView.setAdapter(simpleAdapter);
        navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    drawerLayout.close();

                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }
                if (i == 0) {
                    SourceListActivity.showAddSourceDialog(activity, null);
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);

        SourceModelItem selectedSource = (SourceModelItem) navAdapter.getChild(groupPos, childPos);

        if (selectedSource != null) {
            if (item.getItemId() == R.id.action_remove_source_context) {
                SourceListActivity.showRemoveDialog(this, selectedSource);
            } else if (item.getItemId() == R.id.action_edit_source_context) {
                SourceListActivity.showEditDialog(this, selectedSource);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update() {
        ExpandableListView listView = findViewById(R.id.nav_list_view);
        if (listView != null) {
            SourceNavAdapter adapter = (SourceNavAdapter) listView.getExpandableListAdapter();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateState(int state) {
        if (state == NewsNetworkLoader.LoadState.LOAD_ERROR)
            Toast.makeText(this, getString(R.string.loadError), Toast.LENGTH_LONG).show();
    }
}