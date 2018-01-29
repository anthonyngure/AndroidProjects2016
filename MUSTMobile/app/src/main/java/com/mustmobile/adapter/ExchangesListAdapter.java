package com.mustmobile.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.Exchange;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.util.Helper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ExchangesListAdapter extends BaseAdapter {

    private final ArrayList<Exchange> exchangeArrayList;
    private Context context;
    private LayoutInflater inflater;
    private String userNumber;

    public ExchangesListAdapter(Context context, ArrayList<Exchange> exchanges) {
        this.context = context;
        this.exchangeArrayList = exchanges;
        this.userNumber = User.at(context).getNumber();
    }

    @Override
    public int getCount() {
        if (exchangeArrayList != null) {
            return exchangeArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Exchange getItem(int position) {
        if (exchangeArrayList != null) {
            return exchangeArrayList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_exchange, null);
        }

        final ImageView mProfile = (ImageView) convertView.findViewById(R.id.list_item_exchange_profile);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.list_item_exchange_author);
        TextView tvContent = (TextView) convertView.findViewById(R.id.list_item_exchange_content);
        TextView tvTime = (TextView) convertView.findViewById(R.id.list_item_exchange_time);

        final Exchange e = exchangeArrayList.get(position);

        tvAuthor.setText(Helper.at(context).checkIfIsMine(e));

        tvContent.setText(e.getContent());
        tvTime.setText(e.getTime());

        if (!TextUtils.isEmpty(e.getAuthorProfile())){
            Picasso.with(context).load(Client.absoluteUrl(e.getAuthorProfile())).into(mProfile, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    mProfile.setImageResource(R.drawable.default_profile);
                }
            });
        }

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.at(context).showImagePreview(e.getAuthorProfile(), e.getAuthorName());
            }
        });


        return convertView;
    }

    public void add(Exchange message) {
        exchangeArrayList.add(message);
    }

    public void add(List<Exchange> messages) {
        exchangeArrayList.addAll(messages);
    }
}
