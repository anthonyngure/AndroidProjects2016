package ke.co.elmaxdevelopers.eventskenya.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

/**
 * Created by Tosh on 1/31/2016.
 */
public class DataPolicyFragment extends Fragment {

    private CheckBox loadInAllTabs;
    private CheckBox checkNewComments;

    public static DataPolicyFragment newInstance() {
        DataPolicyFragment fragment = new DataPolicyFragment();
        return fragment;
    }

    public DataPolicyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_policy, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadInAllTabs = (CheckBox) view.findViewById(R.id.load_events_in_all_tabs_checkbox);
        loadInAllTabs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    PrefUtils.getInstance(getActivity()).writePrefLoadEventsInAllTabs(true);
                } else {
                    PrefUtils.getInstance(getActivity()).writePrefLoadEventsInAllTabs(false);
                }
            }
        });

        checkNewComments = (CheckBox) view.findViewById(R.id.check_new_comments_checkbox);
        checkNewComments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    PrefUtils.getInstance(getActivity()).writePrefCheckNewCommentsForSavedEvents(true);
                } else {
                    PrefUtils.getInstance(getActivity()).writePrefCheckNewCommentsForSavedEvents(false);
                }
            }
        });

    }
}
