package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Download;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class DownloadsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Download> downloadArrayList;
    private LayoutInflater inflater;

    public DownloadsListAdapter(Context ctx, ArrayList<Download> downloads){
        this.context = ctx;
        this.downloadArrayList = downloads;
    }

    @Override
    public int getCount() {
        return downloadArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_download, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_download_name);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.list_item_download_description);
        TextView tvTimeUploaded = (TextView) convertView.findViewById(R.id.list_item_download_time_uploaded);
        TextView tvTimesDownloaded = (TextView) convertView.findViewById(R.id.list_item_download_times_downloaded);

        Download d = downloadArrayList.get(position);

        tvName.setText(d.getName());
        tvDescription.setText(d.getDescription());
        tvTimeUploaded.setText(d.getTimeUploaded());
        tvTimesDownloaded.setText(d.getTimesDownloaded()+" downloads.");

        return convertView;
    }
}
