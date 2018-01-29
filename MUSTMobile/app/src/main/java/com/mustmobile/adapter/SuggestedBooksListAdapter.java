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
import com.mustmobile.model.Book;
import com.mustmobile.network.Client;
import com.mustmobile.util.AppStorage;
import com.mustmobile.util.Helper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
public class SuggestedBooksListAdapter extends BaseAdapter {

    private final String TAG = SuggestedBooksListAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Book> books;
    private LayoutInflater inflater, dialogInflater;
    private AlertDialog pDialog;
    private TextView tvProgress;
    private Button  openButton;
    private View dialogView;


    public SuggestedBooksListAdapter(Context context, ArrayList<Book> books){
        this.context = context;
        this.books = books;
    }
    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_book, null);
        }

        TextView bookTitle = (TextView) convertView.findViewById(R.id.list_item_book_title);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_book_cover);
        final ProgressBar coverLoader = (ProgressBar) convertView.findViewById(R.id.list_item_book_cover_loader);
        TextView bookAuthors = (TextView) convertView.findViewById(R.id.list_item_book_authors);
        final Book b = books.get(position);

        bookTitle.setText(b.getBookTitle()+".");
        bookAuthors.setText("Author : " + b.getBookAuthor());
        if (!b.getBookCover().equals("")){

            Picasso.with(context).load(Client.absoluteUrl(b.getBookCover())).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setVisibility(View.VISIBLE);
                    coverLoader.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.pdf);
                    coverLoader.setVisibility(View.GONE);
                }
            });
        }

        final View.OnClickListener readListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProgressDialog();
                Helper.at(context).simpleToast("Opening "+b.getBookTitle()+" for reading");
                //File file = new File(AppStorage.retrieve(AppStorage.BOOKS_STORE, b.getBookId()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.fromFile(file), "application/pdf");
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
                        showDownloadDialog(b);
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        int count;
                        try {
                            URL url = new URL(params[0]);
                            URLConnection connection = url.openConnection();
                            connection.connect();
                            int lengthOfBook = connection.getContentLength();
                            InputStream input = new BufferedInputStream(url.openStream(), 8192);
                            OutputStream output = new FileOutputStream(AppStorage.storeAs(AppStorage.PASTPAPER_STORE, b.getBookId()));
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
                }.execute(Client.absoluteUrl(b.getUrl()));
            }
        };

        imageView.setOnClickListener(downloadListener);

        return convertView;
    }

    private void showDownloadDialog(Book book){

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
                .setTitle(book.getBookTitle())
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

    private void hideDialog(){
        if (pDialog != null){
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

    public void showNetworkErrorDialog(int statusCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    }
                })
                .setNegativeButton("Switch data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}
