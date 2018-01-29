package com.mustmobile.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.mustmobile.R;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;

import org.json.JSONObject;

/**
 * Created by Tosh on 10/11/2015.
 */
public abstract class BaseFragment extends Fragment {

    protected User user;
    private AlertDialog dialog;

    public AlertDialog getDialog() {
        return this.dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    protected abstract void connectAndRespond();
    protected abstract void parseConnectionResponse(JSONObject response);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        user = User.at(getActivity());
    }

    /*public void showNetworkErrorDialog(int statusCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (statusCode){
            case Client.Error.UN_AUTHORIZED_ACCESS:
                builder.setMessage("Possible causes : \n" +
                        "1. Unauthorized access.\n" +
                        "2. Device not connected to Internet.\n" +
                        " HTTP Status code : "+statusCode);
                break;
            case Client.Error.RESOURCE_NOT_FOUND:
                builder.setMessage("Possible causes : \n" +
                        "1. Requested resource not found.\n" +
                        "2. Device not connected to Internet.\n" +
                        " HTTP Status code : "+statusCode);
                break;
            case Client.Error.SERVER_ERROR:
                builder.setMessage("Possible causes : \n" +
                        "1. Something went wrong at server end." +
                        "2. Device not connected to the Internet.\n" +
                        " HTTP Status code : "+statusCode);
                break;
            default:
                builder.setMessage("Possible causes : \n" +
                        "1. Device not connected to Internet.\n" +
                        "2. Web App is not deployed in App server.\n" +
                        "3. App server is not running.\n" +
                        " HTTP Status code : "+statusCode);
                break;
        }
        builder.setTitle("Connection Error!!!!")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //loadBookSuggestions();
                        connectAndRespond();
                    }
                })
                .setNegativeButton("Switch data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
*/
    protected void showNoNetworkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.no_network_connection))
                .setPositiveButton(getString(R.string.button_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectAndRespond();
                    }
                })
                .setNegativeButton(getString(R.string.switch_data), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
}
