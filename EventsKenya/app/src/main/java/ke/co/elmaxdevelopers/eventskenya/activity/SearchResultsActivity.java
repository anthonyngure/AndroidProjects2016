package ke.co.elmaxdevelopers.eventskenya.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.adapter.ServicesListAdapter;
import ke.co.elmaxdevelopers.eventskenya.adapter.EventsListAdapter;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.stickyheaderslibrary.DividerDecoration;
import ke.co.elmaxdevelopers.eventskenya.stickyheaderslibrary.library.StickyRecyclerHeadersDecoration;
import ke.co.elmaxdevelopers.eventskenya.utils.JsonUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;

public class SearchResultsActivity extends AppCompatActivity implements LoadingListener {

    public static final String SEARCH_CONTEXT = "search_context";
    public static final String EVENTS_SEARCH = "events_search";
    public static final String SERVICES_SEARCH = "services_search";
    private static final String KEY_QUERY = "query";
    private String searchContext;
    private ViewGroup loadingScreen;
    private ViewGroup connectionError;

    private EventsListAdapter eventsListAdapter;
    private ServicesListAdapter servicesListAdapter;
    private RecyclerView recyclerView;
    private String serviceType;
    private RequestParams params = new RequestParams();
    private String query;
    private TextView tvNoDataInfo;
    private Snackbar snackbar;
    private LinearLayoutManager mLayoutManager;
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);
        searchContext = getIntent().getExtras().getString(SEARCH_CONTEXT, EVENTS_SEARCH);

        loadingScreen = (ViewGroup) findViewById(R.id.loading_screen);
        connectionError = (ViewGroup) findViewById(R.id.connection_error);
        Button retryButton = (Button) findViewById(R.id.retry_button);
        tvNoDataInfo = (TextView) findViewById(R.id.connection_error_info);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionError.setVisibility(View.GONE);
                doSearch();
            }
        });


        if (searchContext.equalsIgnoreCase(EVENTS_SEARCH) && searchContext != null){
            prepareForEvents();
        } else if (searchContext.equalsIgnoreCase(SERVICES_SEARCH) && searchContext != null){
            prepareForServices();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    if (searchContext.equalsIgnoreCase(SERVICES_SEARCH)) {
                        hideSnackBar();
                        snackbar = Snackbar.make(recyclerView, "No more " + serviceType + " to show for query : " + query,
                                Snackbar.LENGTH_LONG);
                        TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                        snackbar.show();
                    } else {
                        hideSnackBar();
                        snackbar = Snackbar.make(recyclerView, "No more events to show for query : " + query,
                                Snackbar.LENGTH_LONG);
                        TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                        snackbar.show();
                    }
                }
            }
        });

        handleIntent(getIntent());
    }

    private void prepareForEvents() {

        recyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        eventsListAdapter = new EventsListAdapter(this);
        recyclerView.setAdapter(eventsListAdapter);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(eventsListAdapter);
        recyclerView.addItemDecoration(headersDecor);

        eventsListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
    }

    private void hideSnackBar(){
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    private void prepareForServices(){
        serviceType = getIntent().getExtras().getString(ServicesActivity.KEY_SERVICE_TYPE);
        params.put(ServicesActivity.KEY_SERVICE_TYPE, serviceType);
        servicesListAdapter = new ServicesListAdapter (this);
        recyclerView.setAdapter(servicesListAdapter);

        recyclerView.setHasFixedSize(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            params.put(KEY_QUERY, query);
            doSearch();
        }
    }

    public void doSearch() {

        String url = "";
        if (searchContext.equalsIgnoreCase(EVENTS_SEARCH) && searchContext != null){
            url = BackEnd.absoluteUrl("events/search/" + query);
            eventsListAdapter.clear();
        } else if (searchContext.equalsIgnoreCase(SERVICES_SEARCH) && searchContext != null){
            servicesListAdapter.clear();
            params.put(BackEnd.PARAM_SERVICE_TYPE, serviceType);
            url = BackEnd.absoluteUrl("services/search/" + query);
        }

        APIConnector.getInstance(this).search(url, params, this);
    }

    private void parseSearchedEvents(JSONObject response) {
        eventsListAdapter.clear();
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)) {
                JSONArray events = response.getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    eventsListAdapter.add(JsonUtils.getEventFromJsonObject(events.getJSONObject(i)));
                }
                if (eventsListAdapter.getItemCount() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    loadingScreen.setVisibility(View.GONE);
                    connectionError.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    loadingScreen.setVisibility(View.GONE);
                    tvNoDataInfo.setText("No events found for query : " + query);
                    connectionError.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseSearchedServices(JSONObject response) {
        servicesListAdapter.clear();
        if (response.has(Response.SUCCESS)){
            try {
                JSONArray services = response.getJSONArray("services");
                for (int i = 0; i < services.length(); i++){
                    servicesListAdapter.add(JsonUtils.getServiceFromJsonObject(services.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (servicesListAdapter.getItemCount() > 0){
                recyclerView.setVisibility(View.VISIBLE);
                loadingScreen.setVisibility(View.GONE);
                connectionError.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                loadingScreen.setVisibility(View.GONE);
                tvNoDataInfo.setText("No " + serviceType + "s found for query : "+query);
                tvNoDataInfo.setVisibility(View.VISIBLE);
                connectionError.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_search_results, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.activity_search_results_action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        progressWheel.startSpinning();
        loadingScreen.setVisibility(View.VISIBLE);
        connectionError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        progressWheel.stopSpinning();
        connectionError.setVisibility(View.VISIBLE);
        loadingScreen.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        progressWheel.stopSpinning();
        if (searchContext.equalsIgnoreCase(EVENTS_SEARCH)) {
            parseSearchedEvents(response);
        } else if (searchContext.equalsIgnoreCase(SERVICES_SEARCH)) {
            parseSearchedServices(response);
        }
    }
}
