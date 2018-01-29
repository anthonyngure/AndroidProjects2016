package ke.co.elmaxdevelopers.eventskenya.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
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
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.JsonUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;


public class ServicesActivity extends AppCompatActivity implements LoadingListener {


    public static final String KEY_SERVICE_TYPE = "service_type";

    private String serviceType;
    private RecyclerView recyclerView;
    private ViewGroup loadingScreen, connectionError;
    private Button retryButton;
    private ServicesListAdapter mAdapter;
    private boolean isLoadingMore = false;
    private int currentServicesSize = 0;
    private RequestParams params = new RequestParams();
    private TextView tvNoDataInfo;
    private LinearLayoutManager mLayoutManager;
    private Snackbar snackbar;
    private boolean noMoreEventsFound = false;
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        if (getIntent().getExtras() != null){
            serviceType = getIntent().getExtras().getString(KEY_SERVICE_TYPE, Service.EVENT_SERVICES);
            params.put(KEY_SERVICE_TYPE, serviceType);
            getSupportActionBar().setTitle(serviceType.substring(0, 1).toUpperCase() + serviceType.substring(1) + "s");
        }


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        loadingScreen = (ViewGroup) findViewById(R.id.loading_screen);
        connectionError = (ViewGroup) findViewById(R.id.connection_error);
        tvNoDataInfo = (TextView) findViewById(R.id.connection_error_info);
        retryButton = (Button) findViewById(R.id.retry_button);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        recyclerView.setHasFixedSize(false);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ServicesListAdapter(this);
        recyclerView.setAdapter(mAdapter);


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadServices();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem == (totalItemCount - visibleItemCount)) {
                    if (!isLoadingMore && !noMoreEventsFound) {
                        isLoadingMore = true;
                        loadMoreService();
                    }
                    if (noMoreEventsFound && !recyclerView.canScrollVertically(1)) {
                        hideSnackBar();
                        snackbar = Snackbar.make(recyclerView, "No more " + serviceType + " to show.",
                                Snackbar.LENGTH_LONG);
                        TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                        snackbar.show();
                    }
                }
            }
        });

        for (Service service : DataController.getInstance(this).getSavedServices()){
            if (service.getServiceType().equalsIgnoreCase(serviceType)){
                mAdapter.add(service);
            }
        }
        if (mAdapter.getItemCount() > 0){
            currentServicesSize = mAdapter.getItemCount();
            recyclerView.setVisibility(View.VISIBLE);
            loadingScreen.setVisibility(View.GONE);
            connectionError.setVisibility(View.GONE);
            /**
             * When the service cards displayed are less than 3 load more
             */
            if (mAdapter.getItemCount()  < 2){
                isLoadingMore = true;
                loadMoreService();
            }
        } else {
            loadServices();
        }
    }

    private void hideSnackBar(){
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    public void loadServices() {

        if (params.has(BackEnd.PARAM_LAST_ID)){
            params.remove(BackEnd.PARAM_LAST_ID);
        }
        params.put(BackEnd.PARAM_LAST_ID, 0);

        APIConnector.getInstance(this).getServices(params, this);
    }

    private String getLastId(){
        int maxId = 0;
        for (int i = 0; i < mAdapter.getItemCount(); i++){
            if (mAdapter.getItem(i).getId() > maxId ){
                maxId = mAdapter.getItem(i).getId();
            }
        }
        Log.d("Toshde", "Max topic id " + maxId);
        return String.valueOf((maxId));
    }

    private void loadMoreService(){

        if (params.has(BackEnd.PARAM_LAST_ID)){
            params.remove(BackEnd.PARAM_LAST_ID);
        }
        params.put(BackEnd.PARAM_LAST_ID, getLastId());

        APIConnector.getInstance(this).getServices(params, this);
    }

    private void parseLoadedServices(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){
                JSONArray services = response.getJSONArray("services");
                for (int i = 0; i < services.length(); i++){
                    mAdapter.add(JsonUtils.getServiceFromJsonObject(services.getJSONObject(i)));
                }
                if (mAdapter.getItemCount() > 0){
                    currentServicesSize = mAdapter.getItemCount();
                    recyclerView.setVisibility(View.VISIBLE);
                    loadingScreen.setVisibility(View.GONE);
                    connectionError.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    loadingScreen.setVisibility(View.GONE);
                    tvNoDataInfo.setText("No " + serviceType + "s found.");
                    connectionError.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseMoreLoadedServices(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)) {

                JSONArray services = response.getJSONArray("services");
                for (int i = 0; i < services.length(); i++) {
                    mAdapter.add(JsonUtils.getServiceFromJsonObject(services.getJSONObject(i)));
                }
                if (mAdapter.getItemCount() > currentServicesSize) {
                    currentServicesSize = mAdapter.getItemCount();
                    isLoadingMore = false;
                    mAdapter.notifyDataSetChanged();
                } else {
                    isLoadingMore = true;
                    noMoreEventsFound = true;
                    hideSnackBar();
                    snackbar = Snackbar.make(recyclerView, "No more " + serviceType + "s found.", Snackbar.LENGTH_LONG);
                    TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                    snackbar.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_services, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.activity_services_action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra(SearchResultsActivity.SEARCH_CONTEXT,
                    SearchResultsActivity.SERVICES_SEARCH);
            intent.putExtra(ServicesActivity.KEY_SERVICE_TYPE, serviceType);
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.activity_services_action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.activity_services_action_add_your_card:
                startActivity(new Intent(getApplicationContext(), NewCardActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        if (isLoadingMore){
            hideSnackBar();
            snackbar = Snackbar.make(recyclerView, "Loading more...", Snackbar.LENGTH_LONG);
            TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
            snackbar.show();
        } else {
            progressWheel.startSpinning();
            loadingScreen.setVisibility(View.VISIBLE);
            connectionError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        if (isLoadingMore){
            hideSnackBar();
            snackbar = Snackbar.make(recyclerView, "Unable to load more"+serviceType+"s, Check your network status.", Snackbar.LENGTH_LONG);
            TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
            snackbar.show();
        } else {
            progressWheel.stopSpinning();
            connectionError.setVisibility(View.VISIBLE);
            loadingScreen.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        if (isLoadingMore){
            parseMoreLoadedServices(response);
        } else {
            progressWheel.stopSpinning();
            parseLoadedServices(response);
        }
    }
}
