package com.mustmobile.fragment.exchanges;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.ForumTopicsActivity;
import com.mustmobile.adapter.ForumsListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Forum;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ForumsFragment extends BaseFragment {

    private Button retryButton, reportButton;
    private ListView mListView;
    private ForumsListAdapter mAdapter;
    private ArrayList<Forum> forumArrayList;
    private ViewGroup loadingIndicator, noConnection;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ForumsFragment newInstance(int sectionNumber) {
        ForumsFragment fragment = new ForumsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ForumsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchanges, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_exchanges_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_exchanges_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_exchanges_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_exchanges_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_exchanges_no_connection);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Forums");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        forumArrayList = new ArrayList<>();
        mAdapter = new ForumsListAdapter(getActivity(), forumArrayList);
        mListView.setAdapter(mAdapter);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Forum s = forumArrayList.get(position);
                startActivity(new Intent(getActivity(),
                        ForumTopicsActivity.class).putExtra("stack_name",s.getName()).putExtra("stack_code",s.getCode()));
            }
        });

        connectAndRespond();
    }

    @Override
    public void connectAndRespond(){
        Client.post(Client.absoluteUrl("exchangeforums"), null, new JsonHttpResponseHandler() {
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
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray stacks = response.getJSONArray(Response.DATA);
                for (int i = 0; i < stacks.length(); i++){
                    JSONObject stack = stacks.getJSONObject(i);
                    Forum s = new Forum(stack.getString(Response.ForumData.NAME),
                            stack.getString(Response.ForumData.CODE),
                            stack.getString(Response.ForumData.DESCRIPTION),
                            stack.getString(Response.ForumData.ICON_URL));
                    forumArrayList.add(s);
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
