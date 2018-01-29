package com.mustmobile;

import android.content.Context;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.adapter.EclassVideoListAdapter;
import com.mustmobile.model.EClassVideo;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EClassActivity extends AppCompatActivity{

    private static final String TAG  = EClassActivity.class.getSimpleName();
    private ListView mListView;
    private View loadingMoreFooter;
    private ViewGroup loadingIndicator, noConnection;
    private Button retryButton, reportButton;
    private TextView playingTitle;
    private static EclassVideoListAdapter mAdapter;
    private static ArrayList<EClassVideo> eClassVideoArrayList;
    private ViewGroup videoViewContainer;
    private VideoView videoView;
    private ProgressBar videoLoader;
    private MediaController mediaController;
    private int nextVideo = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclass);
        loadingIndicator = (ViewGroup) findViewById(R.id.activity_eclass_loading_indicator);
        noConnection = (ViewGroup) findViewById(R.id.activity_eclass_no_connection);
        retryButton = (Button) findViewById(R.id.activity_eclass_retry_button);
        reportButton = (Button) findViewById(R.id.activity_eclass_report_button);
        mListView = (ListView) findViewById(R.id.activity_eclass_listview);
        videoViewContainer = (ViewGroup) findViewById(R.id.activity_eclass_videoview_container);
        playingTitle = (TextView) findViewById(R.id.activity_eclass_playing_title);
        videoView = (VideoView) findViewById(R.id.activity_eclass_videoview);
        videoLoader = (ProgressBar) findViewById(R.id.activity_eclass_video_loader);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(playingTitle);

        eClassVideoArrayList = new ArrayList<>();
        mAdapter = new EclassVideoListAdapter(this, eClassVideoArrayList);

        loadingMoreFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);

        mListView.addFooterView(loadingMoreFooter);
        mListView.setAdapter(mAdapter);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "Ready");
                videoLoader.setVisibility(View.GONE);
                videoView.start();
                videoView.requestFocus();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideo(nextVideo);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playVideo(position);
            }
        });

        connectAndRespond();

    }

    private void playVideo(int videoNumber){
        finishVideo();
        nextVideo++;
        EClassVideo mVideo = eClassVideoArrayList.get(videoNumber);
        playingTitle.setVisibility(View.VISIBLE);
        playingTitle.setText("Playing : " + mVideo.getTitle());

        videoLoader.setVisibility(View.VISIBLE);
        Uri videoUri = Uri.parse(Client.absoluteUrl(mVideo.getUrl()).replace(" ","%20"));
        Log.d("Toshde",videoUri.toString());
        videoView.setVideoURI(videoUri);
    }

    private void finishVideo(){
        if (videoView.isPlaying() && videoView != null){
            videoView.stopPlayback();
        }
    }

    private void pauseVideo(){
        if (videoView.isPlaying() && videoView != null && videoView.canPause()){
            videoView.pause();
        }
    }

    private void resumeVideo(){
        if (videoView.isPlaying() && videoView != null){
            videoView.resume();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        resumeVideo();
    }

    private void connectAndRespond(){
        String url = Client.absoluteUrl("eclassvideos/it");
        Client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                loadingIndicator.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                videoViewContainer.setVisibility(View.GONE);
                playingTitle.setVisibility(View.GONE);
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
                Helper.at(getApplicationContext()).simpleToast(responseString);
                Log.d(TAG, "Failure "+responseString);
            }
        });
    }

    private void parseConnectionResponse(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){

                JSONArray videos = response.getJSONArray(Response.DATA);

                for (int i = 0; i < videos.length(); i++){
                    JSONObject video = videos.getJSONObject(i);
                    EClassVideo v = new EClassVideo();
                    v.setTitle(video.getString(Response.EClassVideoData.TITLE));
                    v.setUploadTime(video.getString(Response.EClassVideoData.UPLOAD_TIME));
                    v.setAuthor(video.getString(Response.EClassVideoData.AUTHOR));
                    v.setUrl(video.getString(Response.EClassVideoData.URL));
                    v.setDescription(video.getString(Response.EClassVideoData.DESCRIPTION));
                    v.setViews(video.getString(Response.EClassVideoData.VIEWS));
                    v.setThumbnail(video.getString(Response.EClassVideoData.THUMBNAIL));
                    eClassVideoArrayList.add(v);
                }
                mAdapter.notifyDataSetChanged();
                videoViewContainer.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                playVideo(nextVideo);

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseVideo();
    }

    @Override
    public void onStop() {
        super.onStop();
        finishVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finishVideo();
    }
}

