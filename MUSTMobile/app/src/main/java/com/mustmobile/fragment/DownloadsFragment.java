package com.mustmobile.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.DownloadsListAdapter;
import com.mustmobile.model.Download;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.AppStorage;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DownloadsFragment extends BaseFragment {

    private final String TAG = DownloadsFragment.class.getSimpleName();
    private Button retryButton, reportButton;
    private ListView mListView;
    private DownloadsListAdapter mAdapter;
    private ArrayList<Download> downloadArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;
    private AlertDialog pDialog;
    private TextView tvProgress;
    private Button  openButton;
    private View dialogView;
    private LayoutInflater dialogInflater;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static DownloadsFragment newInstance(int sectionNumber) {
        DownloadsFragment fragment = new DownloadsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DownloadsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.at(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_downloads_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_downloads_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_downloads_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_downloads_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_downloads_no_connection);
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

        downloadArrayList = new ArrayList<>();
        mAdapter = new DownloadsListAdapter(getActivity(), downloadArrayList);
        mListView.setAdapter(mAdapter);

        loadingMoreFooter = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);
        mListView.addFooterView(loadingMoreFooter);
        TextView tvFooterMessage = (TextView) loadingMoreFooter.findViewById(R.id.footer_loading_more_message);
        tvFooterMessage.setVisibility(View.VISIBLE);
        tvFooterMessage.setText("No more downloads to show.");


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Download d = downloadArrayList.get(position);
                startDownload(d);
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
    public void connectAndRespond(){
        Client.post(Client.absoluteUrl("downloads/suggestions"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
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
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray downloads = response.getJSONArray(Response.DATA);
                for (int i = 0; i < downloads.length(); i++){
                    JSONObject download = downloads.getJSONObject(i);
                    Download d = new Download();
                    d.setName(download.getString(Response.DownloadData.NAME));
                    d.setDescription(download.getString(Response.DownloadData.DESCRIPTION));
                    d.setTimeUploaded(download.getString(Response.DownloadData.TIME_UPLOADED));
                    d.setTimesDownloaded(download.getString(Response.DownloadData.TIMES_DOWNLOADED));
                    d.setUrl(download.getString(Response.DownloadData.URL));
                    downloadArrayList.add(d);

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

    private void startDownload(final Download d){

        new AsyncTask<String, String, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDownloadDialog(d);
            }

            @Override
            protected String doInBackground(String... params) {
                int count;
                OutputStream output = null;
                try {
                    URL url = new URL(Client.absoluteUrl(params[0]).replace(" ","%20"));
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int lengthOfBook = connection.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    if (AppStorage.externalIsWritable() && AppStorage.externalIsReadable()){
                        output = new FileOutputStream(AppStorage.storeAs(AppStorage.DOWNLOADS_STORE, d.getName()));
                    } else {
                        showStorageErrorDialog();
                    }
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1){
                        total +=count;
                        publishProgress(""+(int)((total*100)/lengthOfBook));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (pDialog != null){
                    tvProgress.setText(String.valueOf(values[0])+"% ready.");
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (pDialog != null){
                    openButton.setEnabled(true);
                    openButton.setOnClickListener(getReadListener(d));
                }
            }
        }.execute(d.getUrl());
    }

    private void showDownloadDialog(Download d){

        initProgressDialog();

        if (dialogInflater == null){
            dialogInflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (dialogView == null){
            dialogView = dialogInflater.inflate(R.layout.book_download_progress, null);
            tvProgress = (TextView) dialogView.findViewById(R.id.book_download_progress_progress);
            openButton = (Button) dialogView.findViewById(R.id.book_download_progress_button_open);
            openButton.setEnabled(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setTitle(d.getName())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initProgressDialog();
                    }
                })
                .setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    private void initProgressDialog(){
        hideDialog();
        pDialog = null;
        dialogView  = null;
        dialogInflater = null;
        tvProgress = null;
        openButton = null;
    }

    private void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showStorageErrorDialog() {
        initProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.storage_error))
                .setPositiveButton(getActivity().getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getActivity().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    private View.OnClickListener getReadListener(final Download d){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog();
                Helper.at(getActivity()).simpleToast("Opening "+d.getName()+" for reading");
                File file = new File(AppStorage.retrieve(AppStorage.DOWNLOADS_STORE, d.getName()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try{
                    getActivity().startActivity(intent);
                } catch (Exception e){
                    Helper.at(getActivity()).simpleToast(getActivity().getString(R.string.book_openning_error));
                    e.printStackTrace();
                }
            }
        };
    }
}
