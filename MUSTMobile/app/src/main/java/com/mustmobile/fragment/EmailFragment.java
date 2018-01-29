package com.mustmobile.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mustmobile.MainActivity;
import com.mustmobile.R;

public class EmailFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static EmailFragment newInstance(int sectionNumber) {
        EmailFragment fragment = new EmailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EmailFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
