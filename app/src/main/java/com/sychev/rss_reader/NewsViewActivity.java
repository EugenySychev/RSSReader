package com.sychev.rss_reader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

;

public class NewsViewActivity extends AppCompatActivity {

    private String urlString;
    private String title = "";
    private String imageUrl = "";

    SharedPreferences pref;
    int font_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        pref = getApplicationContext().getSharedPreferences("ViewPreference", 0);
        font_size = pref.getInt("DigestFontSize", 10);

        Intent intent = new Intent(this, NewsViewActivity.class);
        Bundle bundle = intent.getExtras();

        String content = getIntent().getStringExtra("content");
        urlString = getIntent().getStringExtra("url");
        imageUrl = getIntent().getStringExtra("image");
        title = getIntent().getStringExtra("title");

        Toolbar toolbar = (Toolbar) findViewById(R.id.news_view_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        TextView contentTextView = findViewById(R.id.news_text_content);
        contentTextView.setText(Html.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY));
        contentTextView.setTextSize(font_size);

        Bitmap bmp = ImageCache.getInstance().retrieveBitmapFromCache(imageUrl);

        ImageView imageView = findViewById(R.id.imageNewView);
        if (bmp != null)
            imageView.setImageBitmap(bmp);
        else
            imageView.setVisibility(View.GONE);
        TextView titleTextView = findViewById(R.id.news_text_title);
        titleTextView.setText(title);
        titleTextView.setTextSize(font_size);

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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.action_increase_size) {
            font_size++;
        } else if (id == R.id.action_decrease_size) {
            font_size--;
        }

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("DigestFontSize", font_size);
        editor.commit();
        TextView content = findViewById(R.id.news_text_content);
        content.setTextSize(font_size);

        TextView title = findViewById(R.id.news_text_title);
        title.setTextSize(font_size);

        return super.onOptionsItemSelected(item);
    }
}