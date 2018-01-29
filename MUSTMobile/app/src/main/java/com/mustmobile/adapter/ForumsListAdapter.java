package com.mustmobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Forum;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class ForumsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Forum> forumArrayList;
    private LayoutInflater inflater;

    public ForumsListAdapter(Context ctx, ArrayList<Forum> forums){
        this.context = ctx;
        this.forumArrayList = forums;
    }

    @Override
    public int getCount() {
        return forumArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return forumArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item_forum, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_forum_name);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.list_item_forum_description);
        final ImageView mImageView = (ImageView) convertView.findViewById(R.id.list_item_forum_image);

        Forum s = forumArrayList.get(position);

        tvName.setText(s.getName());
        tvDescription.setText(s.getDescription());

        if (!s.getIconUrl().isEmpty()){
            Picasso.with(context).load(Client.absoluteUrl(s.getIconUrl())).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    mImageView.setImageResource(R.drawable.default_profile);
                }
            });
        }


        return convertView;
    }
}
