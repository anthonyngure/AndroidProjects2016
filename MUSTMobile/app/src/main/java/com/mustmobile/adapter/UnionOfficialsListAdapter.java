package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.UnionOfficial;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class UnionOfficialsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<UnionOfficial> officialArrayList;
    private LayoutInflater inflater;

    public UnionOfficialsListAdapter(Context ctx, ArrayList<UnionOfficial> officialArrayList){
        this.context = ctx;
        this.officialArrayList = officialArrayList;
    }

    @Override
    public int getCount() {
        return officialArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return officialArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_union_official, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_union_official_name);
        TextView tvPost = (TextView) convertView.findViewById(R.id.list_item_union_official_post);
        TextView tvMessageTitle = (TextView) convertView.findViewById(R.id.list_item_union_official_message_title);
        TextView tvMessage = (TextView) convertView.findViewById(R.id.list_item_union_official_message);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_union_official_profile);

        UnionOfficial o = officialArrayList.get(position);

        tvName.setText(o.getName());
        tvPost.setText(o.getPost());
        tvMessageTitle.setText(o.getMessageTitle());
        tvMessage.setText("\" "+o.getMessage()+" \"");

        if (!o.getProfileUrl().equals("")){

            Picasso.with(context).load(Client.absoluteUrl(o.getProfileUrl())).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    imageView.setImageResource(R.drawable.default_profile);
                }
            });
        }


        return convertView;
    }
}
