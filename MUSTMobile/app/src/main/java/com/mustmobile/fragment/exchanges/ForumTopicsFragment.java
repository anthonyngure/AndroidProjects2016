package com.mustmobile.fragment.exchanges;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.R;
import com.mustmobile.ExchangesActivity;
import com.mustmobile.ForumTopicsActivity;
import com.mustmobile.adapter.TopicsListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Topic;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ForumTopicsFragment extends BaseFragment {

    Button retryButton, reportButton;
    private ListView mListView;
    private TopicsListAdapter mAdapter;
    private ArrayList<Topic> topicArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View loadingMoreFooter;
    private ViewGroup noData;
    private int currentTopicsSize = 0, loadCount = 0;
    private TextView tvFooterMessage;
    private boolean isLoading = false;
    private boolean noMoreData = false;

    public static ForumTopicsFragment newInstance() {
        ForumTopicsFragment fragment = new ForumTopicsFragment();
        return fragment;
    }

    public ForumTopicsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_topics, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_forum_topics_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_forum_topics_loading_indicator);
        noData = (ViewGroup) view.findViewById(R.id.fragment_forum_topics_no_data);
        retryButton = (Button) view.findViewById(R.id.fragment_forum_topics_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_forum_topics_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_forum_topics_no_connection);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        topicArrayList = new ArrayList<>();
        mAdapter = new TopicsListAdapter(getActivity(), topicArrayList);
        mListView.setAdapter(mAdapter);

        loadingMoreFooter = Helper.at(getActivity()).getLoadingMoreFooter();
        tvFooterMessage = (TextView) loadingMoreFooter.findViewById(R.id.footer_loading_more_message);
        tvFooterMessage.setVisibility(View.GONE);

        mListView.addFooterView(loadingMoreFooter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = topicArrayList.get(position);
                Intent intent = new Intent(getActivity(), ExchangesActivity.class);
                intent.putExtra("topic_id", topic.getTopicId());
                intent.putExtra("forum_code", topic.getForumCode());
                intent.putExtra("content", topic.getContent());
                intent.putExtra("author_name", topic.getAuthorName());
                intent.putExtra("author_number", topic.getAuthorNumber());
                intent.putExtra("tag", topic.getTopicTag());
                intent.putExtra("views", topic.getViews());
                intent.putExtra("answers", topic.getAnswers());
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Topic topic = (Topic) parent.getItemAtPosition(position);
                String userNumber = User.at(getActivity()).getNumber();
                String userPrivilege = User.at(getActivity()).getPrivilegeCode();
                if (userNumber.equalsIgnoreCase(topic.getAuthorNumber())
                        || userPrivilege.equalsIgnoreCase(User.FORUM_ADMIN)) {
                    deleteTopic(topic);
                    return true;
                } else {
                    Helper.at(getActivity()).simpleToast("You don't have privilege to delete this topic!");
                    return false;
                }
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem+visibleItemCount) == (totalItemCount)){
                    if (!isLoading && !noMoreData){
                        isLoading = true;
                        loadCount++;
                        Log.d("Toshde", "At the bottom of list view and its not loading..");
                        Log.d("Toshde", "LoadCount : "+loadCount);
                        loadMoreTopics();
                    }
                }
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
        Client.post(Client.absoluteUrl("topics/" + ForumTopicsActivity.forumCode), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    mListView.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
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
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray topics = response.getJSONArray(Response.DATA);
                for (int i = 0; i < topics.length(); i++){
                    JSONObject topic = topics.getJSONObject(i);
                    Topic t = new Topic();
                    t.setTopicId(topic.getString(Response.TopicData.ID));
                    t.setForumCode(topic.getString(Response.TopicData.FORUM_CODE));
                    t.setContent(topic.getString(Response.TopicData.CONTENT));
                    t.setAuthorName(topic.getString(Response.TopicData.AUTHOR_NAME));
                    t.setAuthorNumber(topic.getString(Response.TopicData.AUTHOR_NUMBER));
                    t.setTime(topic.getString(Response.TopicData.TIME));
                    t.setStackTag(topic.getString(Response.TopicData.TAG));
                    t.setViews(topic.getString(Response.TopicData.VIEWS));
                    t.setAnswers(topic.getString(Response.TopicData.ANSWERS));
                    topicArrayList.add(t);
                }
                if (topicArrayList.size() > 0){
                    currentTopicsSize = topicArrayList.size();
                    mAdapter.notifyDataSetChanged();
                    mListView.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    noConnection.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                }
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteTopic(final Topic topic){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete this topic!");
        if (TextUtils.equals(topic.getAuthorNumber(), User.at(getActivity()).getNumber())){
            builder.setMessage("You can only delete topics you authored.\nPlease" +
                    " confirm you want to delete.\n\n"+Helper.at(getActivity()).reduceToTwoHundredCharacters(topic.getContent()));
        } else if(User.at(getActivity()).getPrivilegeCode().equalsIgnoreCase(User.FORUM_ADMIN)) {
            builder.setMessage("You can delete topics because you are a forum admin.\nPlease" +
                    " confirm you want to delete.\n\n"+Helper.at(getActivity()).reduceToTwoHundredCharacters(topic.getContent()));
        }
        builder.setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTopicSelected(topic);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();

    }


    private void deleteTopicSelected(final Topic topic){
        final String topicToDeleteId = topic.getTopicId();
        Client.post(Client.absoluteUrl("deletetopic/" + topicToDeleteId), null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Helper.at(getActivity()).simpleToast("Topic delete request sent.");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseDeleteTopicResponse(response, topic);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Helper.at(getActivity()).simpleToast("Unable to delete topic, try again later!");
            }
        });
    }

    private void parseDeleteTopicResponse(JSONObject response, Topic topic){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                try {
                    topicArrayList.remove(topic);
                } catch (Exception e){
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                Helper.at(getActivity()).simpleToast("Topic deleted successfully!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getLastTopicId(){
        int maxId = 0;
        for (int i = 0; i < topicArrayList.size(); i++){
            if (Integer.parseInt(topicArrayList.get(i).getTopicId()) > maxId ){
                maxId = Integer.parseInt(topicArrayList.get(i).getTopicId());
            }
        }
        Log.d("Toshde","Max topic id "+maxId);
        return String.valueOf((maxId-(Client.TOPIC_LOAD_LIMIT*loadCount)));
    }

    private void loadMoreTopics(){

        RequestParams params = new RequestParams();
        params.put("last_id", getLastTopicId());
        params.put("forum_code", ForumTopicsActivity.forumCode);
        Client.post(Client.absoluteUrl("moretopics"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
                    tvFooterMessage.setVisibility(View.GONE);
                } else {
                    tvFooterMessage.setVisibility(View.VISIBLE);
                    tvFooterMessage.setText("Unable to load more topics, Check your network status.");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseLoadMoreTopicsResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                tvFooterMessage.setVisibility(View.VISIBLE);
                isLoading = false;
                tvFooterMessage.setText("Unable to load more topics, Check your network status.");
            }
        });
    }

    private void parseLoadMoreTopicsResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray topics = response.getJSONArray(Response.DATA);
                for (int i = 0; i < topics.length(); i++){
                    JSONObject topic = topics.getJSONObject(i);
                    Topic t = new Topic();
                    t.setTopicId(topic.getString(Response.TopicData.ID));
                    t.setForumCode(topic.getString(Response.TopicData.FORUM_CODE));
                    t.setContent(topic.getString(Response.TopicData.CONTENT));
                    t.setAuthorName(topic.getString(Response.TopicData.AUTHOR_NAME));
                    t.setAuthorNumber(topic.getString(Response.TopicData.AUTHOR_NUMBER));
                    t.setTime(topic.getString(Response.TopicData.TIME));
                    t.setStackTag(topic.getString(Response.TopicData.TAG));
                    t.setViews(topic.getString(Response.TopicData.VIEWS));
                    t.setAnswers(topic.getString(Response.TopicData.ANSWERS));
                    if(!topicArrayList.contains(t)){
                        topicArrayList.add(t);
                    }
                }
                if (topicArrayList.size() > currentTopicsSize){
                    currentTopicsSize = topicArrayList.size();
                    tvFooterMessage.setVisibility(View.GONE);
                    isLoading = false;
                    mAdapter.notifyDataSetChanged();
                } else {
                    tvFooterMessage.setVisibility(View.VISIBLE);
                    noMoreData = true;
                    tvFooterMessage.setText("No more topics to show");
                }
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
