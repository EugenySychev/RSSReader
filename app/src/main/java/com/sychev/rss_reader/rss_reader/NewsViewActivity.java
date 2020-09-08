package com.sychev.rss_reader.rss_reader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class NewsViewActivity extends Activity {

    private String urlString;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);
        Intent intent = new Intent(this, NewsViewActivity.class);
        Bundle bundle = intent.getExtras();

        String content = getIntent().getStringExtra("content");
        urlString = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");


        Bitmap bmp = intent.getParcelableExtra("BitmapImage");

       TextView contentTextView = findViewById(R.id.news_text_content);
       contentTextView.setText(content);

       ImageView imageView = findViewById(R.id.imageView);
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
}