package ke.co.elmaxdevelopers.eventskenya.activity;

import java.util.Locale;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.fragment.CountyCategoryFragment;
import ke.co.elmaxdevelopers.eventskenya.fragment.DataPolicyFragment;
import ke.co.elmaxdevelopers.eventskenya.fragment.EventsListFragment;
import ke.co.elmaxdevelopers.eventskenya.fragment.PosterLoadingFragment;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

public class StarterSettingsActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    Button nextButton, backButton;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter_settings);
        backButton = (Button) findViewById(R.id.back);
        nextButton = (Button) findViewById(R.id.next);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0){
                    backButton.setEnabled(false);
                } else {
                    backButton.setEnabled(true);
                }

                if (position == (mSectionsPagerAdapter.getCount()-1)){
                    nextButton.setText("Finish");
                } else {
                    nextButton.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return CountyCategoryFragment.newInstance();
                case 1:
                    return DataPolicyFragment.newInstance();
                case 2:
                    return PosterLoadingFragment.newInstance();
                default:
                    return CountyCategoryFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
    public void goBack(View view){
        mViewPager.setCurrentItem((mViewPager.getCurrentItem()-1));
    }

    public void goNext(View view){
        if (mViewPager.getCurrentItem() == 0){
            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
        }else if (mViewPager.getCurrentItem() == 1){
            mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1));
        } else if (mViewPager.getCurrentItem() == 2){
            PrefUtils.getInstance(this).writePrefHasFinishedStarterSettings(true);
            PrefUtils.getInstance(this).invalidate();
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }

    public void selectCategory(final View view) {
        final String[] categories = getResources().getStringArray(R.array.categories);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_category))
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Button) view).setText(categories[which]);
                        PrefUtils.getInstance(getApplicationContext()).writeSetCategory(categories[which]);
                        PrefUtils.getInstance(getApplicationContext()).invalidate();
                        DataController.getInstance(getApplicationContext()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();
    }

    public void selectCounty(final View view){
        final CharSequence[] counties = getResources().getStringArray(R.array.counties);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_county))
                .setItems(counties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Button) view).setText(counties[which]);
                        PrefUtils.getInstance(getApplicationContext()).writeSetCounty(String.valueOf(counties[which]));
                        PrefUtils.getInstance(getApplicationContext()).invalidate();
                        DataController.getInstance(getApplicationContext()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();

    }
}
