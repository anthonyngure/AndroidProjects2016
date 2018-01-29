package ke.co.elmaxdevelopers.eventskenya.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ke.co.elmaxdevelopers.eventskenya.R;


public class ObsoleteActivity extends AppCompatActivity {

    public static final String INSTALLED_VERSION_CODE = "installed_version_code";
    public static final String INSTALLED_VERSION_NAME = "installed_version_name";
    public static final String LATEST_VERSION_CODE = "latest_version_code";
    public static final String LATEST_VERSION_NAME = "latest_version_name";
    public static final String MESSAGE = "message";
    public static final String DOWNLOAD_LINK = "download_link";
    public static final String RELEASE_DATE = "release_date";

    private String installedVersionCode, latestVersionCode;
    private String installedVersionName, latestVersionName, message, downloadLink, releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obsolete);

        if (getIntent().getExtras() != null) {
            installedVersionCode = resolveIntentValue(INSTALLED_VERSION_CODE);
            installedVersionName = resolveIntentValue(INSTALLED_VERSION_NAME);
            latestVersionCode = resolveIntentValue(LATEST_VERSION_CODE);
            latestVersionName = resolveIntentValue(LATEST_VERSION_NAME);
            message = resolveIntentValue(MESSAGE);
            downloadLink = resolveIntentValue(DOWNLOAD_LINK);
            releaseDate = resolveIntentValue(RELEASE_DATE);
        }

        initControls();
    }

    private String resolveIntentValue(String key){
        return getIntent().getExtras().getString(key);
    }

    private void initControls(){
        TextView tvInstalledVersionInfor = (TextView) findViewById(R.id.activity_obsolete_installed_version);
        TextView tvLatestVersionInfor = (TextView) findViewById(R.id.activity_obsolete_latest_version);
        TextView tvReleaseDate = (TextView) findViewById(R.id.activity_obsolete_release_date);
        TextView tvMessage = (TextView) findViewById(R.id.activity_obsolete_message);

        tvInstalledVersionInfor.setText("Version Code : " + installedVersionCode
                + "     Version Name : " + installedVersionName);
        tvLatestVersionInfor.setText("Version Code : " + latestVersionCode
                + "     Version Name : " + latestVersionName);
        tvReleaseDate.setText("AS FROM " + releaseDate);
        tvMessage.setText(message);
    }

    public void downloadNewVersion(View view){
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink));
        startActivity(downloadIntent);
    }
}
