package ke.co.elmaxdevelopers.eventskenya.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import ke.co.elmaxdevelopers.eventskenya.fragment.CountyCategoryFragment;
import ke.co.elmaxdevelopers.eventskenya.fragment.EventsListFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles [];
    int numberOfTabs;

    public SectionsPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumberOfTabs) {
        super(fm);
        this.titles = mTitles;
        this.numberOfTabs = mNumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return EventsListFragment.newInstance(position);
            case 1:
                return EventsListFragment.newInstance(position);
            case 2:
                return EventsListFragment.newInstance(position);
            case 3:
                return EventsListFragment.newInstance(position);
            case 4:
                return EventsListFragment.newInstance(position);
            case 5:
                return EventsListFragment.newInstance(position);
            default:
                return EventsListFragment.newInstance(0);
        }
    }
    @Override
    public int getCount() {
        return numberOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}