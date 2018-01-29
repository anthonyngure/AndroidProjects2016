package com.mustmobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.EClassVideo;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class EclassVideoListAdapter extends BaseAdapter {

    private Activity context;
    private LayoutInflater inflater;
    private ArrayList<EClassVideo> eclassVideoItems;

    public EclassVideoListAdapter(Activity context, ArrayList<EClassVideo> eclassVideoItems) {
        this.context = context;
        this.eclassVideoItems = eclassVideoItems;
    }
    @Override
    public int getCount() {
        return eclassVideoItems.size();
    }
    @Override
    public Object getItem(int location) {
        return eclassVideoItems.get(location);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.list_item_eclassvideo, null);

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_eclassvideo_title);
        TextView tvUploadTime = (TextView) convertView.findViewById(R.id.list_item_eclassvideo_upload_time);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.list_item_eclassvideo_author);
        TextView tvViews = (TextView) convertView.findViewById(R.id.list_item_eclassvideo_views);
        final ImageView thumbnail = (ImageView) convertView.findViewById(R.id.list_item_eclassvideo_thumbnail);
        final ProgressBar thumbnailLoader = (ProgressBar) convertView.findViewById(R.id.list_item_eclassvideo_thumbnail_loader);

        final EClassVideo v = eclassVideoItems.get(position);
        tvName.setText(v.getTitle());
        tvAuthor.setText(v.getAuthor());
        tvViews.setText(v.getViews() + " Views");
        tvUploadTime.setText(v.getUploadTime());

        if (!v.getThumbnail().equals("")){

            Picasso.with(context).load(Client.absoluteUrl(v.getThumbnail())).into(thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    thumbnail.setVisibility(View.VISIBLE);
                    thumbnailLoader.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    thumbnail.setVisibility(View.VISIBLE);
                    thumbnail.setImageResource(R.drawable.ic_action_eclass);
                    thumbnailLoader.setVisibility(View.GONE);
                }
            });
        }

        return convertView;
    }
}
