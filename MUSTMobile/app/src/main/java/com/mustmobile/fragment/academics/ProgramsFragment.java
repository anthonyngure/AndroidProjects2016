package com.mustmobile.fragment.academics;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.ProgramsListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Program;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProgramsFragment extends BaseFragment {

    private final String TAG = ProgramsFragment.class.getSimpleName();
    Button retryButton, reportButton;
    private ListView mListView;
    private ProgramsListAdapter mAdapter;
    private ArrayList<Program> programArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ProgramsFragment newInstance(int sectionNumber) {
        ProgramsFragment fragment = new ProgramsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_programs, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_programs_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_programs_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_programs_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_programs_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_programs_no_connection);
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

        programArrayList = new ArrayList<>();
        mAdapter = new ProgramsListAdapter(getActivity(), programArrayList);
        mListView.setAdapter(mAdapter);

        loadingMoreFooter = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);
        mListView.addFooterView(loadingMoreFooter);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

        connectAndRespond();
    }

    @Override
    public void connectAndRespond(){
        Client.post(Client.absoluteUrl("programs"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()){
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
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray programs = response.getJSONArray(Response.DATA);
                for (int i = 0; i < programs.length(); i++){
                    JSONObject program = programs.getJSONObject(i);
                    Program p = new Program();
                    p.setName(program.getString(Response.ProgramData.NAME));
                    p.setRequirements(program.getString(Response.ProgramData.REQUIREMENTS));
                    p.setDuration(program.getString(Response.ProgramData.DURATION));
                    p.setMode(program.getString(Response.ProgramData.MODE));
                    p.setCampus(program.getString(Response.ProgramData.CAMPUS));
                    p.setFees(program.getString(Response.ProgramData.FEES));
                    programArrayList.add(p);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_programs, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_fragment_programs).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }
}
