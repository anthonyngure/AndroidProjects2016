package com.mustmobile;

import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.ImageView;

public class CoverImageChooserActivity extends AppCompatActivity {

    private ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_chooser);
        preview = (ImageView) findViewById(R.id.activity_cover_chooser_preview);
        showPreview(1);
        preview.setTag(1);
    }

    public void chooseCoverImage(View view){
        showPreview(Integer.parseInt(view.getTag().toString()));
        preview.setTag(view.getTag().toString());
    }

    private void showPreview(int number){
        switch (number){
            case 1:
                preview.setBackgroundResource(R.drawable.cover_image_one);
                break;
            case 2:
                preview.setBackgroundResource(R.drawable.cover_image_two);
                break;
            case 3:
                preview.setBackgroundResource(R.drawable.cover_image_three);
                break;
            /**
             * Add images 4 and 5
             */
            case 6:
                preview.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case 7:
                preview.setBackgroundColor(Color.parseColor("#0000FF"));
                break;
            case 8:
                preview.setBackgroundColor(Color.parseColor("#FF00FF"));
                break;
            case 9:
                preview.setBackgroundColor(Color.parseColor("#404040"));
                break;
            case 10:
                preview.setBackgroundColor(Color.parseColor("#FF6600"));
                break;
        }
    }

    public void setNewCoverImage(View view){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(getString(R.string.pref_cover_image), Integer.parseInt(preview.getTag().toString()));
        editor.commit();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_cover_image_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
