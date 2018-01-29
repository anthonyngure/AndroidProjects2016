package ke.co.elmaxdevelopers.eventskenya.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.ImageLoadingUtils;
import ke.co.elmaxdevelopers.eventskenya.views.SquaredImageView;

public class NewCardPreviewActivity extends AppCompatActivity implements LoadingListener {

    public static final String EXTRA_SERVICE = "service";
    public static final String EXTRA_IMAGE_ONE_PATH = "image_one_path";
    public static final String EXTRA_IMAGE_TWO_PATH = "image_two_path";
    private String imageOnePath, imageTwoPath;
    private Service service;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card_preview);
        ImageLoadingUtils utils = new ImageLoadingUtils(this);
        if (getIntent().getExtras() != null){
            ArrayList<Service> services =  getIntent().getParcelableArrayListExtra(EXTRA_SERVICE);
            service = services.get(0);
            imageOnePath = getIntent().getExtras().getString(EXTRA_IMAGE_ONE_PATH);
            imageTwoPath = getIntent().getExtras().getString(EXTRA_IMAGE_TWO_PATH);
        }

        Button messageButton = (Button) findViewById(R.id.list_item_service_message);
        Button callButton = (Button) findViewById(R.id.list_item_service_call);
        Button emailButton = (Button) findViewById(R.id.list_item_service_email);
        Button saveButton = (Button) findViewById(R.id.list_item_service_save_button);

        TextView tvName = (TextView) findViewById(R.id.list_item_service_name);
        TextView tvDescription = (TextView) findViewById(R.id.list_item_service_description);

        SquaredImageView imageOne = (SquaredImageView) findViewById(R.id.list_item_service_one);
        SquaredImageView imageTwo = (SquaredImageView) findViewById(R.id.list_item_service_two);

        imageOne.setImageBitmap(utils.decodeBitmapFromPath(imageOnePath));
        imageTwo.setImageBitmap(utils.decodeBitmapFromPath(imageTwoPath));

        tvName.setText(service.getName());
        tvDescription.setText(service.getAbout());

        saveButton.setEnabled(false);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SENDTO);
                messageIntent.addCategory(Intent.CATEGORY_DEFAULT);
                messageIntent.setType("vnd.android-dir/mms-sms");
                messageIntent.setData(Uri.parse("sms:" + service.getPhone()));

                if (messageIntent.resolveActivity(NewCardPreviewActivity.this.getPackageManager()) != null) {
                    NewCardPreviewActivity.this.startActivity(messageIntent);
                } else {
                    Helper.toast(getApplicationContext(), "Unable to find a messaging App.");
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + service.getPhone()));
                if (callIntent.resolveActivity(NewCardPreviewActivity.this.getPackageManager()) != null) {
                    NewCardPreviewActivity.this.startActivity(callIntent);
                } else {
                    Helper.toast(getApplicationContext(), "Unable to find a messaging App.");
                }
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + service.getEmail()));

                if (emailIntent.resolveActivity(NewCardPreviewActivity.this.getPackageManager()) != null) {
                    NewCardPreviewActivity.this.startActivity(emailIntent);
                } else {
                    Helper.toast(getApplicationContext(), "Unable to find an email App.");
                }

            }
        });

    }

    private void submitCard(){
        RequestParams params = new RequestParams();
        params.put(Response.ServiceData.NAME, service.getName());
        params.put(Response.ServiceData.ABOUT, service.getAbout());
        params.put(Response.ServiceData.PHONE, service.getPhone());
        params.put(Response.ServiceData.EMAIL, service.getEmail());
        params.put(Response.ServiceData.SERVICE_TYPE, service.getServiceType());
        params.put(Response.ServiceData.IMAGE_ONE, service.getImageOneUrl());
        params.put(Response.ServiceData.IMAGE_TWO, service.getImageTwoUrl());
        params.put("image_one_name", service.getName().toString().replace(" ","_").replace(".","")+System.currentTimeMillis()+"101.jpg");
        params.put("image_two_name", service.getName().toString().replace(" ","_").replace(".","")+System.currentTimeMillis()+"202.jpg");
        APIConnector.getInstance(this).addService(params, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_new_card_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.activity_new_card_preview_action_submit) {
            submitCard();
            return true;
        } else if (item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initProgressDialog(){
        hideProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending your card, Please wait...");
        progressDialog.show();
    }

    private void hideProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        hideProgressDialog(); //Case of retrying
        initProgressDialog();
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        hideProgressDialog();
        showErrorAlertDialog();
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase(Response.SUCCESS)){
                showSuccessAlertDialog();
            } else {
                showErrorAlertDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showErrorAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Network connection failed!")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitCard();
                    }
                })
                .setNegativeButton("Switch on data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        NewCardPreviewActivity.this.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showSuccessAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Card submitted successfully\n" +
                "An email will be sent to you immediately it\'s approved.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewCardPreviewActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

}
