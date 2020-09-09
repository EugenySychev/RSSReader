package com.sychev.rss_reader.rss_reader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.LruCache;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsViewActivity extends AppCompatActivity {

    private String urlString;
    private String title = "";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        toolbar = (Toolbar) findViewById(R.id.news_view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = new Intent(this, NewsViewActivity.class);
        Bundle bundle = intent.getExtras();

        String content = getIntent().getStringExtra("content");
        urlString = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");

        Bitmap bmp = ImageCache.getInstance().retrieveBitmapFromCache(urlString);

        TextView contentTextView = findViewById(R.id.news_text_content);
        contentTextView.setText(content);

        ImageView imageView = findViewById(R.id.imageNewView);
        if (bmp != null)
            imageView.setImageBitmap(bmp);

        TextView titleTextView = findViewById(R.id.news_text_title);
        titleTextView.setText(title);

        findViewById(R.id.open_site_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlString.length() > 0) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlString));
                    startActivity(i);
                }
            }
        });
        findViewById(R.id.share_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlString.length() > 0) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Url:");
                    i.putExtra(Intent.EXTRA_TEXT, title + " " + urlString);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}