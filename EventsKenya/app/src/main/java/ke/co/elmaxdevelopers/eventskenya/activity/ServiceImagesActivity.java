package ke.co.elmaxdevelopers.eventskenya.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;

public class ServiceImagesActivity extends AppCompatActivity {

    private String imageMain, imageOne, imageTwo, imageThree, imageFour;
    private ImageView ivMain, ivOne, ivTwo, ivThree, ivFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_images);

        ivMain = (ImageView) findViewById(R.id.activity_service_image_main);
        ivOne = (ImageView) findViewById(R.id.activity_service_image_one);
        ivTwo = (ImageView) findViewById(R.id.activity_service_image_two);
        ivThree = (ImageView) findViewById(R.id.activity_service_image_three);
        ivFour = (ImageView) findViewById(R.id.activity_service_image_four);

        if (getIntent().getExtras() != null){
            getSupportActionBar().setTitle(resolveIntent("name"));
            imageMain = resolveIntent("image_main");
            imageOne = resolveIntent("image_one");
            imageTwo = resolveIntent("image_two");
            imageThree = resolveIntent("image_three");
            imageFour = resolveIntent("image_four");
            loadImages();
        }
    }

    private String resolveIntent(String key){
        return getIntent().getExtras().getString(key);
    }

    private void loadImages(){
        if (!imageMain.isEmpty()){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageMain)).into(ivMain);
        } else {
            ivMain.setVisibility(View.GONE);
        }

        if (!imageOne.isEmpty()){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageOne)).into(ivOne);
        } else {
            ivOne.setVisibility(View.GONE);
        }

        if (!imageTwo.isEmpty()){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageTwo)).into(ivTwo);
        } else {
            ivTwo.setVisibility(View.GONE);
        }

        if (!imageThree.isEmpty()){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageThree)).into(ivThree);
        } else {
            ivThree.setVisibility(View.GONE);
        }

        if (!imageFour.isEmpty()){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageFour)).into(ivFour);
        } else {
            ivFour.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        } else {
            return false;
        }
    }
}
