package ke.co.elmaxdevelopers.eventskenya.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.adapter.CommentsListAdapter;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Comment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.JsonUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

public class CommentsActivity extends AppCompatActivity
        implements LoadingListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_EVENT = "event";

    private CommentsListAdapter mAdapter;
    private ListView listView;
    private EditText etComment;
    private Event event;
    private View footer;
    private Comment newComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Snackbar snackbar;
    private TextInputLayout commentInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comments);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView = (ListView) findViewById(R.id.activity_comments_listview);

        commentInputLayout = (TextInputLayout) findViewById(R.id.activity_comments_inputlayout);
        etComment = (EditText) findViewById(R.id.activity_comments_write_a_comment);
        etComment.addTextChangedListener(Helper.createTextWatcher(160, commentInputLayout, etComment,
                getString(R.string.hint_type_comment)));

        if (getIntent().getExtras() != null){
            ArrayList<Event> events =  getIntent().getParcelableArrayListExtra(EXTRA_EVENT);
            event = events.get(0);
        }

        getSupportActionBar().setTitle(event.getName());
        getSupportActionBar().setSubtitle(event.getVenue());

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                refreshComments();
            }
        });


        mAdapter = new CommentsListAdapter(this);

        listView.setAdapter(mAdapter);

        if (event.getComments().size() != 0 && event.getComments() != null){
            mAdapter.addAll(event.getComments());
            scroll();
        }

        footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.sending_comment_footer, null, false);
        footer.setVisibility(View.GONE);

        listView.addFooterView(footer);
    }

    private void scroll() {
        listView.setSelection(listView.getCount());
    }

    public void sendComment(View view) {
        if (!etComment.getText().toString().trim().isEmpty()){

            newComment = new Comment();
            newComment.setUsername(PrefUtils.getInstance(this).getSetUsername());
            newComment.setContent(etComment.getText().toString());
            newComment.setEventId(event.getId());
            commentInputLayout.setErrorEnabled(false);
            scroll();
            deliverComment();
        } else {
            commentInputLayout.setError(getString(R.string.empty_comment));
        }
    }


    private String getLastCommentId(){
        long maxId = 0;
        for (int i = 0; i < mAdapter.getCount(); i++){
            if (mAdapter.getItem(i).getId() > maxId ){
                maxId = mAdapter.getItem(i).getId();
            }
        }
        Log.d("Toshde", "Max comment id " + maxId);
        return String.valueOf((maxId));
    }

    private void deliverComment(){

        RequestParams params = new RequestParams();
        params.add(BackEnd.PARAM_COMMENT_CONTENT, newComment.getContent());
        params.add(BackEnd.PARAM_LAST_ID, getLastCommentId());
        APIConnector.getInstance(getApplicationContext()).addComment(newComment.getEventId(), params, this);
    }

    private void parseResponse(JSONObject response){

        ArrayList<Comment> loadedComments = new ArrayList<>();
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase(Response.SUCCESS)){
                JSONArray commentsArray = response.getJSONArray("comments");
                for (int i = 0; i < commentsArray.length(); i++){
                    loadedComments.add(JsonUtils.getCommentFromJsonObject(commentsArray.getJSONObject(i)));
                }
                if (loadedComments.size() > 0){
                    mAdapter.addAll(loadedComments);
                    event.getComments().addAll(loadedComments);
                    scroll();
                } else {
                    Helper.snack(swipeRefreshLayout, getString(R.string.no_new_comments));
                }
            } else {
                // TODO handle  server failure
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            footer.setVisibility(View.GONE);
        }
        event.setNewComments(0);
        DataController.getInstance(this).sendBroadCastOnEventDataChanged(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.activity_comments_action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        if (!swipeRefreshLayout.isRefreshing()){
            etComment.getText().clear();
            footer.setVisibility(View.VISIBLE);
            scroll();
        }
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        if (swipeRefreshLayout.isRefreshing()){
            Helper.snack(swipeRefreshLayout, getString(R.string.failed_to_refresh));
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideSnackBar();
            footer.setVisibility(View.GONE);
            snackbar = Snackbar.make(swipeRefreshLayout, getString(R.string.error_sending_comment), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliverComment();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private void hideSnackBar(){
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
            snackbar = null;
        }
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response){
        hideSnackBar();
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        parseResponse(response);
    }

    private void refreshComments(){
        RequestParams params = new RequestParams();
        params.add(BackEnd.PARAM_LAST_ID, getLastCommentId());
        swipeRefreshLayout.setRefreshing(true);
        APIConnector.getInstance(this).refreshComments(event.getId(), params, this);
    }

    @Override
    public void onRefresh() {
        refreshComments();
    }

}
