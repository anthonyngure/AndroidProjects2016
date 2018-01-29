package com.mustmobile;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import com.mustmobile.util.IntentKey;

public class ObsoleteActivity extends AppCompatActivity {

    private String installedVersionCode, latestVersionCode;
    private String installedVersionName, latestVersionName, message, downloadLink, releaseDate;
    private TextView tvLatestVersionInfor, tvInstalledVersionInfor, tvReleaseDate, tvMessage;
    private Button buttonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obsolete);

        if (getIntent().getExtras() != null) {
            installedVersionCode = resolveIntentValue(IntentKey.INSTALLED_VERSION_CODE);
            installedVersionName = resolveIntentValue(IntentKey.INSTALLED_VERSION_NAME);
            latestVersionCode = resolveIntentValue(IntentKey.LATEST_VERSION_CODE);
            latestVersionName = resolveIntentValue(IntentKey.LATEST_VERSION_NAME);
            message = resolveIntentValue(IntentKey.MESSAGE);
            downloadLink = resolveIntentValue(IntentKey.DOWNLOAD_LINK);
            releaseDate = resolveIntentValue(IntentKey.RELEASE_DATE);
        }

        initControls();
    }

    private String resolveIntentValue(String key){
        return getIntent().getExtras().getString(key);
    }

    private void initControls(){
        tvInstalledVersionInfor = (TextView) findViewById(R.id.activity_obsolete_installed_version);
        tvLatestVersionInfor = (TextView) findViewById(R.id.activity_obsolete_latest_version);
        tvReleaseDate = (TextView) findViewById(R.id.activity_obsolete_release_date);
        tvMessage = (TextView) findViewById(R.id.activity_obsolete_message);
        buttonDownload = (Button) findViewById(R.id.activity_obsolete_buttonDownload);

        tvInstalledVersionInfor.setText("Version Code : "+installedVersionCode
                +"     Version Name : "+installedVersionName);
        tvLatestVersionInfor.setText("Version Code : "+latestVersionCode
                +"     Version Name : "+latestVersionName);
        tvReleaseDate.setText("AS FROM " + releaseDate);
        tvMessage.setText(message);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadLatestAppVersion();
            }
        });
    }

    public void downloadLatestAppVersion(){
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
        startActivity(downloadIntent);
    }
}
