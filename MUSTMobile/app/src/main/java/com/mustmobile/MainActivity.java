package com.mustmobile;

import android.preference.PreferenceManager;
import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.database.MySuggestionProvider;
import com.mustmobile.fragment.academics.AcademicsFragment;
import com.mustmobile.fragment.academics.EClassFragment;
import com.mustmobile.fragment.timetable.ExamTimetableFragment;
import com.mustmobile.fragment.accomodation.AccommodationFragment;
import com.mustmobile.fragment.EmailFragment;
import com.mustmobile.fragment.finance.FinanceFragment;
import com.mustmobile.fragment.AboutFragment;
import com.mustmobile.fragment.timetable.TeachingTimetableFragment;
import com.mustmobile.fragment.library.BooksFragment;
import com.mustmobile.fragment.MainFragment;
import com.mustmobile.fragment.media.EventsFragment;
import com.mustmobile.fragment.media.GalleryFragment;
import com.mustmobile.fragment.media.NewsFragment;
import com.mustmobile.fragment.exchanges.ForumsFragment;
import com.mustmobile.fragment.StaffFragment;
import com.mustmobile.fragment.library.NotesFragment;
import com.mustmobile.fragment.library.PastpapersFragment;
import com.mustmobile.fragment.media.VideosFragment;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.service.UpdatesCheckService;
import com.mustmobile.util.FragmentHelp;
import com.mustmobile.util.Helper;
import com.mustmobile.util.IntentKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private boolean doubleBackToExitPressedOnce = false;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        try {
            startService(new Intent(this, UpdatesCheckService.class));
        } catch (Exception e){
            e.printStackTrace();
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFirstRun();
    }

    @Override
    public void onNavigationDrawerItemSelected(int item) {
        // update the menu_activity_main content by replacing fragments
        switch (item){
            case FragmentHelp.FRAGMENT_MAIN:
                replaceFragment(new MainFragment().newInstance(FragmentHelp.FRAGMENT_MAIN));
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_BOOKS:
                replaceFragment(new BooksFragment().newInstance(FragmentHelp.FRAGMENT_LIBRARY_BOOKS));
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_NOTES:
                replaceFragment(new NotesFragment().newInstance(FragmentHelp.FRAGMENT_LIBRARY_NOTES));
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_PASTPAPERS:
                replaceFragment(new PastpapersFragment().newInstance(FragmentHelp.FRAGMENT_LIBRARY_PASTPAPERS));
                break;
            case FragmentHelp.FRAGMENT_ACADEMICS:
                replaceFragment(new AcademicsFragment().newInstance(FragmentHelp.FRAGMENT_ACADEMICS));
                break;
            case FragmentHelp.FRAGMENT_TEACHING_TIMETABLE:
                replaceFragment(new TeachingTimetableFragment().newInstance(FragmentHelp.FRAGMENT_TEACHING_TIMETABLE));
                break;
            case FragmentHelp.FRAGMENT_EXAM_TIMETABLE:
                replaceFragment(new ExamTimetableFragment().newInstance(FragmentHelp.FRAGMENT_EXAM_TIMETABLE));
                break;
            case FragmentHelp.FRAGMENT_ECLASS:
                replaceFragment(new EClassFragment().newInstance(FragmentHelp.FRAGMENT_ECLASS));
                //startActivity(new Intent(this, EClassActivity.class));
                break;
            case FragmentHelp.FRAGMENT_EXCHANGE:
                replaceFragment(new ForumsFragment().newInstance(FragmentHelp.FRAGMENT_EXCHANGE));
                break;
            case FragmentHelp.FRAGMENT_NEWS:
                replaceFragment(new NewsFragment().newInstance(FragmentHelp.FRAGMENT_NEWS));
                break;
            case FragmentHelp.FRAGMENT_EVENTS:
                replaceFragment(new EventsFragment().newInstance(FragmentHelp.FRAGMENT_EVENTS));
                break;
            case FragmentHelp.FRAGMENT_GALLERY:
                replaceFragment(new GalleryFragment().newInstance(FragmentHelp.FRAGMENT_GALLERY));
                break;
            case FragmentHelp.FRAGMENT_VIDEOS:
                replaceFragment(new VideosFragment().newInstance(FragmentHelp.FRAGMENT_VIDEOS));
                break;
            case FragmentHelp.FRAGMENT_FINANCE:
                replaceFragment(new FinanceFragment().newInstance(FragmentHelp.FRAGMENT_FINANCE));
                break;
            case FragmentHelp.FRAGMENT_STAFF:
                replaceFragment(new StaffFragment().newInstance(FragmentHelp.FRAGMENT_STAFF));
                break;
            case FragmentHelp.FRAGMENT_EMAIL:
                replaceFragment(new EmailFragment().newInstance(FragmentHelp.FRAGMENT_EMAIL));
                break;
            case FragmentHelp.FRAGMENT_CAMPUS_MAP:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case FragmentHelp.FRAGMENT_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case FragmentHelp.FRAGMENT_ABOUT:
                replaceFragment(new AboutFragment().newInstance(FragmentHelp.FRAGMENT_ABOUT));
                break;
            case FragmentHelp.FRAGMENT_ACCOMODATION:
                replaceFragment(new AccommodationFragment().newInstance(FragmentHelp.FRAGMENT_ACCOMODATION));
                break;
        }
    }

    public void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_main_fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    public void onSectionAttached(int item) {
        switch (item) {
            case FragmentHelp.FRAGMENT_MAIN:
                mTitle = getString(R.string.title_main_fragment);
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_BOOKS:
                mTitle = getString(R.string.title_library_books_fragment);
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_NOTES:
                mTitle = getString(R.string.title_library_notes_fragment);
                break;
            case FragmentHelp.FRAGMENT_LIBRARY_PASTPAPERS:
                mTitle = getString(R.string.title_library_pastpapers_fragment);
                break;
            case FragmentHelp.FRAGMENT_ACADEMICS:
                mTitle = getString(R.string.title_academics_fragment);
                break;
            case FragmentHelp.FRAGMENT_TEACHING_TIMETABLE:
                mTitle = getString(R.string.title_teaching_timetable_fragment);
                break;
            case FragmentHelp.FRAGMENT_EXAM_TIMETABLE:
                mTitle = getString(R.string.title_exam_timetable_fragment);
                break;
            case FragmentHelp.FRAGMENT_ECLASS:
                mTitle = getString(R.string.title_eclass_fragment);
                break;
            case FragmentHelp.FRAGMENT_EXCHANGE:
                mTitle = getString(R.string.title_exchange_fragment);
                break;
            case FragmentHelp.FRAGMENT_NEWS:
                mTitle = getString(R.string.title_news_fragment);
                break;
            case FragmentHelp.FRAGMENT_EVENTS:
                mTitle = getString(R.string.title_events_fragment);
                break;
            case FragmentHelp.FRAGMENT_GALLERY:
                mTitle = getString(R.string.title_gallery_fragment);
                break;
            case FragmentHelp.FRAGMENT_VIDEOS:
                mTitle = getString(R.string.title_videos_fragment);
                break;
            case FragmentHelp.FRAGMENT_STUDENT_UNION:
                mTitle = getString(R.string.title_student_union_fragment);
                break;
            case FragmentHelp.FRAGMENT_STAFF:
                mTitle = getString(R.string.title_staff_fragment);
                break;
            case FragmentHelp.FRAGMENT_FINANCE:
                mTitle = getString(R.string.title_finance_fragment);
                break;
            case FragmentHelp.FRAGMENT_EMAIL:
                mTitle = getString(R.string.title_email_fragment);
                break;
            case FragmentHelp.FRAGMENT_CAMPUS_MAP:
                mTitle = getString(R.string.title_campus_map_fragment);
                break;
            case FragmentHelp.FRAGMENT_SETTINGS:
                mTitle = getString(R.string.title_settings_fragment);
                break;
            case FragmentHelp.FRAGMENT_ACCOMODATION:
                mTitle = getString(R.string.title_accomodation_fragment);
                break;
            case FragmentHelp.FRAGMENT_DIGITAL_COLLECTIONS:
                mTitle = getString(R.string.title_digital_collections_fragment);
                restoreActionBar();
                break;
            case FragmentHelp.FRAGMENT_EJOURNALS:
                mTitle = getString(R.string.title_ejournals_fragment);
                restoreActionBar();
                break;
            case FragmentHelp.FRAGMENT_PROGRAMS:
                mTitle = getString(R.string.title_programs_fragment);
                restoreActionBar();
                break;
            case FragmentHelp.FRAGMENT_DOWNLOADS:
                mTitle = getString(R.string.title_downloads_fragment);
                restoreActionBar();
                break;
            case FragmentHelp.FRAGMENT_SEMESTER_DATES:
                mTitle = getString(R.string.title_semester_dates_fragment);
                restoreActionBar();
                break;
            case FragmentHelp.FRAGMENT_ABOUT:
                mTitle = getString(R.string.title_about_fragment);
                restoreActionBar();
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setSubtitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_activity_main, menu);
            //hideSearchMenuItem();
            restoreActionBar();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_activity_main_action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_activity_main_action_feedback){
            startActivity(new Intent(this, FeedbackActivity.class));
            return true;
        } else if(id == R.id.menu_activity_main_action_my_infor){
            startActivity(new Intent(this, ChangeInformationActivity.class));
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }

    }

    public void checkFirstRun(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = sp.getBoolean(getString(R.string.pref_is_first_run), true);
        if (isFirstRun) {
            startActivity(new Intent(getApplicationContext(), StarterActivity.class));
            this.finish();
        } else {
            checkObsolete();
        }
    }

    public void checkObsolete(){

        Client.post(Client.absoluteUrl("checkobsolete"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseObsoleteResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Helper.at(getApplicationContext()).simpleToast("Couldn't refresh updates!");
                checkObsolete();
            }
        });
    }

    private void parseObsoleteResponse(JSONObject response){
        int installedVersionCode = 0;
        int latestVersionCode = 0;

        String installedVersionName = null, latestVersionName, message, downloadLink, releaseDate;
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
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
                    Log.d("Toshde", "Installed versionCode "+installedVersionCode);
                    Log.d("Toshde", "Installed versionName "+installedVersionName);
                    Log.d("Toshde", "Latest versionCode "+latestVersionCode);
                    Log.d("Toshde", "Latest versionName "+latestVersionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (installedVersionCode != latestVersionCode){
                    Intent intent = new Intent(this, ObsoleteActivity.class);
                    intent.putExtra(IntentKey.INSTALLED_VERSION_CODE,String.valueOf(installedVersionCode));
                    intent.putExtra(IntentKey.INSTALLED_VERSION_NAME, installedVersionName);
                    intent.putExtra(IntentKey.LATEST_VERSION_CODE, String.valueOf(latestVersionCode));
                    intent.putExtra(IntentKey.LATEST_VERSION_NAME, latestVersionName);
                    intent.putExtra(IntentKey.MESSAGE, message);
                    intent.putExtra(IntentKey.DOWNLOAD_LINK, downloadLink);
                    intent.putExtra(IntentKey.RELEASE_DATE, releaseDate);
                    startActivity(intent);
                    this.finish();
                } else {
                    return;
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeCoverImage(View view){
        Intent intent = new Intent(this, CoverImageChooserActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchRecentSuggestions suggestions = new
                SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    @Override
    public void onBackPressed() {
        replaceFragment(new MainFragment().newInstance(FragmentHelp.FRAGMENT_MAIN));
        if (doubleBackToExitPressedOnce ){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        //Helper.at(this).simpleToast("Press again to exit");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void showUnImplemented(View view){
        Helper.at(getApplicationContext()).simpleToast("Check out this later!!!");
    }

}
