package com.mustmobile.fragment.timetable;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.TimetableListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Timetable;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.FragmentHelp;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ExamTimetableFragment extends BaseFragment {

    private final String TAG = ExamTimetableFragment.class.getSimpleName();
    private AlertDialog dialog;
    private Button retryButton, reportButton;
    private ListView mListView;
    private TimetableListAdapter mAdapter;
    private ArrayList<Timetable> timetableArrayList;
    private ViewGroup loadingIndicator, noConnection, noData;
    private TextView tvNoTimetableData;
    private User user;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ExamTimetableFragment newInstance(int sectionNumber) {
        ExamTimetableFragment fragment = new ExamTimetableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ExamTimetableFragment() {
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
        View view = inflater.inflate(R.layout.fragment_exam_timetable, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_exam_timetable_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_exam_timetable__loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_exam_timetable_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_exam_timetable_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_exam_timetable_no_connection);
        noData = (ViewGroup) view.findViewById(R.id.fragment_exam_timetable_no_data);
        tvNoTimetableData = (TextView) view.findViewById(R.id.fragment_exam_timetable_no_exam_timetable_data);
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
        timetableArrayList = new ArrayList<>();
        mAdapter = new TimetableListAdapter(getActivity(), timetableArrayList);
        mListView.setAdapter(mAdapter);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });
        /**
         * Connect to fetch exam timetable data
         * Now show no data
         */
        //connectAndRespond();
        noData.setVisibility(View.VISIBLE);
        tvNoTimetableData.setText(user.getProgramCode()+" "+user.getStage()+" Exam timetable is not in the system. Will be added soon");
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    protected void connectAndRespond(){

        String userClass = user.getProgramCode()+user.getStage();
        Client.post(Client.absoluteUrl("timetable/" + userClass), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                    noConnection.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseConnectionResponse(response);
                //Helper.at(getActivity()).simpleToast(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                //Helper.at(getActivity()).simpleToast(responseString);
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray timetables = response.getJSONArray(Response.DATA);
                for (int i = 0; i < timetables.length(); i++){
                    JSONObject timetable = timetables.getJSONObject(i);
                    Timetable t = new Timetable();
                    t.setDay(timetable.getString(Response.TimetableData.DAY));
                    t.setHourOne(timetable.getString(Response.TimetableData.HOUR_ONE));
                    t.setHourTwo(timetable.getString(Response.TimetableData.HOUR_TWO));
                    t.setHourThree(timetable.getString(Response.TimetableData.HOUR_THREE));
                    t.setHourFour(timetable.getString(Response.TimetableData.HOUR_FOUR));
                    t.setHourFive(timetable.getString(Response.TimetableData.HOUR_FIVE));
                    t.setHourSix(timetable.getString(Response.TimetableData.HOUR_SIX));
                    t.setHourSeven(timetable.getString(Response.TimetableData.HOUR_SEVEN));
                    t.setHourEight(timetable.getString(Response.TimetableData.HOUR_EIGHT));
                    t.setHourNine(timetable.getString(Response.TimetableData.HOUR_NINE));
                    t.setHourTen(timetable.getString(Response.TimetableData.HOUR_TEN));
                    timetableArrayList.add(t);
                }
                if (timetableArrayList.size()<1){
                    noData.setVisibility(View.VISIBLE);
                    tvNoTimetableData.setText(user.getStage()+" Time table is not in the system. Will be added soon");
                    loadingIndicator.setVisibility(View.GONE);
                } else {
                    mAdapter.notifyDataSetChanged();
                    mListView.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                }
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_exam_timetable, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.fragment_exam_timetable_action_lectures:
                ((MainActivity) getActivity())
                        .replaceFragment(new TeachingTimetableFragment()
                                .newInstance(FragmentHelp.FRAGMENT_TEACHING_TIMETABLE));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
