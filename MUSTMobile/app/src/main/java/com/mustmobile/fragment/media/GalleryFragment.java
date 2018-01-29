package com.mustmobile.fragment.media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.GalleryViewActivity;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.adapter.GalleryListAdapter;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.model.Gallery;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GalleryFragment extends BaseFragment {

    private GridView mGridView;
    private GalleryListAdapter mAdapter;
    private ArrayList<Gallery> galleryArrayList;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static GalleryFragment newInstance(int sectionNumber) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mGridView = (GridView) view.findViewById(R.id.fragment_gallery_gridview);

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

        galleryArrayList = new ArrayList<>();
        mAdapter = new GalleryListAdapter(getActivity(), galleryArrayList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Gallery gallery = galleryArrayList.get(position);
                Intent intent = new Intent(getActivity(), GalleryViewActivity.class);
                intent.putExtra("description", gallery.getDescription());
                intent.putExtra("url", gallery.getUrl());
                startActivity(intent);
            }
        });
        connectAndRespond();
    }

    @Override
    protected void connectAndRespond(){

        Client.post(Client.absoluteUrl("gallery"), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getActivity()).isOnline()){

                } else {

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
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response) {
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                JSONArray galleries = response.getJSONArray(Response.DATA);
                for (int i = 0; i < galleries.length(); i++){
                    JSONObject gallery = galleries.getJSONObject(i);
                    Gallery g = new Gallery();
                    g.setUrl(gallery.getString(Response.GalleryData.URL));
                    g.setDescription(gallery.getString(Response.GalleryData.DESCRIPTION));
                    galleryArrayList.add(g);
                }
                mAdapter.notifyDataSetChanged();
            } else if (response.getString(Response.ERROR).equalsIgnoreCase("0")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
