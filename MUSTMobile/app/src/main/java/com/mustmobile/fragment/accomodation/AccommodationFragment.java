package com.mustmobile.fragment.accomodation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.RentalsListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Rental;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AccommodationFragment extends BaseFragment{

    private final String TAG = AccommodationFragment.class.getSimpleName();
    private Button retryButton, reportButton;
    private ListView mListView;
    private RentalsListAdapter mAdapter;
    private ArrayList<Rental> rentalArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;
    private TextView tvHeading;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static AccommodationFragment newInstance(int sectionNumber) {
        AccommodationFragment fragment = new AccommodationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AccommodationFragment() {
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
        View view = inflater.inflate(R.layout.fragment_accomodation, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_accomodation_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_accomodation_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_accomodation_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_accomodation_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_accomodation_no_connection);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rentalArrayList = new ArrayList<>();
        mAdapter = new RentalsListAdapter(getActivity(), rentalArrayList);
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
    protected void connectAndRespond(){
        Client.post(Client.absoluteUrl("rentalsaround"), null, new JsonHttpResponseHandler() {
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
        Log.d(TAG, response.toString());
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray rentals = response.getJSONArray(Response.DATA);
                for (int i = 0; i < rentals.length(); i++){
                    JSONObject rental = rentals.getJSONObject(i);
                    Rental r = new Rental();
                    r.setName(rental.getString(Response.RentalData.NAME));
                    r.setLocation(rental.getString(Response.RentalData.LOCATION));
                    r.setContact(rental.getString(Response.RentalData.CONTACT));
                    r.setPrice(rental.getString(Response.RentalData.PRICE));
                    r.setDistance(rental.getString(Response.RentalData.DISTANCE));
                    r.setUrl(rental.getString(Response.RentalData.URL));
                    rentalArrayList.add(r);
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
        inflater.inflate(R.menu.menu_fragment_accomodation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
