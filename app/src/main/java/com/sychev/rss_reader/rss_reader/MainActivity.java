package com.sychev.rss_reader.rss_reader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<NewsModelItem> list = new ArrayList<>();
        list.add(new NewsModelItem("Title 1", "Decr 1"));
        list.add(new NewsModelItem("Title 2", "Decr 2"));
        ListView lvMain = findViewById(R.id.lvMain);

        // создаем адаптер
        NewsAdapter adapter = new NewsAdapter(this, list);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);
    }
}
