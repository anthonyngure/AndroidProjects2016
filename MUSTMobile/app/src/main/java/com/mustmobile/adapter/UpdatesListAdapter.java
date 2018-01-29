package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Update;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class UpdatesListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Update> updateArrayList;
    private LayoutInflater inflater;

    public UpdatesListAdapter(Context ctx, ArrayList<Update> updates){
        this.context = ctx;
        this.updateArrayList = updates;
    }

    @Override
    public int getCount() {
        return updateArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return updateArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_update, null);
        }

        TextView tvTitle = (TextView) convertView.findViewById(R.id.list_item_update_title);
        TextView tvContent = (TextView) convertView.findViewById(R.id.list_item_update_content);
        TextView tvTimesRead = (TextView) convertView.findViewById(R.id.list_item_update_views);
        TextView tvTimeCreated = (TextView) convertView.findViewById(R.id.list_item_update_time_created);

        Update u = updateArrayList.get(position);

        tvTitle.setText(u.getTitle());
        if (u.getContent().length() > 140){
            tvContent.setText(u.getContent().substring(0, 140)+"......");
        } else {
            tvContent.setText(u.getContent());
        }

        tvTimesRead.setText(u.getViews()+" views.");
        tvTimeCreated.setText(u.getTimeCreated());

        return convertView;
    }
}
