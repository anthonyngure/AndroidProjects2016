package com.mustmobile;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GalleryViewActivity extends AppCompatActivity {

    private String url, description;
    private ImageView mImageView;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        mImageView = (ImageView) findViewById(R.id.activity_gallery_view_image);
        tvDescription = (TextView) findViewById(R.id.activity_gallery_view_description);

        if (getIntent().getExtras() != null){
            url = resolveIntentValue("url");
            description = resolveIntentValue("description");
        }

        getSupportActionBar().setTitle(description);

        tvDescription.setText(description);
        Picasso.with(this).load(Client.absoluteUrl(url)).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                mImageView.setImageResource(R.drawable.default_profile);
            }
        });

    }

    private String resolveIntentValue(String key){
        return getIntent().getExtras().getString(key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
