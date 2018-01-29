package com.mustmobile.fragment.academics;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.SemestersListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.fragment.timetable.TeachingTimetableFragment;
import com.mustmobile.model.Semester;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SemesterDatesFragment extends BaseFragment {

    private final String TAG = TeachingTimetableFragment.class.getSimpleName();
    private AlertDialog dialog;
    private Button retryButton, reportButton;
    private ListView mListView;
    private SemestersListAdapter mAdapter;
    private ArrayList<Semester> semesterArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private User user;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SemesterDatesFragment newInstance(int sectionNumber) {
        SemesterDatesFragment fragment = new SemesterDatesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SemesterDatesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.at(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_semester_dates, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_semester_dates_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_semester_dates__loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_semester_dates_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_semester_dates_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_semester_dates_no_connection);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        semesterArrayList = new ArrayList<>();
        mAdapter = new SemestersListAdapter(getActivity(), semesterArrayList);
        mListView.setAdapter(mAdapter);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

        connectAndRespond();
    }

    @Override
    protected void connectAndRespond(){

        Client.post(Client.absoluteUrl("semesterdates"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();if (Helper.at(getActivity()).isOnline()){
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                } else {
                    noConnection.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseConnectionResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noConnection.setVisibility(View.VISIBLE);
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray semesters = response.getJSONArray(Response.DATA);
                for (int i = 0; i < semesters.length(); i++){
                    JSONObject semester = semesters.getJSONObject(i);
                    Semester s = new Semester();

                    s.setReportingNewStudentsDate(semester.getString(Response.SemesterData.REPORTING_NEW_STUDENTS));
                    s.setReportingContinuingStudentsDate(semester.getString(Response.SemesterData.REPORTING_CONTINUING_STUDENTS));
                    s.setCommencementOfLecturesDate(semester.getString(Response.SemesterData.COMMENCEMENT_OF_LECTURES));
                    s.setCatOneStartDate(semester.getString(Response.SemesterData.CAT_ONE_START));
                    s.setCatOneEndDate(semester.getString(Response.SemesterData.CAT_ONE_END));
                    s.setCatTwoStartDate(semester.getString(Response.SemesterData.CAT_TWO_START));
                    s.setCatTwoEndDate(semester.getString(Response.SemesterData.CAT_TWO_END));
                    s.setEndOfLecturesDate(semester.getString(Response.SemesterData.END_OF_LECTURES));
                    s.setExaminationsStartDate(semester.getString(Response.SemesterData.EXAMINATIONS_START));
                    s.setExaminationsEndDate(semester.getString(Response.SemesterData.EXAMINATIONS_END));
                    s.setTeachingWeeks(semester.getString(Response.SemesterData.TEACHING_WEEKS));
                    s.setExaminationWeeks(semester.getString(Response.SemesterData.EXAMINATION_WEEKS));
                    s.setBreakStart(semester.getString(Response.SemesterData.BREAK_START));
                    s.setBreakEnd(semester.getString(Response.SemesterData.BREAK_END));
                    s.setBreakWeeks(semester.getString(Response.SemesterData.BREAK_WEEKS));
                    s.setRemarks(semester.getString(Response.SemesterData.REMARKS));

                    semesterArrayList.add(s);

                }
                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
