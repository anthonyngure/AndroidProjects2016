package ke.co.elmaxdevelopers.eventskenya.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.ImageLoadingUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

public class NewEventPreviewActivity extends AppCompatActivity implements LoadingListener {

    public static final String EXTRA_EVENT = "event";
    public static final String EXTRA_IMAGE_PATH = "image_path";
    public static final String EXTRA_ENCODED_IMAGE = "encoded_image_string";
    private static final int IMAGE_ERROR = 0;
    private static final int EVENT_ERROR = 1;
    public static final String EXTRA_ENCODED_THUMBNAIL = "encoded_thumbnail";

    private ViewGroup footerViewContent;
    private Event event;
    private String imagePath;
    private String encodedImageString;
    private String encodedThumbnailString;
    private ProgressDialog pDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_event_preview);

        if (getIntent().getExtras() != null){
            ArrayList<Event> events =  getIntent().getParcelableArrayListExtra(EXTRA_EVENT);
            imagePath = getIntent().getExtras().getString(EXTRA_IMAGE_PATH);
            encodedImageString = getIntent().getExtras().getString(EXTRA_ENCODED_IMAGE);
            encodedThumbnailString = getIntent().getExtras().getString(EXTRA_ENCODED_THUMBNAIL);
            event = events.get(0);
        }

        TextView tvName = (TextView) findViewById(R.id.list_item_event_name);
        TextView tvVenue = (TextView) findViewById(R.id.list_item_event_venue);
        TextView tvTime = (TextView) findViewById(R.id.list_item_event_time);
        TextView tvDescription = (TextView) findViewById(R.id.list_item_event_description);
        ImageView imageView = (ImageView) findViewById(R.id.list_item_event_image);
        TextView tvDate = (TextView) findViewById(R.id.list_item_event_date);

        findViewById(R.id.list_item_event_no_going).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_no_interested).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_button_write_comment).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_button_details).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_button_going).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_following_going_update_progress).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_button_follow).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_overflow).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_image_loader).setVisibility(View.GONE);
        findViewById(R.id.list_item_event_thumbnail_overlay).setVisibility(View.GONE);

        footerViewContent = (ViewGroup) findViewById(R.id.details_activity_footerview_content);

        tvName.setText(event.getName());
        tvVenue.setText(event.getVenue());
        tvDescription.setText(event.getDescription());
        tvTime.setText(event.getTime());
        tvDate.setText(DateUtils.sortDates(event.getStringStartDate(), event.getStringEndDate()));

        ImageLoadingUtils utils = new ImageLoadingUtils(this);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(utils.decodeBitmapFromPath(imagePath));
        showDetails();

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
        tvCreator.setText(event.getOrganizer());
        tvSecurity.setText(event.getSecurityInfo());

        tvBurTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyTicketIntent = new Intent(Intent.ACTION_VIEW);
                String url = event.getTicketsLink();
                if (!url.isEmpty()) {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    buyTicketIntent.setData(Uri.parse(url));
                    if (buyTicketIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(buyTicketIntent);
                    }
                }

            }
        });

        TextView tvFree = (TextView) findViewById(R.id.details_activity_footerview_tickets_free);
        ViewGroup notFree = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_not_free);
        ViewGroup advanceBar = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_advance_bar);
        ViewGroup vipBar = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_vip_bar);
        ViewGroup regularBar = (ViewGroup) findViewById(R.id.details_activity_footerview_tickets_regular_bar);

        TextView tvAdvance = (TextView) findViewById(R.id.details_activity_footerview_advance);
        TextView tvRegular = (TextView) findViewById(R.id.details_activity_footerview_regular);
        TextView tvVip = (TextView) findViewById(R.id.details_activity_footerview_vip);

        if (event.getAdvance() == 0 && event.getRegular() == 0 && event.getVip() == 0){
            tvFree.setVisibility(View.VISIBLE);
            notFree.setVisibility(View.GONE);
        } else {
            if (event.getAdvance() == event.getRegular() && event.getRegular() == event.getVip()) {
                advanceBar.setVisibility(View.GONE);
                vipBar.setVisibility(View.GONE);
            }
            if (event.getVip() == 0){
                vipBar.setVisibility(View.GONE);
            }
            if (event.getAdvance() == 0){
                advanceBar.setVisibility(View.GONE);
            }
            if (event.getRegular() == 0){
                regularBar.setVisibility(View.GONE);
            }
            tvAdvance.setText("Ksh "+event.getAdvance());
            tvRegular.setText("Ksh "+event.getRegular());
            tvVip.setText("Ksh " + event.getVip());
            notFree.setVisibility(View.VISIBLE);
            tvFree.setVisibility(View.GONE);
        }
        footerViewContent.setVisibility(View.VISIBLE);
    }

    private void submitEvent(){
        RequestParams params = new RequestParams();
        params.add(Response.EventData.NAME, event.getName());
        params.add(Response.EventData.VENUE, event.getVenue());
        params.add(Response.EventData.DESCRIPTION, event.getDescription());
        params.add(Response.EventData.TIME, event.getTime());
        params.add(Response.EventData.START_DATE, event.getStringStartDate());
        params.add(Response.EventData.END_DATE, event.getStringEndDate());
        params.add(Response.EventData.ADVANCE, String.valueOf(event.getAdvance()));
        params.add(Response.EventData.REGULAR, String.valueOf(event.getRegular()));
        params.add(Response.EventData.VIP, String.valueOf(event.getVip()));
        params.add(Response.EventData.IMAGE, event.getImageUrl());
        params.add(Response.EventData.CATEGORY, event.getCategory());
        params.add(Response.EventData.COUNTY, event.getCounty());
        params.add(Response.EventData.PROMOTER, event.getPromoter());
        params.add(Response.EventData.ORGANIZER, event.getOrganizer());
        params.add(Response.EventData.PARKING_INFO, event.getParkingInfo());
        params.add(Response.EventData.SECURITY_INFO, event.getSecurityInfo());
        params.add(Response.EventData.TICKETS_LINK, event.getTicketsLink());
        params.add(BackEnd.PARAM_USERNAME, PrefUtils.getInstance(this).getSetUsername());
        APIConnector.getInstance(this).addEvent(params, this);

    }

    private void submitImage(){
        RequestParams params = new RequestParams();
        params.put("image", encodedImageString);
        params.put("thumbnail", encodedThumbnailString);
        params.put("filename", event.getImageUrl());
        APIConnector.getInstance(this).sendImage(BackEnd.absoluteUrl(BackEnd.ADD_IMAGE), params,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onStart() {
                        super.onStart();
                        pDialog.setMessage("Sending image now, Please wait...");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        dismissPdialog();
                        showSucceededDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        dismissPdialog();
                        showSubmittingErrorDialog(IMAGE_ERROR);
                        Log.d("Toshde", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        dismissPdialog();
                        showSubmittingErrorDialog(IMAGE_ERROR);
                        Log.d("Toshde", responseString.toString());
                    }
                });

    }

    public void showSubmittingErrorDialog(final int errorType){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Network connection failed!")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (errorType){
                            case IMAGE_ERROR:
                                submitImage();
                                break;
                            case EVENT_ERROR:
                                createPdialog();
                                submitEvent();
                                break;
                        }
                    }
                })
                .setNegativeButton("Switch on data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        NewEventPreviewActivity.this.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showSucceededDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Event submitted successfully\nYou will be notified to your email when it\'s approved.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewEventPreviewActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_new_event_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_preview_action_submit:
                submitEvent();
                return true;
            case R.id.activity_preview_action_edit:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dismissPdialog(){
        if (pDialog != null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        dismissPdialog();
        createPdialog();
        pDialog.setMessage("Submitting your event, Please wait...");

    }

    private void createPdialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        dismissPdialog();
        showSubmittingErrorDialog(EVENT_ERROR);
        Log.d("Toshde", "Error code "+statusCode);
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){
                Helper.toast(this, response.getString(Response.MESSAGE));
                submitImage();
            } else {
                Helper.toast(this, getString(R.string.unknown_error));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Helper.toast(this, getString(R.string.unknown_error));
        }
    }
}

