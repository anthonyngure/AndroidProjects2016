package com.mustmobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.mustmobile.R;
import com.mustmobile.model.Gallery;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tosh on 11/17/2015.
 */
public class GalleryListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Gallery> galleryArrayList;
    private LayoutInflater inflater;

    public GalleryListAdapter(Context ctx, ArrayList<Gallery> galleries){
        this.context = ctx;
        this.galleryArrayList = galleries;
    }

    @Override
    public int getCount() {
        return galleryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return galleryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView mImageView;
        if (convertView == null){
            mImageView = new ImageView(context);
            mImageView.setLayoutParams(
                    new GridView.LayoutParams(230, 230));
            mImageView.setBackgroundColor(Color.DKGRAY);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setPadding(5, 5, 5, 5);
        } else {
            mImageView = (ImageView) convertView;
        }

        Gallery g = galleryArrayList.get(position);
        Picasso.with(context).load(Client.absoluteUrl(g.getUrl())).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                mImageView.setImageResource(R.drawable.default_profile);
            }
        });
        return mImageView;
    }
}
