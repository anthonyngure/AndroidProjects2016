package com.mustmobile;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.adapter.PastPapersListAdapter;
import com.mustmobile.database.MySuggestionProvider;
import com.mustmobile.model.PastPaper;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchResultsActivity extends AppCompatActivity {

    private Button retryButton, reportButton;
    private ListView mListView;
    private PastPapersListAdapter mAdapter;
    private ArrayList<PastPaper> pastPaperArrayList;
    private ViewGroup loadingIndicator, noConnection, noData;
    private TextView tvHeading, tvNoSearchData;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mListView = (ListView) findViewById(R.id.activity_search_results_listview);
        tvHeading = (TextView) findViewById(R.id.activity_search_results_heading);
        tvNoSearchData = (TextView) findViewById(R.id.activity_search_results_no_search_data);
        loadingIndicator = (ViewGroup) findViewById(R.id.activity_search_results_loading_indicator);
        retryButton = (Button) findViewById(R.id.activity_search_results_retry_button);
        reportButton = (Button) findViewById(R.id.activity_search_results_report_button);
        noConnection = (ViewGroup) findViewById(R.id.activity_search_results_no_connection);
        noData = (ViewGroup) findViewById(R.id.activity_search_results_no_data);
        pastPaperArrayList = new ArrayList<>();
        mAdapter = new PastPapersListAdapter(this, pastPaperArrayList);
        mListView.setAdapter(mAdapter);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBooks();
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        tvHeading.setVisibility(View.GONE);
        query = "";
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new
                    SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            //use the query to search
            searchBooks();
        } else {
            searchBooks();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_search_results, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.activity_search_results_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }

    private void searchBooks(){
        pastPaperArrayList.clear();
        String url = Client.absoluteUrl("exampastpapers/search/"+query).trim().replace(" ","") ;
        Client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                tvHeading.setText("Searching for '" + query + "'");
                tvHeading.setVisibility(View.VISIBLE);

                if (Helper.at(getApplicationContext()).isOnline()){
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                    noConnection.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mResponseParser(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void mResponseParser(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray pastpapers = response.getJSONArray(Response.DATA);
                for (int i = 0; i < pastpapers.length(); i++){
                    JSONObject pastpaper = pastpapers.getJSONObject(i);
                    PastPaper p = new PastPaper(pastpaper.getString(Response.PastPaperData.NAME).replace("_"," "),
                            pastpaper.getString(Response.PastPaperData.URL));
                    pastPaperArrayList.add(p);
                }

                if (pastPaperArrayList.size() < 1){
                    noData.setVisibility(View.VISIBLE);
                    tvNoSearchData.setText("Searched past paper is not in the system. Will be added soon.");
                    loadingIndicator.setVisibility(View.GONE);
                } else {
                    mAdapter.notifyDataSetChanged();
                    mListView.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                }
                tvHeading.setText("Search results for '"+query+"'");
            } else {
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
