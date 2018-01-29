package com.mustmobile.fragment.media;

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
import com.mustmobile.ReaderActivity;
import com.mustmobile.adapter.UpdatesListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Update;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EventsFragment extends BaseFragment {


    private Button retryButton, reportButton;
    private ListView mListView;
    private UpdatesListAdapter mAdapter;
    private ArrayList<Update> updateArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static EventsFragment newInstance(int sectionNumber) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_and_events, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_news_and_events_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_news_and_events_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_news_and_events_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_news_and_events_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_news_and_events_no_connection);

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

        updateArrayList = new ArrayList<>();
        mAdapter = new UpdatesListAdapter(getActivity(), updateArrayList);
        mListView.setAdapter(mAdapter);

        loadingMoreFooter = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);
        mListView.addFooterView(loadingMoreFooter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Update update = updateArrayList.get(position);
                Intent intent = new Intent(getActivity(), ReaderActivity.class);
                intent.putExtra("title", update.getTitle());
                intent.putExtra("content", update.getContent());
                intent.putExtra("views", String.valueOf(Integer.parseInt(update.getViews())+1));
                intent.putExtra("timeCreated", update.getTimeCreated());
                getActivity().startActivity(intent);
                Client.updateViewsCount(update.getUpdateId());
            }
        });

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

        Client.post(Client.absoluteUrl("events"), null, new JsonHttpResponseHandler() {
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
                loadingIndicator.setVisibility(View.GONE);
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray updates = response.getJSONArray(Response.DATA);
                for (int i = 0; i < updates.length(); i++){
                    JSONObject update = updates.getJSONObject(i);
                    String title = update.getString(Response.UpdateData.TITLE);
                    String updateId = update.getString(Response.UpdateData.ID);
                    String content = update.getString(Response.UpdateData.CONTENT);
                    String time = update.getString(Response.UpdateData.CREATED_AT);
                    String timesRead = update.getString(Response.UpdateData.TIMES_READ);
                    Update u = new Update(content, title, time, timesRead, updateId);
                    updateArrayList.add(u);
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
