package com.mustmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Topic;
import com.mustmobile.model.User;
import com.mustmobile.util.Helper;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class TopicsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Topic> topicArrayList;
    private LayoutInflater inflater;
    private String userNumber;

    public TopicsListAdapter(Context ctx, ArrayList<Topic> topics){
        this.context = ctx;
        this.topicArrayList = topics;
        userNumber = User.at(ctx).getNumber();
    }

    @Override
    public int getCount() {
        return topicArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_topic, null);
        }

        TextView tvQuestion = (TextView) convertView.findViewById(R.id.list_item_overstack_question);
        TextView tvAnswers = (TextView) convertView.findViewById(R.id.list_item_overstack_answers);
        TextView tvViews = (TextView) convertView.findViewById(R.id.list_item_overstack_views);
        TextView tvTag = (TextView) convertView.findViewById(R.id.list_item_overstack_tag);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.list_item_overstack_author);

        Topic t = topicArrayList.get(position);

        tvQuestion.setText(t.getContent());
        tvAnswers.setText(t.getAnswers()+"\nAnswers");
        tvViews.setText(t.getViews()+"\nViews");
        tvTag.setText("#" + t.getTopicTag());
        tvAuthor.setText(Helper.at(context).checkIfIsMine(t));

        return convertView;
    }
}
