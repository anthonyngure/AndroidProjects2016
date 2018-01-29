package com.mustmobile.fragment.finance;

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
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.FeeStructuresListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.FeeStructure;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FinanceFragment extends BaseFragment{

    private final String TAG = FinanceFragment.class.getSimpleName();
    private Button retryButton, reportButton;
    private ListView mListView;
    private FeeStructuresListAdapter mAdapter;
    private ArrayList<FeeStructure> feeStructureArrayList;
    private ViewGroup loadingIndicator, noConnection, header;
    private TextView financeTitle, financeProgram;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FinanceFragment newInstance(int sectionNumber) {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FinanceFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_finance_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_finance_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_finance_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_finance_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_finance_no_connection);
        header = (ViewGroup) view.findViewById(R.id.fragment_finance_header);

        financeTitle = (TextView) view.findViewById(R.id.fragment_finance_title);
        financeTitle.setText("FEE STRUCTURE FOR "+user.getProgramCode()+" "+user.getStage()+" "+user.getMode()+" "+user.getSchoolAsName().toUpperCase());

        financeProgram = (TextView) view.findViewById(R.id.fragment_finance_program);
        financeProgram.setText(user.getProgramName());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        feeStructureArrayList = new ArrayList<>();
        mAdapter = new FeeStructuresListAdapter(getActivity(), feeStructureArrayList);
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
        params.put("school_code", user.getSchoolCode());
        params.put("program_code", user.getProgramCode());

        Client.post(Client.absoluteUrl("feestructure"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()){
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                    noConnection.setVisibility(View.VISIBLE);
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
                loadingIndicator.setVisibility(View.GONE);
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray feeStructures = response.getJSONArray(Response.DATA);
                for (int i = 0; i < feeStructures.length(); i++){
                    JSONObject feeStructure = feeStructures.getJSONObject(i);
                    FeeStructure f = new FeeStructure();

                    f.setTuitionSemOne(feeStructure.getString(Response.FeeStructureData.TUITION_SEM_ONE));
                    f.setTuitionSemTwo(feeStructure.getString(Response.FeeStructureData.TUITION_SEM_TWO));

                    f.setExaminationSemOne(feeStructure.getString(Response.FeeStructureData.EXAMINATION_SEM_ONE));
                    f.setExaminationSemTwo(feeStructure.getString(Response.FeeStructureData.EXAMINATION_SEM_TWO));

                    f.setMedicalSemOne(feeStructure.getString(Response.FeeStructureData.MEDICAL_SEM_ONE));
                    f.setMedicalSemTwo(feeStructure.getString(Response.FeeStructureData.MEDICAL_SEM_TWO));

                    f.setActivitySemOne(feeStructure.getString(Response.FeeStructureData.ACTIVITY_SEM_ONE));
                    f.setActivitySemTwo(feeStructure.getString(Response.FeeStructureData.ACTIVITY_SEM_TWO));

                    f.setInternetSemOne(feeStructure.getString(Response.FeeStructureData.INTERNET_SEM_ONE));
                    f.setInternetSemTwo(feeStructure.getString(Response.FeeStructureData.INTERNET_SEM_TWO));

                    f.setTripSemOne(feeStructure.getString(Response.FeeStructureData.TRIP_SEM_ONE));
                    f.setTripSemTwo(feeStructure.getString(Response.FeeStructureData.TRIP_SEM_TWO));

                    f.setLibrarySemOne(feeStructure.getString(Response.FeeStructureData.LIBRARY_SEM_ONE));
                    f.setLibrarySemTwo(feeStructure.getString(Response.FeeStructureData.LIBRARY_SEM_TWO));

                    f.setRegistration(feeStructure.getString(Response.FeeStructureData.REGISTRATION_FEE));
                    f.setAttachment(feeStructure.getString(Response.FeeStructureData.ATTACHMENT));
                    f.setStudentUnion(feeStructure.getString(Response.FeeStructureData.STUDENT_UNION));
                    f.setAccommodation(feeStructure.getString(Response.FeeStructureData.ACCOMODATION));

                    f.setTotalPayableSemOne(feeStructure.getString(Response.FeeStructureData.TOTAL_PAYABLE_SEM_ONE));
                    f.setTotalPayableSemTwo(feeStructure.getString(Response.FeeStructureData.TOTAL_PAYABLE_SEM_TWO));

                    feeStructureArrayList.add(f);
                }

                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
