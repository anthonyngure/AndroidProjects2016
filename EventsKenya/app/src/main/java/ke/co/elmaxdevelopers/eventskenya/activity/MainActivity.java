package ke.co.elmaxdevelopers.eventskenya.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.adapter.SectionsPagerAdapter;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.fragment.EventsListFragment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.model.Queue;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.QueueWorkListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements QueueWorkListener {

    private DrawerLayout mDrawerLayout;
    private LocalDate localDate = new LocalDate();
    private AlertDialog dialog;
    private ArrayList<Queue> queueArrayList;
    private boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataController.getInstance(this).sweep();
        DataController.getInstance(this).cleanQueues();
        DataController.getInstance(this).cleanEvents();
        queueArrayList = DataController.getInstance(this).getQueues();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenuDialog();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        workOnQueues();
        checkObsoletes();
    }

    private void setupViewPager(ViewPager viewPager) {
        CharSequence[] mTitles = {"Today", setTabDate(1), setTabDate(2), setTabDate(3), setTabDate(4), setTabDate(5)};
        SectionsPagerAdapter mAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mTitles, 6);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(mAdapter.getCount());
        viewPager.setAdapter(mAdapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        setupDrawerHeader();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        if (menuItem.getItemId() == R.id.drawer_action_auto_download_posters) {
                            if (PrefUtils.getInstance(getApplicationContext()).autoDownloadPosters()) {
                                menuItem.setChecked(true);
                            } else {
                                menuItem.setChecked(false);
                            }
                        }
                        mDrawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.activity_main_action_create_event:
                                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
                                return true;
                            case R.id.activity_main_action_settings:
                                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                return true;
                            case R.id.activity_main_action_more_services:
                                Intent serviceIntent = new Intent(getApplicationContext(), ServicesActivity.class);
                                serviceIntent.putExtra(ServicesActivity.KEY_SERVICE_TYPE, Service.EVENT_SERVICES);
                                startActivity(serviceIntent);
                                return true;
                            case R.id.activity_main_action_more_organizers:
                                Intent organizersIntent = new Intent(getApplicationContext(), ServicesActivity.class);
                                organizersIntent.putExtra(ServicesActivity.KEY_SERVICE_TYPE, Service.EVENT_ORGANIZER);
                                startActivity(organizersIntent);
                                return true;
                            case R.id.activity_main_action_add_your_card:
                                startActivity(new Intent(getApplicationContext(), NewCardActivity.class));
                                return true;
                            case R.id.activity_main_action_more_entertainers:
                                Intent entertainersIntent = new Intent(getApplicationContext(), ServicesActivity.class);
                                entertainersIntent.putExtra(ServicesActivity.KEY_SERVICE_TYPE, Service.EVENT_ENTERTAINER);
                                startActivity(entertainersIntent);
                                return true;
                            case R.id.drawer_action_help_and_feedback:
                                startActivity(new Intent(getApplicationContext(), HelpAndFeedbackActivity.class));
                                return true;
                            case R.id.drawer_action_settings:
                                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                return true;
                            case R.id.drawer_action_change_category:
                                selectCategoryDialog();
                                return true;
                            case R.id.drawer_action_change_county:
                                selectCountyDialog();
                                return true;
                            case R.id.drawer_action_auto_download_posters:
                                if (PrefUtils.getInstance(getApplicationContext()).autoDownloadPosters()) {
                                    menuItem.setChecked(false);
                                    PrefUtils.getInstance(getApplicationContext()).writePrefAutoDownloadPosters(false);
                                } else {
                                    menuItem.setChecked(true);
                                    PrefUtils.getInstance(getApplicationContext()).writePrefAutoDownloadPosters(true);
                                }
                                PrefUtils.getInstance(getApplicationContext()).invalidate();
                                DataController.getInstance(getApplicationContext()).sendBroadCastOnEventDataChanged(new Event());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
    }

    private void setupDrawerHeader(){
        ((TextView)mDrawerLayout.findViewById(R.id.username))
                .setText(PrefUtils.getInstance(this).getSetUsername());

    }

    private String setTabDate(int daysToAdd){
        int day = localDate.plusDays(daysToAdd).getDayOfMonth();
        int month = localDate.plusDays(daysToAdd).getMonthOfYear();
        return month+"/"+day;
    }

    private void showMenuDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_menu, null);
        ((LinearLayout) view.findViewById(R.id.new_event)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuDialog();
                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
            }
        });
        ((LinearLayout) view.findViewById(R.id.service_card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuDialog();
                startActivity(new Intent(getApplicationContext(), NewCardActivity.class));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void hideMenuDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void workOnQueues(){
        if (queueArrayList.size() > 0 && queueArrayList != null){
            for (Queue q : queueArrayList){
                if (q.getOperation() == Queue.GOING_UPDATE){
                    APIConnector.getInstance(this).addGoing(this, q);
                } else {
                    APIConnector.getInstance(this).addInterested(this, q);
                }
            }
        }
    }

    private void checkObsoletes(){
        long lastObsoleteCheck = PrefUtils.getInstance(this).getLastObsoleteCheck();
        if(lastObsoleteCheck < DateUtils.getIntegerDate(new LocalDate().minusWeeks(2).toString())){
            APIConnector.getInstance(this).checkObsolete(new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    parseObsoleteResponse(response);
                }

            });
        } else {
            if (!PrefUtils.getInstance(this).hasDownloadedNewVersion()){
                APIConnector.getInstance(this).checkObsolete(new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        parseObsoleteResponse(response);
                    }

                });
            }
        }
    }

    private void parseObsoleteResponse(JSONObject response){
        int installedVersionCode = 0;
        int latestVersionCode = 0;

        String installedVersionName = null, latestVersionName, message, downloadLink, releaseDate;
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase(Response.SUCCESS)){
                JSONArray versionInfor = response.getJSONArray(Response.DATA);
                JSONObject info = versionInfor.getJSONObject(0);
                latestVersionCode = info.getInt(Response.VersionControlData.VERSION_CODE);
                latestVersionName = info.getString(Response.VersionControlData.VERSION_NAME);
                message = info.getString(Response.VersionControlData.MESSAGE);
                downloadLink = info.getString(Response.VersionControlData.DOWNLOAD_LINK);
                releaseDate = info.getString(Response.VersionControlData.RELEASE_DATE);
                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    installedVersionCode = packageInfo.versionCode;
                    installedVersionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (installedVersionCode != latestVersionCode){

                    PrefUtils.getInstance(this).writeObsoleteLastCheck(DateUtils.getIntegerDate(new LocalDate().toString()));
                    PrefUtils.getInstance(this).writeHasNotDownloadedNewVersion();
                    Intent intent = new Intent(this, ObsoleteActivity.class);
                    intent.putExtra(ObsoleteActivity.INSTALLED_VERSION_CODE,String.valueOf(installedVersionCode));
                    intent.putExtra(ObsoleteActivity.INSTALLED_VERSION_NAME, installedVersionName);
                    intent.putExtra(ObsoleteActivity.LATEST_VERSION_CODE, String.valueOf(latestVersionCode));
                    intent.putExtra(ObsoleteActivity.LATEST_VERSION_NAME, latestVersionName);
                    intent.putExtra(ObsoleteActivity.MESSAGE, message);
                    intent.putExtra(ObsoleteActivity.DOWNLOAD_LINK, downloadLink);
                    intent.putExtra(ObsoleteActivity.RELEASE_DATE, releaseDate);
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFirstRun();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.activity_main_action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.activity_main_action_create_event:
                startActivity(new Intent(this, NewEventActivity.class));
                return true;
            case R.id.activity_main_action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchResultsActivity.SEARCH_CONTEXT,
                    SearchResultsActivity.EVENTS_SEARCH);
        }
        super.startActivity(intent);
    }

    private void checkFirstRun() {

        if (PrefUtils.getInstance(this).isFirstRun()){
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        } else {
            checkStarterSettings();
        }
    }

    private void checkStarterSettings(){
        if (!PrefUtils.getInstance(this).hasFinishedStarterSettings()){
            startActivity(new Intent(this, StarterSettingsActivity.class));
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            PrefUtils.getInstance(this).invalidate();
            DataController.getInstance(this).invalidate();
            APIConnector.getInstance(this).getClient().cancelAllRequests(true);
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(mDrawerLayout, getString(R.string.back_press_exit), Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode, Queue queue) {
        //DataController.getInstance(this).addQueue(queue);
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response, Queue queue) {
        DataController.getInstance(this).removeQueue(queue);
    }

    private void selectCategoryDialog() {
        final String[] categories = getResources().getStringArray(R.array.categories);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_category))
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtils.getInstance(getApplicationContext()).writeSetCategory(categories[which]);
                        DataController.getInstance(getApplicationContext()).forceSweep();
                        PrefUtils.getInstance(getApplicationContext()).invalidate();
                        DataController.getInstance(getApplicationContext()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();
    }

    private void selectCountyDialog(){
        final CharSequence[] counties = getResources().getStringArray(R.array.counties);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_county))
                .setItems(counties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtils.getInstance(getApplicationContext()).writeSetCounty(String.valueOf(counties[which]));
                        DataController.getInstance(getApplicationContext()).forceSweep();
                        PrefUtils.getInstance(getApplicationContext()).invalidate();
                        DataController.getInstance(getApplicationContext()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        PrefUtils.getInstance(this).invalidate();
        DataController.getInstance(this).invalidate();
        APIConnector.getInstance(this).getClient().cancelAllRequests(true);
        super.onDestroy();
    }
}
