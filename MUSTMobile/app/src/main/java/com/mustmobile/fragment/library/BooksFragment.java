package com.mustmobile.fragment.library;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
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
import com.loopj.android.http.RequestParams;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.BooksListAdapter;
import com.mustmobile.adapter.PastPapersListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Book;
import com.mustmobile.model.PastPaper;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class BooksFragment extends BaseFragment {

    private final String TAG = BooksFragment.class.getSimpleName();
    private AlertDialog dialog;
    private Button retryButton, reportButton;
    private ListView mListView;
    private BooksListAdapter booksListAdapter;
    private ArrayList<Book> bookArrayList;
    private ViewGroup loadingIndicator, noConnection, noData;
    private TextView tvNoLibraryData;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static BooksFragment newInstance(int sectionNumber) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BooksFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_library_listview);
        loadingIndicator = (ViewGroup) view.findViewById(R.id.fragment_library_loading_indicator);
        retryButton = (Button) view.findViewById(R.id.fragment_library_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_library_report_button);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_library_no_connection);
        noData = (ViewGroup) view.findViewById(R.id.fragment_library_no_data);
        tvNoLibraryData = (TextView) view.findViewById(R.id.fragment_library_no_library_data);
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

        bookArrayList = new ArrayList<>();
        booksListAdapter = new BooksListAdapter(getActivity(), bookArrayList);
        mListView.setAdapter(booksListAdapter);
        mListView.setFooterDividersEnabled(true);
        mListView.addFooterView(Helper.at(getActivity()).getLoadingMoreFooter());
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
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Books");
        Client.post(Client.absoluteUrl("librarybooks"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()) {
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
                JSONArray books = response.getJSONArray(Response.DATA);
                for (int i = 0; i < books.length(); i++){
                    JSONObject book = books.getJSONObject(i);
                    Book b = new Book();
                    b.setBookTitle(book.getString(Response.BookData.TITLE));
                    b.setBookAuthor(book.getString(Response.BookData.AUTHOR));
                    b.setBookCover(book.getString(Response.BookData.COVER));
                    b.setUrl(book.getString(Response.BookData.URL));
                    b.setBookId(book.getString(Response.BookData.ID));
                    bookArrayList.add(b);
                }

                if (bookArrayList.size() < 1){
                    noData.setVisibility(View.VISIBLE);
                    tvNoLibraryData.setText("No books found in the system. Will be added soon.");
                    loadingIndicator.setVisibility(View.GONE);
                } else {
                    booksListAdapter.notifyDataSetChanged();
                    mListView.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    noData.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                }

            } else {
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_library, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.activity_main_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       return super.onOptionsItemSelected(item);
    }

}
