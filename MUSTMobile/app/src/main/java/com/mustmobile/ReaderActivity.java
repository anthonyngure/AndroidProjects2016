package com.mustmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ReaderActivity extends AppCompatActivity {

    private String content, timeCreated, title, views, imageOne, imageTwo, imageThree, imageFour;
    private TextView tvContent, tvTimeCreated, tvTitle, tvViews;
    private ImageView ivImageOne, ivImageTwo, ivImageThree, ivImageFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        tvTitle = (TextView) findViewById(R.id.activity_reader_title);
        tvContent = (TextView) findViewById(R.id.activity_reader_content);
        tvTimeCreated = (TextView) findViewById(R.id.activity_reader_time_created);
        tvViews = (TextView) findViewById(R.id.activity_reader_views);
        ivImageOne = (ImageView) findViewById(R.id.activity_reader_image_one);
        ivImageTwo = (ImageView) findViewById(R.id.activity_reader_image_two);
        ivImageThree = (ImageView) findViewById(R.id.activity_reader_image_three);
        ivImageFour = (ImageView) findViewById(R.id.activity_reader_image_four);

        if (getIntent().getExtras() != null){
            title = getIntentValue("title");
            content = getIntentValue("content");
            timeCreated = getIntentValue("timeCreated");
            views = getIntentValue("views");
            imageOne = getIntentValue("image_one");
            imageTwo = getIntentValue("image_two");
            imageThree = getIntentValue("image_three");
            imageFour = getIntentValue("image_four");
        }

        getSupportActionBar().setTitle(title);

        tvTitle.setText(title);
        tvContent.setText(content);
        tvTimeCreated.setText(timeCreated);
        tvViews.setText(views+" Views");

        /*Picasso.with(this).load(Client.absoluteUrl(imageOne)).into(ivImageOne, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });

        Picasso.with(this).load(Client.absoluteUrl(imageTwo)).into(ivImageTwo, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        Picasso.with(this).load(Client.absoluteUrl(imageThree)).into(ivImageThree, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });
        Picasso.with(this).load(Client.absoluteUrl(imageFour)).into(ivImageFour, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });*/

    }

    private String getIntentValue(String key){
        return getIntent().getExtras().getString(key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.activity_reader_action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
