package com.mustmobile.fragment.academics;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.R;
import com.mustmobile.adapter.GradesListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Grade;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class GradesFragment extends BaseFragment{

    private final String TAG = GradesFragment.class.getSimpleName();
    private Button retryButton, reportButton;
    private ListView mListView;
    private GradesListAdapter mAdapter;
    private ArrayList<Grade> gradeArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private TextView tvHeading;
    private String whichYear;

    private static final String GRADES_FOR = "grades_for";

    public static GradesFragment newInstance(String whichYear) {
        GradesFragment fragment = new GradesFragment();
        Bundle args = new Bundle();
        args.putString(GRADES_FOR, whichYear);
        fragment.setArguments(args);
        return fragment;
    }

    public GradesFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.whichYear = getArguments().getString(GRADES_FOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_grades_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_grades_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_grades_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_grades_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_grades_no_connection);
        tvHeading = (TextView) view.findViewById(R.id.fragment_grades_heading);

        if (whichYear.equalsIgnoreCase("year_one")){
            tvHeading.setText("Year one grades");
        } else if (whichYear.equalsIgnoreCase("year_two")){
            tvHeading.setText("Year two grades");
        } else if (whichYear.equalsIgnoreCase("year_three")){
            tvHeading.setText("Year three grades");
        } else {
            tvHeading.setText("Year four grades");
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gradeArrayList = new ArrayList<>();
        mAdapter = new GradesListAdapter(getActivity(), gradeArrayList);
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
        RequestParams params = new RequestParams();
        params.put("user_number", user.getNumber());
        params.put("intake_year", "2013");
        params.put("program_code", user.getProgramCode());
        params.put("year", whichYear);

        Client.post(Client.absoluteUrl("results"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                loadingIndicator.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
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
    protected void parseConnectionResponse(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray data = response.getJSONArray(Response.DATA);
                JSONObject grades = data.getJSONObject(0);
                Iterator<String> mKeys = grades.keys();
                while (mKeys.hasNext()){
                    String key = mKeys.next();
                    if (!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("registration_number")){
                        Grade g = new Grade(grades.getString(key), key, "");
                        gradeArrayList.add(g);
                    }
                }

                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
