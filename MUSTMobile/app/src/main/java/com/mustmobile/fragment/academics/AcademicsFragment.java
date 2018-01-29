package com.mustmobile.fragment.academics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.ReaderActivity;
import com.mustmobile.adapter.UpdatesListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Update;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.FragmentHelp;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AcademicsFragment extends BaseFragment{

    private final String TAG = AcademicsFragment.class.getSimpleName();
    private Button retryButton, reportButton;
    private ListView mListView;
    private UpdatesListAdapter mAdapter;
    private ArrayList<Update> updateArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;
    private TextView tvHeading;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static AcademicsFragment newInstance(int sectionNumber) {
        AcademicsFragment fragment = new AcademicsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AcademicsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_academics, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_academics_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_academics_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_academics_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_academics_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_academics_no_connection);
        tvHeading = (TextView) view.findViewById(R.id.fragment_academics_heading);
        tvHeading.setText("Notice Board");
        return view;
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
        String url = Client.absoluteUrl("schoolnotices/"+user.getSchoolCode());
        Client.post(url, null, new JsonHttpResponseHandler() {
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
                    Update n = new Update(content, title, time, timesRead, updateId);
                    updateArrayList.add(n);
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
        inflater.inflate(R.menu.menu_fragment_academics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_fragment_academics_my_grades:
                showYearDialog();
                return true;
            case R.id.action_fragment_academics_programs:
                replaceFragment(new ProgramsFragment().newInstance(FragmentHelp.FRAGMENT_PROGRAMS));
                return true;
            default:
                return false;
        }
    }

    private void showYearDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("See grades for...")
                .setItems(new String[]{"Year one", "Year two", "Year three", "Year four"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                replaceFragment(GradesFragment.newInstance("year_one"));
                                break;
                            case 1:
                                replaceFragment(GradesFragment.newInstance("year_two"));
                                break;
                            case 2:
                                replaceFragment(GradesFragment.newInstance("year_three"));
                                break;
                            case 3:
                                replaceFragment(GradesFragment.newInstance("year_four"));
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        setDialog(builder.create());
        getDialog().show();
    }

    private void replaceFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(AcademicsFragment.class.getSimpleName())
                .replace(R.id.activity_main_fragment_container, fragment)
                .commit();
    }
}
