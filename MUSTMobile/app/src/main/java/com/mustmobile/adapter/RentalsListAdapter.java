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
import com.mustmobile.model.Rental;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tosh on 10/11/2015.
 */
public class RentalsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Rental> rentalArrayList;
    private LayoutInflater inflater;

    public RentalsListAdapter(Context ctx, ArrayList<Rental> rentals){
        this.context = ctx;
        this.rentalArrayList = rentals;
    }

    @Override
    public int getCount() {
        return rentalArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return rentalArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_rental, null);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_item_rental_name);
        TextView tvLocation = (TextView) convertView.findViewById(R.id.list_item_rental_location);
        TextView tvContact = (TextView) convertView.findViewById(R.id.list_item_rental_contact);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.list_item_rental_distance);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.list_item_rental_price);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_rental_image);
        final ProgressBar imageLoader = (ProgressBar) convertView.findViewById(R.id.list_item_rental_image_loader);

        Rental r = rentalArrayList.get(position);
        tvName.setText(r.getName());
        tvLocation.setText(r.getLocation());
        tvContact.setText("Unavailable");
        tvDistance.setText(r.getDistance());
        tvPrice.setText("Unavailable");

        if (!r.getUrl().equals("")){

            Picasso.with(context).load(Client.absoluteUrl(r.getUrl())).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                    imageLoader.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_action_accomodation);
                    imageLoader.setVisibility(View.GONE);
                }
            });
        }


        return convertView;
    }
}
