package com.sychev.rss_reader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.net.MalformedURLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int SETUP_ACTIVITY_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageCache.getInstance().setCacheDir(getCacheDir());
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SetupSourceActivity.class);

            intent.putExtra("selected_category", 1);
            intent.putExtra("url_for_edit", "http://gazeta.sru");

            startActivityForResult(intent, SETUP_ACTIVITY_REQUEST_CODE);
        } else if (item.getItemId() == R.id.action_sources) {
            Intent intent = new Intent(this, SourceListActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_refresh) {
            callUpdateNews();
        }
        return super.onOptionsItemSelected(item);
    }

    private void callUpdateNews() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NewsListFragment fragment = null;
        if (navHostFragment != null) {
            List<Fragment> fragments = (List<Fragment>) navHostFragment.getChildFragmentManager().getFragments();
            fragment = (NewsListFragment) fragments.get(0);//findFragmentByTag("NewsListTag");
        }
        try {
            if (fragment != null)
                fragment.requestUpdate();
            else
                Log.d("MAIN", "Null frag found!!!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

}