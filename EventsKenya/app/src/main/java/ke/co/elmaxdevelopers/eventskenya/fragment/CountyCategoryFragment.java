package ke.co.elmaxdevelopers.eventskenya.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.elmaxdevelopers.eventskenya.R;

/**
 * Created by Tosh on 1/31/2016.
 */
public class CountyCategoryFragment extends Fragment {

    public static CountyCategoryFragment newInstance() {
        CountyCategoryFragment fragment = new CountyCategoryFragment();
        return fragment;
    }

    public CountyCategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_county_and_category_settings, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
