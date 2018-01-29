package com.mustmobile;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.mustmobile.model.Update;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewUpdatesActivity extends AppCompatActivity {


    private Button retryButton, reportButton;
    private ListView mListView;
    private UpdatesListAdapter mAdapter;
    private ArrayList<Update> updateArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_and_events);
        mListView = (ListView) findViewById(R.id.fragment_news_and_events_listview);
        loadingIndicator = (ViewGroup) findViewById(R.id.fragment_news_and_events_loading_indicator);
        retryButton = (Button) findViewById(R.id.fragment_news_and_events_retry_button);
        reportButton = (Button) findViewById(R.id.fragment_news_and_events_report_button);
        noConnection = (ViewGroup) findViewById(R.id.fragment_news_and_events_no_connection);

        updateArrayList = new ArrayList<>();
        mAdapter = new UpdatesListAdapter(this, updateArrayList);
        mListView.setAdapter(mAdapter);

        loadingMoreFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);
        mListView.addFooterView(loadingMoreFooter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Update update = updateArrayList.get(position);
                Intent intent = new Intent(NewUpdatesActivity.this, ReaderActivity.class);
                intent.putExtra("title", update.getTitle());
                intent.putExtra("content", update.getContent());
                intent.putExtra("views", String.valueOf(Integer.parseInt(update.getViews()) + 1));
                intent.putExtra("timeCreated", update.getTimeCreated());
                NewUpdatesActivity.this.startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected void connectAndRespond(){

        Client.post(Client.absoluteUrl("updates/mostread"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getApplicationContext()).isOnline()){
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
