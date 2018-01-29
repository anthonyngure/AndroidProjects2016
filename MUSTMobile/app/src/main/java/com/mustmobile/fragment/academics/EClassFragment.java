package com.mustmobile.fragment.academics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.EclassVideoListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.EClassVideo;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EClassFragment extends BaseFragment{

    private static final String TAG  = EClassFragment.class.getSimpleName();
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

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static EClassFragment newInstance(int sectionNumber) {
        EClassFragment fragment = new EClassFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eclassvideos, container, false);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_eclassvideos_loading_indicator);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_eclassvideos_no_connection);
        retryButton = (Button) view.findViewById(R.id.fragment_eclassvideos_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_eclassvideos_report_button);
        mListView = (ListView) view.findViewById(R.id.fragment_eclassvideos_listview);
        videoViewContainer = (ViewGroup) view.findViewById(R.id.fragment_eclassvideos_videoview_container);
        playingTitle = (TextView) view.findViewById(R.id.fragment_eclassvideos_playing_title);
        videoView = (VideoView) view.findViewById(R.id.fragment_eclassvideos_videoview);
        videoLoader = (ProgressBar) view.findViewById(R.id.fragment_eclassvideos_video_loader);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(playingTitle);

        eClassVideoArrayList = new ArrayList<>();
        mAdapter = new EclassVideoListAdapter(getActivity(), eClassVideoArrayList);

        loadingMoreFooter = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer_loading_more, null, false);

        mListView.addFooterView(loadingMoreFooter);
        mListView.setAdapter(mAdapter);

        connectAndRespond();

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

        /*videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Helper.at(getActivity()).simpleToast("An error occurred");
                return false;
            }
        });*/

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
    }

    private void playVideo(int videoNumber){
        finishVideo();
        nextVideo++;
        EClassVideo mVideo = eClassVideoArrayList.get(videoNumber);
        playingTitle.setVisibility(View.VISIBLE);
        playingTitle.setText("Playing : " + mVideo.getTitle());

        videoLoader.setVisibility(View.VISIBLE);
        Uri videoUri = Uri.parse(Client.absoluteUrl(mVideo.getUrl()));
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

    @Override
    protected void connectAndRespond(){
        String url = Client.absoluteUrl("eclassvideos/it");
        Client.post(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()){
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                    videoViewContainer.setVisibility(View.GONE);
                    playingTitle.setVisibility(View.GONE);
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
                Log.d(TAG, "Failure " + responseString);
                loadingIndicator.setVisibility(View.GONE);
                //showNetworkErrorDialog(statusCode);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response) {
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
                noConnection.setVisibility(View.GONE);
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
    public void onDetach() {
        super.onDetach();
        finishVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finishVideo();
    }
}

