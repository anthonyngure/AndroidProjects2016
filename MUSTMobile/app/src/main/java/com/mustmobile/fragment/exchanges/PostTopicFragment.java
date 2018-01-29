package com.mustmobile.fragment.exchanges;

import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.R;
import com.mustmobile.ForumTopicsActivity;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PostTopicFragment extends BaseFragment {

    private Button retryButton, reportButton, sendButton;
    private ViewGroup loadingIndicator, noConnection, postStackContainer;
    private EditText etQuestion, etTag;
    private String content, tag;

    public static PostTopicFragment newInstance() {
        PostTopicFragment fragment = new PostTopicFragment();
        return fragment;
    }

    public PostTopicFragment() {
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
        View view = inflater.inflate(R.layout.fragment_post_topic, container, false);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_post_exchange_loading_indicator);
        postStackContainer = (ViewGroup) view.findViewById(R.id.fragment_post_exchange_content);
        retryButton = (Button) view.findViewById(R.id.fragment_post_exchange_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_post_exchange_report_button);
        sendButton = (Button) view.findViewById(R.id.fragment_post_exchange_button_send);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_post_exchange_no_connection);
        etQuestion = (EditText) view.findViewById(R.id.fragment_post_exchange_question);
        etTag = (EditText) view.findViewById(R.id.fragment_post_exchange_tag);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                content = etQuestion.getText().toString();
                tag = etTag.getText().toString();

                if (!fieldsAreEmpty()){
                    if (content.length() >= 50){
                        connectAndRespond();
                    } else {
                        Helper.at(getActivity())
                                .simpleToast("For constructive discussions, your topic must contain more than 50 characters!");
                    }
                } else {
                    Helper.at(getActivity()).simpleToast("You must fill all the fields!");
                }
            }
        });
    }

    private boolean fieldsAreEmpty(){
        if (!content.isEmpty() && !tag.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void connectAndRespond(){

        RequestParams params = new RequestParams();
        params.put("content", content);
        params.put("forum_code", ForumTopicsActivity.forumCode);
        params.put("author_name", user.getName());
        params.put("author_number", user.getNumber());
        params.put("tag", tag.trim().replace(" ",""));

        Client.post(Client.absoluteUrl("posttopic"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    postStackContainer.setVisibility(View.GONE);
                    ((ForumTopicsActivity) getActivity()).getSupportActionBar().setSubtitle("Sending...");
                } else {
                    postStackContainer.setVisibility(View.GONE);
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
                Log.d("Toshde", "Error posting topic " + responseString);
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                ((ForumTopicsActivity) getActivity()).getSupportActionBar().setSubtitle("");
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                noConnection.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
                Helper.at(getActivity()).simpleToast("Topic submitted successfully!");
                ((ForumTopicsActivity) getActivity()).getSupportActionBar().setSubtitle("Topics");
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            } else {
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
