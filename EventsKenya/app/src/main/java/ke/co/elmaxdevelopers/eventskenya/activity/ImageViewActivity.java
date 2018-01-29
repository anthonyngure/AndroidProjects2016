package ke.co.elmaxdevelopers.eventskenya.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;

public class ImageViewActivity extends AppCompatActivity {

    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        if (getIntent().getExtras() != null){
            imageUrl = resolveIntent("image");
        }

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(resolveIntent("name"));

        final ImageView imageView = (ImageView) findViewById(R.id.list_item_event_image);
        final ProgressWheel imageLoader = (ProgressWheel) findViewById(R.id.list_item_event_image_loader);
        imageLoader.startSpinning();
        if (imageUrl != null){
            Picasso.with(this).load(BackEnd.absoluteUrl(imageUrl))
                    .noFade()
                    .into(imageView, new Callback() {


                        @Override
                        public void onSuccess() {
                            imageLoader.setVisibility(View.GONE);
                            imageLoader.stopSpinning();
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            imageLoader.setVisibility(View.VISIBLE);
                            imageLoader.stopSpinning();
                            imageLoader.setText(getString(R.string.failed_tap_to_retry));
                        }
                    });
        }

    }
    private String resolveIntent(String key){
        return getIntent().getExtras().getString(key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
