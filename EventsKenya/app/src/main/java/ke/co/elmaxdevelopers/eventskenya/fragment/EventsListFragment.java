package ke.co.elmaxdevelopers.eventskenya.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.adapter.EventsListAdapter;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.stickyheaderslibrary.DividerDecoration;
import ke.co.elmaxdevelopers.eventskenya.stickyheaderslibrary.library.StickyRecyclerHeadersDecoration;
import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.JsonUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;

/**
 * Created by Tosh on 12/30/2015.
 */
public class EventsListFragment extends Fragment
        implements LoadingListener {

    public static final String ACTION_CATEGORY_CHANGE = "com.eventskenya.ACTION_CATEGORY_CHANGE";
    private RecyclerView recyclerView;
    private ViewGroup loadingScreen, connectionError;
    private EventsListAdapter mAdapter;
    private boolean isLoadingMore = false;
    private int currentEventsSize = 0;
    private TextView tvConnectionErrorInfo;
    private int day = 0;
    private RequestParams params;
    private Snackbar snackbar;
    private ProgressWheel progressWheel;

    /**
     * noMoreEventsFound is true when loading events of day and loading more events returns no events
     * it is saved in the persistence table
     */
    private boolean noMoreEventsFound;

    /**
     * categoryChangeReceiver receives an intent sent when the events category is changed
     * When received new data is loaded
     */
    private BroadcastReceiver categoryChangeReceiver;

    /**
     * isToday flag for what day this fragment is runnning
     */

    private boolean isToday = false;
    private Button retryButton;

    public EventsListFragment(){

    }

    public static EventsListFragment newInstance(int day) {
        Bundle args = new Bundle();
        args.putInt(BackEnd.PARAM_DATE, day);
        EventsListFragment fragment = new EventsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        params = new RequestParams();
        this.day = getArguments().getInt(BackEnd.PARAM_DATE, 0);

        noMoreEventsFound = DataController.getInstance(getActivity())
                .persistenceNoMoreData(DateUtils.getIntegerDate(getFragmentDate()));

        if (getFragmentDate().equals(new LocalDate().toString())){
            isToday = true;
        } else {
            isToday = false;
        }
        setDateParams();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        loadingScreen = (ViewGroup) view.findViewById(R.id.loading_screen);
        connectionError = (ViewGroup) view.findViewById(R.id.connection_error);
        retryButton = (Button) view.findViewById(R.id.retry_button);
        tvConnectionErrorInfo = (TextView) view.findViewById(R.id.connection_error_info);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEvents();
            }
        });
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setHasFixedSize(false);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EventsListAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        // Add decoration for dividers between list items
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        recyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        mAdapter.registerReceiver(getActivity());

        ArrayList<Event> savedEvents = new ArrayList<>();

        for (Event event : DataController.getInstance(getActivity()).getSavedEvents()){
            if (event.getStartDate() == DateUtils.getIntegerDate(getFragmentDate())){
                savedEvents.add(event);
            }
        }

        /**
         * Check if there are saved events
         */
        if (savedEvents.size() > 0){
            //Helper.getInstance(getContext()).toast("Found saved events " + DataController.getInstance(getActivity()).getSavedEvents());
            /**
             * There are saved events for this day, show them
             */
            currentEventsSize = mAdapter.getItemCount();
            mAdapter.addAll(savedEvents);
            recyclerView.setVisibility(View.VISIBLE);
            loadingScreen.setVisibility(View.GONE);
            connectionError.setVisibility(View.GONE);
        } else if (savedEvents.size() <= 0 && isToday){
            /**
             * There are no saved events and its today
             */
            loadEvents();
        } else if (savedEvents.size() <= 0 && !isToday && PrefUtils.getInstance(getActivity()).allowLoadingEventsForAllTabs()){
            /**
             * Its not today but user wants to load events for all days visible
             */
            loadEvents();
        } else {
            /**
             * There are no saved events and its not today
             */

            setHasNotYetLoaded();

        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    Picasso.with(getActivity()).pauseTag(EventsListAdapter.class.getSimpleName());
                } else {
                    Picasso.with(getActivity()).resumeTag(EventsListAdapter.class.getSimpleName());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem ==(totalItemCount - visibleItemCount)){
                    if (!isLoadingMore && !noMoreEventsFound){
                        isLoadingMore = true;
                        loadMoreEvents();
                    }
                    if (noMoreEventsFound && !recyclerView.canScrollVertically(1)){
                        hideSnackBar();
                        snackbar = Snackbar.make(getView(), DateUtils.formatDateForDisplay(getFragmentDate()) + " : No more events found.",
                                Snackbar.LENGTH_LONG);
                        TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                        snackbar.show();
                    }
                }
            }
        });

        categoryChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_CATEGORY_CHANGE)){
                    if (isToday && (!isToday && PrefUtils.getInstance(getActivity()).allowLoadingEventsForAllTabs())){
                        /**
                         * Today load events afresh
                         * AND Its not today but allows loading events for all tabs
                         */
                        isLoadingMore = false; //Allow loading more
                        noMoreEventsFound = false; // Allow loading more
                        mAdapter.clear();
                        loadEvents();
                    } else if (!isToday && !PrefUtils.getInstance(getActivity()).allowLoadingEventsForAllTabs()){
                        if (mAdapter.getItemCount() > 0){
                            mAdapter.clear();
                            setHasNotYetLoaded();
                        }
                    }



                    if (isToday || PrefUtils.getInstance(context).allowLoadingEventsForAllTabs()){
                        isLoadingMore = false; //Allow loading more
                        noMoreEventsFound = false; // Allow loading more
                        mAdapter.clear();
                        loadEvents();
                    } else {

                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(ACTION_CATEGORY_CHANGE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(categoryChangeReceiver, intentFilter);
    }

    private void setHasNotYetLoaded(){
        recyclerView.setVisibility(View.GONE);
        loadingScreen.setVisibility(View.GONE);
        connectionError.setVisibility(View.VISIBLE);
        connectionError.findViewById(R.id.connection_error_image).setVisibility(View.GONE);
        retryButton.setText("Check " + DateUtils.formatDateForDisplay(getFragmentDate()) + " events");
        tvConnectionErrorInfo.setText("Events happening on " + DateUtils.formatDateForDisplay(getFragmentDate())
                + " are not yet loaded, Your device date is " + DateUtils.formatDateForDisplay(new LocalDate().toString())
                + ".\n(This is to ensure minimum data consumption," +
                " however you can allow loading events for all tabs in the settings)");
    }

    private void hideSnackBar(){
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    protected void loadEvents(){
        currentEventsSize = 0;
        mAdapter.clear();
        params.put(BackEnd.PARAM_LAST_ID, 0);
        APIConnector.getInstance(getActivity())
                .loadEvents(params, "loadEvents at fragment " + (day + 1), this);
    }

    private String getLastEventId(){
        long maxId = 0;
        for (int i = 0; i < mAdapter.getItems().size(); i++){
            if (mAdapter.getItems().get(i).getId() > maxId ){
                maxId = mAdapter.getItems().get(i).getId();
            }
        }
        Log.d("Toshde", "Max topic id " + maxId);
        return String.valueOf((maxId));
    }

    private void loadMoreEvents() {

        if (params.has(BackEnd.PARAM_LAST_ID)) {
            params.remove(BackEnd.PARAM_LAST_ID);
        }

        params.put(BackEnd.PARAM_LAST_ID, getLastEventId());
        APIConnector.getInstance(getActivity().getApplicationContext())
                .loadEvents(params,"loadMoreEvents at fragment "+(day+1), this);
    }

    private void parseLoadedEvents(JSONObject response){
        ArrayList<Event> loadedEvents = new ArrayList<>();
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)) {
                JSONArray events = response.getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    loadedEvents.add(JsonUtils.getEventFromJsonObject(events.getJSONObject(i)));
                }
                if (loadedEvents.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    loadingScreen.setVisibility(View.GONE);
                    connectionError.setVisibility(View.GONE);
                    mAdapter.addAll(loadedEvents);
                    /**
                     * Persistence for this date is changed if it was previously set to no more data
                     * This can occur when a user clicks retry button on a day that was previously set
                     * to no more data but it happens to load data on this event occuring
                     */
                    DataController.getInstance(getActivity())
                            .addPersistence(DateUtils.getIntegerDate(getFragmentDate()), mAdapter.getItemCount(), 0);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    loadingScreen.setVisibility(View.GONE);
                    connectionError.setVisibility(View.VISIBLE);
                    connectionError.findViewById(R.id.connection_error_image).setVisibility(View.VISIBLE);
                    retryButton.setText("Retry");
                    tvConnectionErrorInfo.setText("No Events Found for " + DateUtils.formatDateForDisplay(getFragmentDate()) +
                            " .Your device date is " + DateUtils.formatDateForDisplay(new LocalDate().toString())
                            + " and you are interested in " + PrefUtils.getInstance(getActivity()).getSetCategory() + " events"
                            + " happening within " + PrefUtils.getInstance(getActivity()).getSetCounty()
                            +".\nYou can change this in settings.");

                    DataController.getInstance(getActivity())
                            .addPersistence(DateUtils.getIntegerDate(getFragmentDate()), mAdapter.getItemCount(), 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseMoreLoadedEvents(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){
                JSONArray events = response.getJSONArray("events");
                for (int i = 0; i < events.length(); i++){
                    mAdapter.add(JsonUtils.getEventFromJsonObject(events.getJSONObject(i)));
                }
                if (mAdapter.getItemCount() > currentEventsSize){
                    currentEventsSize = mAdapter.getItemCount();
                    isLoadingMore = false;
                    hideSnackBar();
                } else {
                    isLoadingMore = true;
                    noMoreEventsFound = true;
                    DataController.getInstance(getActivity())
                            .addPersistence(DateUtils.getIntegerDate(getFragmentDate()), mAdapter.getItemCount(), 1);
                    hideSnackBar();
                    snackbar = Snackbar.make(getView(), DateUtils.formatDateForDisplay(getFragmentDate()) + " : No more events found.",
                            Snackbar.LENGTH_LONG);
                    TextView tvSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tvSnackBar.setTextColor(Color.parseColor(getString(R.string.sub_theme)));
                    snackbar.show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String getFragmentDate(){
        return new LocalDate().plusDays(day).toString();
    }

    private void setDateParams(){
        if (params.has(BackEnd.PARAM_DATE)){
            params.remove(BackEnd.PARAM_DATE);
        }
        params.put(BackEnd.PARAM_DATE, getFragmentDate());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter.getItemCount() > 0){
            DataController.getInstance(getActivity()).saveAllEvents(mAdapter.getItems());
        }
        /**
         * Invalidation of Singleton classes during first run
         */
        if (PrefUtils.getInstance(getActivity()).getIsFirstInstance()){
            PrefUtils.getInstance(getActivity()).writeIsFirstInstance(false);
            PrefUtils.getInstance(getActivity()).invalidate();
            DataController.getInstance(getActivity()).invalidate();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter.unregisterBroadcast(getActivity());
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(categoryChangeReceiver);
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        if (isLoadingMore){
            hideSnackBar();
            snackbar = Snackbar.make(getView(), "Loading more...",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        } else {
            progressWheel.startSpinning();
            loadingScreen.setVisibility(View.VISIBLE);
            connectionError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response){
        if (isLoadingMore){
            parseMoreLoadedEvents(response);
        } else {
            progressWheel.stopSpinning();
            parseLoadedEvents(response);
        }
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        if (isLoadingMore){
            isLoadingMore = true;
            hideSnackBar();
            snackbar = Snackbar.make(getView(),"No internet connection!",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadMoreEvents();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        } else {
            connectionError.setVisibility(View.VISIBLE);
            connectionError.findViewById(R.id.connection_error_image).setVisibility(View.VISIBLE);
            tvConnectionErrorInfo.setText(getString(R.string.connection_error_message));
            loadingScreen.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            progressWheel.stopSpinning();
        }
    }
}
