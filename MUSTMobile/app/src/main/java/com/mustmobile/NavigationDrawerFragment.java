package com.mustmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.network.Response;
import com.squareup.picasso.Picasso;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.util.AppStorage;
import com.mustmobile.util.FragmentHelp;
import com.mustmobile.util.Helper;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = FragmentHelp.FRAGMENT_MAIN;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private TextView actionAcademics, actionEClass, actionOverstack;
    private TextView actionLibraryBooks, getActionLibraryNotes, getActionLibraryPapers;
    private TextView actionTeachingTimetable, actionExamTimetable;
    private TextView actionMediaNews, actionMediaEvents, actionMediaGallery;
    private TextView actionStudentUnion, actionFinance, actionStaff;
    private TextView actionEmail, actionCampusMap, actionSettings, actionAbout;
    private TextView actionAccomodation, tvUserName, tvUserNumber;
    private ArrayList<TextView> actions = new ArrayList<>();
    private User user;
    private ImageView mProfile;
    private ViewGroup coverImageArea;

    private ProgressBar profileUpdateProgress;
    private String encodedString;
    private RequestParams params = new RequestParams();
    private String imgPath, profileImageName;
    private Bitmap bitmap;
    private static int RESULT_LOAD_IMG = 1;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.at(getActivity());
        profileImageName = user.getNumber().trim().replace("-","_").replace("/", "_") + ".png";
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        actionLibraryBooks = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_books);
        actions.add(actionLibraryBooks);
        actionLibraryBooks.setOnClickListener(getListener(FragmentHelp.FRAGMENT_LIBRARY_BOOKS));

        getActionLibraryNotes = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_notes);
        actions.add(getActionLibraryNotes);
        getActionLibraryNotes.setOnClickListener(getListener(FragmentHelp.FRAGMENT_LIBRARY_NOTES));

        getActionLibraryPapers = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_pastpapers);
        actions.add(getActionLibraryPapers);
        getActionLibraryPapers.setOnClickListener(getListener(FragmentHelp.FRAGMENT_LIBRARY_PASTPAPERS));

        actionAcademics = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_academics);
        actions.add(actionAcademics);
        actionAcademics.setOnClickListener(getListener(FragmentHelp.FRAGMENT_ACADEMICS));

        actionTeachingTimetable = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_teaching_timetable);
        actions.add(actionTeachingTimetable);
        actionTeachingTimetable.setOnClickListener(getListener(FragmentHelp.FRAGMENT_TEACHING_TIMETABLE));

        actionExamTimetable = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_exam_timetable);
        actions.add(actionExamTimetable);
        actionExamTimetable.setOnClickListener(getListener(FragmentHelp.FRAGMENT_EXAM_TIMETABLE));

        actionEClass = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_eclass);
        actions.add(actionEClass);
        actionEClass.setOnClickListener(getListener(FragmentHelp.FRAGMENT_ECLASS));

        actionOverstack = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_overstack);
        actions.add(actionOverstack);
        actionOverstack.setOnClickListener(getListener(FragmentHelp.FRAGMENT_EXCHANGE));

        actionMediaNews = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_news);
        actions.add(actionMediaNews);
        actionMediaNews.setOnClickListener(getListener(FragmentHelp.FRAGMENT_NEWS));

        actionMediaEvents = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_events);
        actions.add(actionMediaEvents);
        actionMediaEvents.setOnClickListener(getListener(FragmentHelp.FRAGMENT_EVENTS));

        actionMediaGallery = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_gallery);
        actions.add(actionMediaGallery);
        actionMediaGallery.setOnClickListener(getListener(FragmentHelp.FRAGMENT_GALLERY));

        actionStaff = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_staff);
        //actions.add(actionStaff);
        //actionStaff.setOnClickListener(getListener(FragmentHelp.FRAGMENT_STAFF));
        actionStaff.setBackgroundColor(Color.CYAN);

        actionFinance = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_finance);
        actions.add(actionFinance);
        actionFinance.setOnClickListener(getListener(FragmentHelp.FRAGMENT_FINANCE));

        actionEmail = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_email);
        //actions.add(actionEmail);
        //actionEmail.setOnClickListener(getListener(FragmentHelp.FRAGMENT_EMAIL));
        actionEmail.setBackgroundColor(Color.CYAN);

        actionCampusMap = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_campus_map);
        actions.add(actionCampusMap);
        actionCampusMap.setOnClickListener(getListener(FragmentHelp.FRAGMENT_CAMPUS_MAP));

        actionSettings = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_settings);
        actions.add(actionSettings);
        actionSettings.setOnClickListener(getListener(FragmentHelp.FRAGMENT_SETTINGS));

        actionAbout = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_about);
        actions.add(actionAbout);
        actionAbout.setOnClickListener(getListener(FragmentHelp.FRAGMENT_ABOUT));

        actionAccomodation = (TextView) view.findViewById(R.id.fragment_navigation_drawer_action_accomodation);
        actions.add(actionAccomodation);
        actionAccomodation.setOnClickListener(getListener(FragmentHelp.FRAGMENT_ACCOMODATION));


        tvUserName = (TextView) view.findViewById(R.id.fragment_navigation_drawer_username);
        tvUserNumber = (TextView) view.findViewById(R.id.fragment_navigation_drawer_user_number);
        mProfile = (ImageView) view.findViewById(R.id.fragment_navigation_drawer_profile_picture);
        coverImageArea = (ViewGroup) view.findViewById(R.id.fragment_navigation_drawer_cover_image);
        profileUpdateProgress = (ProgressBar) view.findViewById(R.id.fragment_navigation_drawer_profile_update);
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        tvUserName.setText(user.getName());
        tvUserNumber.setText(user.getNumber());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpCoverImage(coverImageArea);
        setUpProfileImage();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the menu_activity_main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private View.OnClickListener getListener(final int item){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(item);
                setItemSelected(v.getId());
            }
        };
    }

    private void selectItem(int item) {
        mCurrentSelectedPosition = item;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(item);
        }
    }

    private void setItemSelected(int id){
        for (TextView action : actions){
            if (action.getId() == id){
                action.setBackgroundColor(Color.GRAY);
            } else {
                action.setBackgroundResource(R.drawable.buttons_selector);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        } else {
            getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    private void setUpCoverImage(ViewGroup coverImageArea){
        int userCoverImage = user.getPreferredCoverImage();
        switch (userCoverImage){
            case 1:
                coverImageArea.setBackgroundResource(R.drawable.cover_image_one);
                break;
            case 2:
                coverImageArea.setBackgroundResource(R.drawable.cover_image_two);
                break;
            case 3:
                coverImageArea.setBackgroundResource(R.drawable.cover_image_three);
                break;
            /**
             * Add images 4 and 5
             */
            case 6:
                coverImageArea.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case 7:
                coverImageArea.setBackgroundColor(Color.parseColor("#0000FF"));
                break;
            case 8:
                coverImageArea.setBackgroundColor(Color.parseColor("#FF00FF"));
                break;
            case 9:
                coverImageArea.setBackgroundColor(Color.parseColor("#404040"));
                break;
            case 10:
                coverImageArea.setBackgroundColor(Color.parseColor("#FF6600"));
                break;
        }
    }

    private void updateProfile(){
        loadImageFromGallery();
    }

    public void loadImageFromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        Intent intent = Intent.createChooser(galleryIntent,"Select new profile");
        // Start the Intent
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                // Put file name in Async Http Event Param which will used in Php
                // web app
                params.put("filename", profileImageName);
                encodeImageToString();
            } else {
                Toast.makeText(getActivity(), "No image picked",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong selecting image, try again", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // AsyncTask - To convert Image to String
    public void encodeImageToString() {

        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload
                // easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                params.put("image", encodedString);
                sendImageToServer();
            }
        }.execute(null, null, null);
    }

    public void sendImageToServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as
        // well.
        client.post(Client.absoluteUrl("profile_image_upload.php"), params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (!Helper.at(getActivity()).isOnline()){
                    profileUpdateProgress.setVisibility(View.GONE);
                    mProfile.setImageBitmap(BitmapFactory.decodeFile(AppStorage.retrieve(AppStorage.PROFILE, profileImageName)));
                    Helper.at(getActivity()).simpleToast("Unable to update profile, try again later");
                } else {
                    profileUpdateProgress.setVisibility(View.VISIBLE);
                    mProfile.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                profileUpdateProgress.setVisibility(View.GONE);
                parseProfileImageUploadResponse(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                profileUpdateProgress.setVisibility(View.GONE);
                mProfile.setImageBitmap(BitmapFactory.decodeFile(AppStorage.retrieve(AppStorage.PROFILE, profileImageName)));
                Helper.at(getActivity()).simpleToast("Unable to update profile, try again later");
            }
        });
    }

    private void parseProfileImageUploadResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                Helper.at(getActivity()).simpleToast(response.getString(Response.MESSAGE));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(AppStorage.storeAs(AppStorage.PROFILE, profileImageName));
                        try {
                            AppStorage.delete(AppStorage.PROFILE, profileImageName);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            Bitmap mBitmap = BitmapFactory.decodeFile(imgPath);
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpProfileImage(){
        if (AppStorage.externalIsReadable()){
            File mFile = new File(AppStorage.retrieve(AppStorage.PROFILE, profileImageName));
            if (mFile.exists()){
                Log.d("Toshde"," Profile image is saved locally");
                mProfile.setImageBitmap(
                        BitmapFactory
                                .decodeFile(AppStorage
                                        .retrieve(AppStorage.PROFILE, profileImageName)));
            } else {
                Log.d("Toshde"," Going online to fetch profile image");
                profileUpdateProgress.setVisibility(View.VISIBLE);
                fetchProfileImage();
            }
        }
    }

    private void fetchProfileImage() {
        Picasso.with(getActivity()).load(Client.absoluteUrl("profiles/"+ profileImageName)).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                final Bitmap mBitmap = bitmap;
                mProfile.setImageBitmap(bitmap);
                profileUpdateProgress.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(AppStorage.storeAs(AppStorage.PROFILE, profileImageName));
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                profileUpdateProgress.setVisibility(View.GONE);
                Helper.at(getActivity()).simpleToast("Unable to fetch profile Image");
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        });
    }

}
