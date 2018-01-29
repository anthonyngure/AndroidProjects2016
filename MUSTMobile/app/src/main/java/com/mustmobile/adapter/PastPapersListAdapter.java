package com.mustmobile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mustmobile.R;
import com.mustmobile.model.PastPaper;
import com.mustmobile.network.Client;
import com.mustmobile.util.AppStorage;
import com.mustmobile.util.Helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Tosh on 10/7/2015.
 */
public class PastPapersListAdapter extends BaseAdapter {

    private final String TAG = SuggestedBooksListAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<PastPaper> pastPaperArrayList;
    private LayoutInflater inflater;
    private AlertDialog pDialog;
    private TextView tvProgress;
    private Button  openButton;
    private View dialogView;
    private LayoutInflater dialogInflater;


    public PastPapersListAdapter(Context context, ArrayList<PastPaper> pastPapers){
        this.context = context;
        this.pastPaperArrayList = pastPapers;
    }
    @Override
    public int getCount() {
        return pastPaperArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return pastPaperArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_pastpaper, null);
        }

        TextView tvPastPaperName = (TextView) convertView.findViewById(R.id.list_item_pastpaper_name);
        TextView tvAvailability = (TextView) convertView.findViewById(R.id.list_item_pastpaper_availability);

        final PastPaper p = pastPaperArrayList.get(position);

        tvPastPaperName.setText(p.getName() + ".");

        final View.OnClickListener readListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog();
                Helper.at(context).simpleToast("Opening "+p.getName()+" for reading");
                File file = new File(AppStorage.retrieve(AppStorage.PASTPAPER_STORE , p.getName()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try{
                    context.startActivity(intent);
                } catch (Exception e){
                    Helper.at(context).simpleToast(context.getString(R.string.book_openning_error));
                    e.printStackTrace();
                }
            }
        };

        View.OnClickListener downloadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, String, String>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        showDownloadDialog(p);
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        int count;
                        OutputStream output = null;
                        try {
                            URL url = new URL(Client.absoluteUrl(params[0]).replace(" ","%20"));
                            URLConnection connection = url.openConnection();
                            connection.connect();
                            int lengthOfBook = connection.getContentLength();
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);
                            if (AppStorage.externalIsWritable() && AppStorage.externalIsReadable()){
                                output = new FileOutputStream(AppStorage.storeAs(AppStorage.PASTPAPER_STORE, p.getName()));
                            } else {
                                showStorageErrorDialog();
                            }
                            byte data[] = new byte[1024];
                            long total = 0;
                            while ((count = input.read(data)) != -1){
                                total +=count;
                                publishProgress(""+(int)((total*100)/lengthOfBook));
                                output.write(data, 0, count);
                            }

                            output.flush();
                            output.close();
                            input.close();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        super.onProgressUpdate(values);
                        if (pDialog != null){
                            tvProgress.setText(String.valueOf(values[0])+"% ready.");
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (pDialog != null){
                            openButton.setEnabled(true);
                            openButton.setOnClickListener(readListener);
                        }
                    }
                }.execute(p.getUrl());
            }
        };

        if (AppStorage.externalIsReadable()){
            File mFile = new File(AppStorage.retrieve(AppStorage.PASTPAPER_STORE, p.getName()));
            if (mFile.exists()){
                tvPastPaperName.setOnClickListener(readListener);
                tvAvailability.setVisibility(View.VISIBLE);
            } else {
                tvPastPaperName.setOnClickListener(downloadListener);
                tvAvailability.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void showDownloadDialog(PastPaper book){

        initProgressDialog();

        if (dialogInflater == null){
            dialogInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (dialogView == null){
            dialogView = dialogInflater.inflate(R.layout.book_download_progress, null);
            tvProgress = (TextView) dialogView.findViewById(R.id.book_download_progress_progress);
            openButton = (Button) dialogView.findViewById(R.id.book_download_progress_button_open);
            openButton.setEnabled(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle(book.getName())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initProgressDialog();
                    }
                })
                .setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    private void initProgressDialog(){
        hideDialog();
        pDialog = null;
        dialogView  = null;
        dialogInflater = null;
        tvProgress = null;
        openButton = null;
    }

    private void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    private String getPercentageReady(long bytesDownloaded, long totalSize){
        float percentage;
        float mDownloaded = (float) bytesDownloaded;
        float mTotal = (float) totalSize;
        percentage = ((mDownloaded/mTotal)*100);
        return Math.round(percentage)+"% ready.";
    }

    private void showStorageErrorDialog() {
        initProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.storage_error))
                .setPositiveButton(context.getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }
}
