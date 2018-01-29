package com.mustmobile.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.mustmobile.ChangeInformationActivity;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.ReaderActivity;
import com.mustmobile.adapter.UpdatesListAdapter;
import com.mustmobile.fragment.academics.SemesterDatesFragment;
import com.mustmobile.fragment.timetable.TeachingTimetableFragment;
import com.mustmobile.fragment.library.BooksFragment;
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
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class MainFragment extends BaseFragment {


    private Button retryButton, reportButton;
    private ListView mListView;
    private UpdatesListAdapter mAdapter;
    private ArrayList<Update> updateArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;
    private Button downloadsButton, semesterDatesButton, timetableButton, libraryButton;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_main_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_main_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_main_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_main_report_button);
        downloadsButton = (Button) view.findViewById(R.id.fragment_main_button_download);
        semesterDatesButton = (Button) view.findViewById(R.id.fragment_main_button_semester_dates);
        timetableButton = (Button) view.findViewById(R.id.fragment_main_button_timetable);
        libraryButton = (Button) view.findViewById(R.id.fragment_main_button_library);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_main_no_connection);

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

        loadingMoreFooter = Helper.at(getActivity()).getLoadingMoreFooter();
        mListView.addFooterView(loadingMoreFooter);


        timetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .replaceFragment(new TeachingTimetableFragment().newInstance(FragmentHelp.FRAGMENT_TEACHING_TIMETABLE));
            }
        });
        downloadsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .replaceFragment(new DownloadsFragment().newInstance(FragmentHelp.FRAGMENT_DOWNLOADS));
            }
        });

        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .replaceFragment(new BooksFragment().newInstance(FragmentHelp.FRAGMENT_LIBRARY_BOOKS));
            }
        });

        semesterDatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .replaceFragment(new SemesterDatesFragment().newInstance(FragmentHelp.FRAGMENT_SEMESTER_DATES));
            }
        });

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

        connectAndRespond();

        if (!user.getConfirmedInformation()){
            try {
                showConfirmInformationDialog();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void showConfirmInformationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm your information")
                .setMessage("Hello, "+user.getName()+".\nThis is what we know about you!" +
                        "\n\nProgram of study :\n"+user.getProgramName()+"\n\nStage : "+user.getStage()+"" +
                        "\n\nProgram initials : " + user.getProgramCode()+"\n(as they appear on timetables)." +
                        "\n\nThe app will use this information for accuracy and AUTOMATION of the data and services you get.")
                .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), ChangeInformationActivity.class));
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(getString(R.string.pref_confirmed_information), true);
                        editor.putString(getString(R.string.pref_user_program), user.getProgramName());
                        editor.putString(getString(R.string.pref_user_stage),user.getStage());
                        editor.putString(getString(R.string.pref_user_program_code), user.getProgramCode());
                        editor.commit();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void connectAndRespond(){

        Client.post(Client.absoluteUrl("updates/mostread"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()){
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                } else {
                    noConnection.setVisibility(View.VISIBLE);
                    loadingIndicator.setVisibility(View.GONE);
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
                if (throwable.getCause() instanceof ConnectTimeoutException){
                    Log.d("Toshde", "Connection timed out!");
                    connectAndRespond();
                }
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


}
