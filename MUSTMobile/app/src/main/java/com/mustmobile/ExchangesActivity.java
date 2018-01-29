package com.mustmobile;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.media.MediaPlayer;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.adapter.ExchangesListAdapter;
import com.mustmobile.model.Exchange;
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


public class ExchangesActivity extends AppCompatActivity {

    private EditText etMessage;
    private ListView mListView;
    private ImageButton buttonSend;
    private ExchangesListAdapter mAdapter;
    private ArrayList<Exchange> exchangeArrayList;
    private ViewGroup loadingIndicator, noConnection;
    private View headerView;
    private Button retryButton, reportButton;
    private Topic thisTopic;
    private int loadedExchanges = 0;
    private MediaPlayer mediaPlayer;
    private Boolean hasBeenPaused;
    private String lastId = null;

    public ExchangesActivity(){
        thisTopic = new Topic();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hasBeenPaused = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasBeenPaused){
            loadNewExchanges();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchanges);

        if(getIntent().getExtras() != null){
            thisTopic.setAuthorNumber(resolveIntent("author_number"));
            thisTopic.setAuthorName(resolveIntent("author_name"));
            thisTopic.setAnswers(resolveIntent("answers"));
            thisTopic.setViews(resolveIntent("views"));
            thisTopic.setForumCode(resolveIntent("forum_code"));
            thisTopic.setContent(resolveIntent("content"));
            thisTopic.setTime(resolveIntent("time"));
            thisTopic.setStackTag(resolveIntent("tag"));
            thisTopic.setTopicId(resolveIntent("topic_id"));
        }
        exchangeArrayList = new ArrayList<>();
        mAdapter = new ExchangesListAdapter(this, exchangeArrayList);
        getSupportActionBar().setSubtitle(thisTopic.getContent());
        initControls();
        connectAndRespond();
    }

    private String resolveIntent(String key) {
        return getIntent().getExtras().getString(key);
    }

    private void initControls() {
        mListView = (ListView) findViewById(R.id.activity_exchanges_listview);
        etMessage = (EditText) findViewById(R.id.activity_exchanges_message);
        buttonSend = (ImageButton) findViewById(R.id.activity_exchanges_button_send);
        buttonSend.setEnabled(false);
        buttonSend.setBackgroundColor(Color.CYAN);
        loadingIndicator = (ViewGroup) findViewById(R.id.activity_exchanges_loading_indicator);
        noConnection = (ViewGroup) findViewById(R.id.activity_exchanges_no_connection);
        retryButton = (Button) findViewById(R.id.activity_exchanges_retry_button);
        reportButton = (Button) findViewById(R.id.fragment_exchanges_report_button);

        headerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.exchanges_header, null, false);
        mListView.addHeaderView(headerView, null, false);
        mListView.setHeaderDividersEnabled(true);

        TextView tvQuestion = (TextView) headerView.findViewById(R.id.activity_exchanges_question);
        TextView tvAnswers = (TextView) headerView.findViewById(R.id.activity_exchanges_answers);
        TextView tvViews = (TextView) headerView.findViewById(R.id.activity_exchanges_views);
        TextView tvTag = (TextView) headerView.findViewById(R.id.activity_exchanges_tag);
        TextView tvAuthor = (TextView) headerView.findViewById(R.id.activity_exchanges_author);

        tvQuestion.setText(thisTopic.getContent());
        tvAnswers.setText(thisTopic.getAnswers()+"\nAnswers");
        tvViews.setText(thisTopic.getViews()+"\nViews");
        tvTag.setText("#"+thisTopic.getTopicTag());
        tvAuthor.setText(Helper.at(this).checkIfIsMine(thisTopic));

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll();
            }
        });
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMessage.length() != 0) {
                    buttonSend.setEnabled(true);
                    buttonSend.setBackgroundColor(Color.parseColor("#FFFFB635"));
                } else {
                    buttonSend.setEnabled(false);
                    buttonSend.setBackgroundColor(Color.CYAN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.at(getApplicationContext());
                String exchangeText = etMessage.getText().toString();
                if (!TextUtils.isEmpty(exchangeText)){
                    if (exchangeText.length() >= 50){
                        Exchange e = new Exchange();
                        e.setAuthorName(user.getName());
                        e.setAuthorNumber(user.getNumber());
                        e.setTopicId(thisTopic.getTopicId());
                        e.setContent(exchangeText);
                        etMessage.setText("");
                        sendExchange(e);
                    } else {
                        Helper.at(getApplicationContext())
                                .simpleToast("For constructive exchanges, your exchange must contain more than 50 characters!");
                    }
                } else {
                    return;
                }
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Exchange e = (Exchange) parent.getItemAtPosition(position);

                String userNumber = User.at(ExchangesActivity.this).getNumber();
                String userPrivilege = User.at(ExchangesActivity.this).getPrivilegeCode();
                if (e.getAuthorNumber().equalsIgnoreCase(userNumber) ||
                        userPrivilege.equalsIgnoreCase(User.FORUM_ADMIN) ||
                        e.getAuthorNumber().equalsIgnoreCase(thisTopic.getAuthorNumber())){
                    try {
                        deleteDialog(e);
                    } catch (Exception e1){
                        e1.printStackTrace();
                    }
                    return true;
                } else {
                    Helper.at(ExchangesActivity.this).simpleToast("You don't have privilege to delete this exchange!");
                    return false;
                }
            }
        });
    }

    public void displayExchange(Exchange message) {
        mAdapter.add(message);
        mAdapter.notifyDataSetChanged();
        scroll();
    }

    private  void sendExchange(Exchange e) {
        User user = User.at(this);
        String profileImageName = user.getNumber().replace("-","_").trim().replace("/","_")+".png";
        RequestParams params = new RequestParams();
        params.put("content", e.getContent());
        params.put("author_name", e.getAuthorName());
        params.put("author_number", e.getAuthorNumber());
        params.put("author_profile", "profiles/" + profileImageName);
        params.put("topic_id", e.getTopicId());

        Client.post(Client.absoluteUrl("postexchange"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!Helper.at(getApplicationContext()).isOnline()) {
                    Helper.at(getApplicationContext()).simpleToast("Unable to send your exchange! No internet connection.");
                } else {
                    getSupportActionBar().setSubtitle("Sending...");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parsePostExchangeResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Toshde", "Error sending exchange " + responseString);
                getSupportActionBar().setSubtitle(thisTopic.getContent());
                Helper.at(getApplicationContext()).simpleToast("Unable to send your exchange! No internet connection.");
            }
        });
    }

    protected void parsePostExchangeResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                getSupportActionBar().setSubtitle(thisTopic.getContent());
                Helper.at(this).simpleToast("Exchange submitted successfully!");
            } else {
                Helper.at(getApplicationContext()).simpleToast("Unable to send your exchange! No internet connection.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void scroll() {
        mListView.setSelection(mListView.getCount() - 1);
    }

    public void connectAndRespond(){
        Client.post(Client.absoluteUrl("exchanges/" + thisTopic.getTopicId()), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getApplicationContext()).isOnline()) {
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
            }
        });
    }

    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                lastId = response.getString(Response.LAST_ID);
                JSONArray exchanges = response.getJSONArray(Response.DATA);
                for (int i = 0; i < exchanges.length(); i++){
                    JSONObject exchange = exchanges.getJSONObject(i);
                    Exchange e = new Exchange();
                    e.setAuthorName(exchange.getString(Response.ExchangeData.AUTHOR_NAME));
                    e.setAuthorNumber(exchange.getString(Response.ExchangeData.AUTHOR_NUMBER));
                    e.setContent(exchange.getString(Response.ExchangeData.CONTENT));
                    e.setTopicId(exchange.getString(Response.ExchangeData.TOPIC_ID));
                    e.setTime(exchange.getString(Response.ExchangeData.TIME));
                    e.setAuthorProfile(exchange.getString(Response.ExchangeData.AUTHOR_PROFILE));
                    exchangeArrayList.add(e);
                }

                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
                noConnection.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
                loadedExchanges = exchangeArrayList.size();
                loadNewExchanges();
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadNewExchanges(){
        RequestParams params = new RequestParams();
        params.put("topic_id", thisTopic.getTopicId());
        params.put("last_id", lastId);
        Client.post(Client.absoluteUrl("newexchanges"), thisTopic.getTopicId(), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getApplicationContext()).isOnline()) {
                    //Helper.at(getApplicationContext()).simpleToast("New exchange check started");
                } else {
                    //Helper.at(getApplicationContext()).simpleToast("Unable to refresh exchanges!");
                    loadNewExchanges();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseNewExchangesResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Helper.at(getApplicationContext()).simpleToast("Unable to refresh exchanges!");
                loadNewExchanges();
            }
        });
    }

    protected void parseNewExchangesResponse(JSONObject response){
        Log.d("Toshde", "Using Last id as " + lastId);
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                lastId = response.getString(Response.LAST_ID);
                JSONArray exchanges = response.getJSONArray(Response.DATA);
                for (int i = 0; i < exchanges.length(); i++){
                    JSONObject exchange = exchanges.getJSONObject(i);
                    Exchange e = new Exchange();
                    e.setAuthorName(exchange.getString(Response.ExchangeData.AUTHOR_NAME));
                    e.setAuthorNumber(exchange.getString(Response.ExchangeData.AUTHOR_NUMBER));
                    e.setContent(exchange.getString(Response.ExchangeData.CONTENT));
                    e.setTopicId(exchange.getString(Response.ExchangeData.TOPIC_ID));
                    e.setTime(exchange.getString(Response.ExchangeData.TIME));
                    e.setAuthorProfile(exchange.getString(Response.ExchangeData.AUTHOR_PROFILE));
                    e.setExchangeId(exchange.getString(Response.ExchangeData.EXCHANGE_ID));
                    exchangeArrayList.add(e);
                }
                if(loadedExchanges < exchangeArrayList.size()){
                    Boolean playSound =
                            PreferenceManager.getDefaultSharedPreferences(this)
                                    .getBoolean(getString(R.string.pref_exchange_tones), true);
                    if (mediaPlayer == null && playSound){
                        playNotificationSound();
                    }
                    mAdapter.notifyDataSetChanged();
                    scroll();
                    loadedExchanges = exchangeArrayList.size();
                }
                loadNewExchanges();
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playNotificationSound(){
        mediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

    }

    private void deleteDialog(final Exchange exchange){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this exchange!");
        if (TextUtils.equals(exchange.getAuthorNumber(), User.at(this).getNumber())){
            builder.setMessage("You can only delete exchanges you authored.\nPlease" +
                    " confirm you want to delete.\n\n"+exchange.getContent());
        } else if(User.at(ExchangesActivity.this).getPrivilegeCode().equalsIgnoreCase(User.FORUM_ADMIN)) {
            builder.setMessage("You can delete exchanges because you are a forum admin.\nPlease" +
                    " confirm you want to delete.\n\n"
                    +Helper.at(getApplicationContext()).reduceToTwoHundredCharacters(exchange.getContent()));
        } else {
            builder.setMessage("You can only delete exchanges in topics you authored.\nPlease" +
                    " confirm you want to delete.\n\n"
                    +Helper.at(getApplicationContext()).reduceToTwoHundredCharacters(exchange.getContent()));
        }
        builder.setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExchangeSelected(exchange);
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

    private void deleteExchangeSelected(final Exchange exchange){

        final String exchangeToDeleteId = exchange.getExchangeId();
        Client.post(Client.absoluteUrl("deleteexchange/"+exchangeToDeleteId), null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Helper.at(getApplicationContext()).simpleToast("Exchange delete request sent.");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseDeleteExchangeResponse(response, exchange);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Helper.at(getApplicationContext()).simpleToast("Unable to delete exchange, try again later!");
            }
        });
    }

    private void parseDeleteExchangeResponse(JSONObject response, Exchange exchange){

        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                try {
                    exchangeArrayList.remove(exchange);
                } catch (Exception e){
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                Helper.at(getApplicationContext()).simpleToast("Exchange deleted successfully!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Client.cancelMyRequests(thisTopic.getTopicId());
        hasBeenPaused = false;
    }
}
