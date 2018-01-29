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
public class PosterLoadingFragment extends Fragment {

    private CheckBox autoDownloadCheckBox;

    public static PosterLoadingFragment newInstance() {
        PosterLoadingFragment fragment = new PosterLoadingFragment();
        return fragment;
    }

    public PosterLoadingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posters_loading, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoDownloadCheckBox = (CheckBox) view.findViewById(R.id.auto_download_posters_checkbox);
        autoDownloadCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PrefUtils.getInstance(getActivity()).writePrefAutoDownloadPosters(true);
                } else {
                    PrefUtils.getInstance(getActivity()).writePrefAutoDownloadPosters(false);
                }
            }
        });
    }

}
