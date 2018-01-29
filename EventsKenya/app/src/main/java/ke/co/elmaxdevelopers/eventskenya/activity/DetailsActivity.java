package ke.co.elmaxdevelopers.eventskenya.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.StorageUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;
import ke.co.elmaxdevelopers.eventskenya.views.SquaredImageView;

public class DetailsActivity extends AppCompatActivity implements LoadingListener {

    public static final String EXTRA_EVENT = "event";

    private ViewGroup footerViewContent;
    private Event event;
    private ProgressWheel detailsLoader;
    private TextView tvDescription;
    private SquaredImageView imageView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        if (getIntent().getExtras() != null){
            ArrayList<Event> events =  getIntent().getParcelableArrayListExtra(EXTRA_EVENT);
            event = events.get(0);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(event.getName());
        imageView = (SquaredImageView) findViewById(R.id.backdrop);

        if (event.hasImage()){
            imageView.setImageBitmap(event.decodeImage());
        } else {
            loadFullPoster();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                ArrayList<Event> parcelableEvent = new ArrayList<>(1);
                parcelableEvent.add(event);
                intent.putParcelableArrayListExtra(CommentsActivity.EXTRA_EVENT, parcelableEvent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        tvDescription = (TextView) findViewById(R.id.description);

        detailsLoader = (ProgressWheel) findViewById(R.id.progress_wheel);
        footerViewContent = (ViewGroup) findViewById(R.id.details_activity_footerview_content);

        tvDescription.setText(event.getDescription());

        if (event.hasLoadedDetails()){
            showDetails();
        } else {
            APIConnector.getInstance(this).getDetails(DetailsActivity.class.getSimpleName(), event.getId(), this);
        }
    }

    private void loadFullPoster() {
        Picasso.with(this).load(
                BackEnd.absoluteUrl("images/" + event.getImageUrl()))
                .noFade()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .tag(DetailsActivity.class.getSimpleName())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                        event.addImage(imageView);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void showDetails(){
        TextView tvPromoter = (TextView) findViewById(R.id.details_activity_footerview_promoter);
        TextView tvOrganizer = (TextView) findViewById(R.id.details_activity_footerview_organizer);
        TextView tvParking = (TextView) findViewById(R.id.details_activity_footerview_parking);
        TextView tvSecurity = (TextView) findViewById(R.id.details_activity_footerview_security);
        TextView tvCreator = (TextView) findViewById(R.id.details_activity_footerview_creator);
        TextView tvBurTicket = (TextView) findViewById(R.id.details_activity_footerview_button_buy_ticket);

        tvPromoter.setText(event.getPromoter());
        tvOrganizer.setText(event.getOrganizer());
        tvParking.setText(event.getParkingInfo());
        tvSecurity.setText(event.getSecurityInfo());
        tvCreator.setText(event.getOrganizer());

        tvBurTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyTicketIntent = new Intent(Intent.ACTION_VIEW);
                String url = event.getTicketsLink();
                if (!url.isEmpty()){
                    if (!url.startsWith("http://") && !url.startsWith("https://") ){
                        url = "http://"+url;
                    }
                    buyTicketIntent.setData(Uri.parse(url));
                    if (buyTicketIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(buyTicketIntent);
                    }
                }

            }
        });

        TextView tvFree = (TextView) findViewById(R.id.details_activity_footerview_tickets_free);
        ViewGroup notFree = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_not_free);
        ViewGroup advanceBar = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_advance_bar);
        ViewGroup vipBar = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_vip_bar);

        TextView tvAdvance = (TextView) findViewById(R.id.details_activity_footerview_advance);
        TextView tvRegular = (TextView) findViewById(R.id.details_activity_footerview_regular);
        TextView tvVip = (TextView) findViewById(R.id.details_activity_footerview_vip);

        if (event.getAdvance() == 0 && event.getRegular() == 0 && event.getVip() == 0){
            tvFree.setVisibility(View.VISIBLE);
            notFree.setVisibility(View.GONE);
        } else {
            //TODO add functionality to buy button
            if (event.getAdvance() != 0 && event.getRegular() != 0 && event.getVip() != 0) {
                if (event.getAdvance() == event.getRegular() && event.getRegular() == event.getVip()) {
                    advanceBar.setVisibility(View.GONE);
                    vipBar.setVisibility(View.GONE);
                }
                tvAdvance.setText("Ksh "+event.getAdvance());
                tvRegular.setText("Ksh "+event.getRegular());
                tvVip.setText("Ksh " + event.getVip());
            }
            notFree.setVisibility(View.VISIBLE);
            tvFree.setVisibility(View.GONE);
        }

        footerViewContent.setVisibility(View.VISIBLE);
        detailsLoader.setVisibility(View.GONE);
    }

    private void mParseResponse(JSONObject response){

        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){

                JSONObject data = response.getJSONArray(Response.DATA).getJSONObject(0);
                event.setPromoter(data.getString(Response.EventData.PROMOTER));
                event.setOrganizer(data.getString(Response.EventData.ORGANIZER));
                event.setParkingInfo(data.getString(Response.EventData.PARKING_INFO));
                event.setSecurityInfo(data.getString(Response.EventData.SECURITY_INFO));
                event.setAdvance(data.getInt(Response.EventData.ADVANCE));
                event.setRegular(data.getInt(Response.EventData.REGULAR));
                event.setVip(data.getInt(Response.EventData.VIP));
                event.setHasLoadedDetails(1);
                DataController.getInstance(this).sendBroadCastOnEventDataChanged(event);
                showDetails();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        MenuItem shareItem = menu.findItem(R.id.activity_details_action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getShareIntent());
        return super.onCreateOptionsMenu(menu);
    }

    private Intent getShareIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi there, "
                + event.getName() + " is an event happening on "
                + event.getDate() + " from "
                + event.getTime() + " at "
                + event.getVenue()+"\n"
                +getString(R.string.app_download_info)+" "
                +getString(R.string.company_website));
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.activity_details_action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        detailsLoader.startSpinning();
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        Helper.toast(this, "Unable to load details, Please check your network status.");
        detailsLoader.stopSpinning();
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        detailsLoader.stopSpinning();
        mParseResponse(response);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Picasso.with(this).pauseTag(DetailsActivity.class.getSimpleName());
        APIConnector.getInstance(this).getClient().cancelRequestsByTAG(DetailsActivity.class.getSimpleName(), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(this).resumeTag(DetailsActivity.class.getSimpleName());
    }
}

