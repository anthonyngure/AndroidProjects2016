package ke.co.elmaxdevelopers.eventskenya.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.model.Event;

/**
 * Created by Tosh on 1/30/2016.
 */
public class StorageUtils {

    public static String saveAsPostedEventImage(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "EventsKenya/My Events");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
        return uriSting;
    }
}
