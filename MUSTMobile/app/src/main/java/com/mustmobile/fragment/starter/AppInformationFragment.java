package com.mustmobile.fragment.starter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mustmobile.MainActivity;
import com.mustmobile.R;

public class AppInformationFragment extends Fragment {

    private Button continueButton;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static AppInformationFragment newInstance(int sectionNumber) {
        AppInformationFragment fragment = new AppInformationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AppInformationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_app_information, container, false);
        continueButton = (Button) view.findViewById(R.id.fragment_app_information_button_continue);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(AppInformationFragment.class.getSimpleName())
                        .replace(R.id.activity_starter_fragment_container,
                                new StarterFragment().newInstance(), StarterFragment.class.getSimpleName())
                        .commit();
            }
        });
    }
}
