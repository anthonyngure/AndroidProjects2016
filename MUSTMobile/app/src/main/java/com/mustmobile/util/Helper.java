package com.mustmobile.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mustmobile.R;
import com.mustmobile.model.Exchange;
import com.mustmobile.model.Topic;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tosh on 10/7/2015.
 */
public class Helper {

    private static Context context;

    private Helper(Context context){
        this.context = context;
    }

    public static Helper at(Context context){
        return new Helper(context);
    }

    public static void simpleToast(String msg){
        /**
         * Catching context null situation
         */
        try {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isOnline() {
        Boolean ret = false;
        /**
         * Try to check if the user is online
         * Catching context null situation
         */
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            ret = ((networkInfo != null) && (networkInfo.isConnected()));
        } catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    public static String formatDate(String dateString) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7));
        int day = Integer.parseInt(dateString.substring(8, 10));
        cal.set(year, month-1, day);
        Date date = cal.getTime();
        return DateFormat.getDateInstance().format(date);
    }

    public static String checkIfIsMine(Exchange exchange){
        if (TextUtils.equals(exchange.getAuthorNumber(), User.at(context).getNumber())){
            return "Me";
        } else {
            return exchange.getAuthorName();
        }
    }

    public static String checkIfIsMine(Topic topic){
        if (TextUtils.equals(topic.getAuthorNumber(), User.at(context).getNumber())){
            return "Me";
        } else {
            return topic.getAuthorName();
        }
    }

    public static void showImagePreview(String imageUrl, String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_image_preview, null, false);
        TextView tvName = (TextView) view.findViewById(R.id.dialog_image_preview_name);
        final ImageView mProfile = (ImageView) view.findViewById(R.id.dialog_image_preview_image);

        tvName.setText(name);
        Picasso.with(context).load(Client.absoluteUrl(imageUrl)).into(mProfile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                mProfile.setImageResource(R.drawable.default_profile);
            }
        });

        builder.setView(view)
                .setCancelable(true)
                .create()
                .show();
    }

    public static void showImagePreview(String tag){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_image_preview, null, false);
        TextView tvName = (TextView) view.findViewById(R.id.dialog_image_preview_name);
        final ImageView mProfile = (ImageView) view.findViewById(R.id.dialog_image_preview_image);
        if (tag.equalsIgnoreCase("1")){
            mProfile.setImageResource(R.drawable.developer);
            tvName.setText(context.getString(R.string.about_developer_info));
        } else if (tag.equalsIgnoreCase("2")){
            mProfile.setImageResource(R.drawable.makau);
            tvName.setText(context.getString(R.string.about_makau_info));
        } else if (tag.equalsIgnoreCase("3")){
            mProfile.setImageResource(R.drawable.peter);
            tvName.setText(context.getString(R.string.about_wamenju_info));
        }
        builder.setView(view)
                .setCancelable(true)
                .create()
                .show();
    }

    public static View getLoadingMoreFooter(){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.footer_loading_more, null, false);
        TextView tvFooterMessage = (TextView) mView.findViewById(R.id.footer_loading_more_message);
        tvFooterMessage.setVisibility(View.VISIBLE);
        return mView;
    }

    public static String reduceToTwoHundredCharacters(String string){
        if (string.length() >= 200){
            return string+".....";
        } else {
            return string;
        }
    }
}
