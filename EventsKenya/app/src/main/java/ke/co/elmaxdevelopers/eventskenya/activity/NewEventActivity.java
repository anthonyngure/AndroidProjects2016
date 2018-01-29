package ke.co.elmaxdevelopers.eventskenya.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import org.joda.time.LocalDate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.fragment.picker.DatePickerFragment;
import ke.co.elmaxdevelopers.eventskenya.fragment.picker.TimePickerFragment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.ImageLoadingUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;
import ke.co.elmaxdevelopers.eventskenya.utils.StorageUtils;

public class NewEventActivity extends AppCompatActivity {

    private EditText etName, etVenue, etDescription, etParkingInfo, etSecurityInfo, etAdvancePrice,
            etRegularPrice, etVipPrice, etPromoter, etOrganizer, etTicketsLink;
    private TextView tvStartTime, tvStartDate, tvEndTime, tvEndDate, tvCounty, tvCategory;
    private CharSequence[] categories, counties;
    private ImageView ivImageView;
    private TextInputLayout nameInputLayout, venueInputLayout, descriptionInputLayout,
            ticketsLinkInputLayout;
    private ImageLoadingUtils utils;
    private String encodedImageString,encodedThumbnailString, imagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        categories = getResources().getStringArray(R.array.post_categories);
        counties = getResources().getStringArray(R.array.post_counties);
        utils = new ImageLoadingUtils(this);
        initControls();
    }

    private void initControls() {
        nameInputLayout = (TextInputLayout) findViewById(R.id.name_inputlayout);
        etName = (EditText) findViewById(R.id.set_event_name);
        etName.addTextChangedListener(Helper.createTextWatcher(50, nameInputLayout,
                etName, getHintString(R.string.name_hint)));

        venueInputLayout = (TextInputLayout) findViewById(R.id.venue_inputlayout);
        etVenue = (EditText) findViewById(R.id.set_event_venue);
        etVenue.addTextChangedListener(Helper.createTextWatcher(50, venueInputLayout,
                etVenue, getHintString(R.string.venue_hint)));

        descriptionInputLayout = (TextInputLayout) findViewById(R.id.description_inputlayout);
        etDescription = (EditText) findViewById(R.id.event_description);
        etDescription.addTextChangedListener(Helper.createTextWatcher(320, descriptionInputLayout,
                etDescription, getHintString(R.string.description_hint)));

        Button startTimeButton = (Button) findViewById(R.id.button_start_time);
        Button startDateButton = (Button) findViewById(R.id.button_start_date);
        Button endTimeButton = (Button) findViewById(R.id.button_end_time);
        Button endDateButton = (Button) findViewById(R.id.button_end_date);

        Button countyButton = (Button) findViewById(R.id.button_county);
        tvCounty = (TextView) findViewById(R.id.set_county);
        tvCounty.setText(counties[0]);
        tvCounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountyDialog();
            }
        });

        Button categoryButton = (Button) findViewById(R.id.button_category);
        tvCategory = (TextView) findViewById(R.id.set_category);
        tvCategory.setText(categories[0]);
        tvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategoryDialog();
            }
        });

        Calendar calendar = Calendar.getInstance();
        String currentTime = DateUtils.formatTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));



        tvStartTime = (TextView) findViewById(R.id.set_start_time);
        tvStartTime.setText(currentTime);
        tvStartTime.setOnClickListener(showTimeDialogListener(tvStartTime));

        tvStartDate = (TextView) findViewById(R.id.set_start_date);
        tvStartDate.setText(DateUtils.formatDateForDisplay(new LocalDate().toString()));
        tvStartDate.setOnClickListener(showDateDialogListener(tvStartDate));

        tvEndTime = (TextView) findViewById(R.id.set_end_time);
        tvEndTime.setText(currentTime);
        tvEndTime.setOnClickListener(showTimeDialogListener(tvEndTime));

        tvEndDate = (TextView) findViewById(R.id.set_end_date);
        tvEndDate.setText(DateUtils.formatDateForDisplay(new LocalDate().toString()));
        tvEndDate.setOnClickListener(showDateDialogListener(tvEndDate));

        etAdvancePrice = (EditText) findViewById(R.id.advance_price);
        etRegularPrice = (EditText) findViewById(R.id.regular_price);
        etVipPrice = (EditText) findViewById(R.id.vip_price);

        startTimeButton.setOnClickListener(showTimeDialogListener(tvStartTime));
        startDateButton.setOnClickListener(showDateDialogListener(tvStartDate));
        endTimeButton.setOnClickListener(showTimeDialogListener(tvEndTime));
        endDateButton.setOnClickListener(showDateDialogListener(tvEndDate));

        countyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCountyDialog();
            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategoryDialog();
            }
        });

        ivImageView = (ImageView) findViewById(R.id.event_image);
        ivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(NewEventActivity.this);
            }
        });


        etPromoter = (EditText) findViewById(R.id.event_promoter);
        etPromoter.setText(PrefUtils.getInstance(this).getSetUsername());

        etOrganizer = (EditText) findViewById(R.id.event_organizer);
        etOrganizer.setText(PrefUtils.getInstance(this).getSetUsername());

        TextInputLayout parkingInfoInputLayout = (TextInputLayout) findViewById(R.id.parking_info_inputlayout);
        etParkingInfo = (EditText) findViewById(R.id.parking_info);
        etParkingInfo.addTextChangedListener(Helper.createTextWatcher(100, parkingInfoInputLayout,
                etParkingInfo, getHintString(R.string.hint_parking_info)));

        TextInputLayout securityInfoInputLayout = (TextInputLayout) findViewById(R.id.security_info_inputlayout);
        etSecurityInfo = (EditText) findViewById(R.id.security_info);
        etSecurityInfo.addTextChangedListener(Helper.createTextWatcher(100, securityInfoInputLayout,
                etSecurityInfo, getHintString(R.string.hint_security_info)));

        ticketsLinkInputLayout = (TextInputLayout) findViewById(R.id.tickets_link_inputlayout);
        etTicketsLink = (EditText) findViewById(R.id.tickets_link);
        etTicketsLink.addTextChangedListener(Helper.createTextWatcher(100, ticketsLinkInputLayout,
                etTicketsLink, getHintString(R.string.hint_tickets_link)));

        CheckBox freeCheckBox = (CheckBox) findViewById(R.id.free_checkbox);
        freeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etAdvancePrice.setText("0");
                    etAdvancePrice.setEnabled(false);
                    etRegularPrice.setText("0");
                    etRegularPrice.setEnabled(false);
                    etVipPrice.setText("0");
                    etVipPrice.setEnabled(false);
                    etTicketsLink.setText("");
                    etTicketsLink.setEnabled(false);
                } else {
                    etAdvancePrice.setEnabled(true);
                    etAdvancePrice.setText("");
                    etRegularPrice.setEnabled(true);
                    etRegularPrice.setText("");
                    etVipPrice.setEnabled(true);
                    etVipPrice.setText("");
                    etTicketsLink.setEnabled(true);
                }
            }
        });

    }

    private String getHintString(int id){
        return getString(id);
    }

    private View.OnClickListener showTimeDialogListener(final TextView textView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(textView);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        };
    }

    private View.OnClickListener showDateDialogListener(final TextView textView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(textView);
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        };
    }

    private void selectCountyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_county))
                .setItems(counties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvCounty.setText(String.valueOf(counties[which]));
                    }
                })
                .create()
                .show();

    }

    private void selectCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_choose_category))
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvCategory.setText(String.valueOf(categories[which]));
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        try {
            if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                //verifyImageSize(result.getData());
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void verifyImageSize(Uri imageUri){
        String filePath = getRealPathFromURI(imageUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //Log.d("Toshde", "Bytes count " + options.inBitmap.getByteCount());
        if (options.outHeight > 1024 || options.outWidth > 1024){
            showWrongImageSizeDialog(options.outHeight, options.outWidth);
        } else {
            beginCrop(imageUri);
        }
    }

    private void showWrongImageSizeDialog(int height, int width){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opps, Image Error!")
                .setMessage("Image poster should have a resolution of 1024P or less" +
                        "\nYour poster is "+height+"px by "+width+"px.")
                .create()
                .show();
    }

    private String getRealPathFromURI(Uri contentURI) {
        Uri contentUri = contentURI;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            //resultView.setImageURI(Crop.getOutput(result));
            new ImageCompressionAsyncTask().execute(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }


    class ImageCompressionAsyncTask extends AsyncTask<Uri, Void, String>{

        public ImageCompressionAsyncTask(){

         }

        @Override
        protected String doInBackground(Uri... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(Uri imageUri) {

            String filePath = getRealPathFromURI(imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 480.0f;
            float maxWidth = 480.0f;

            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16*1024];

            try{
                bmp = BitmapFactory.decodeFile(filePath,options);
            }
            catch(OutOfMemoryError exception){
                exception.printStackTrace();

            }
            try{
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            }
            catch(OutOfMemoryError exception){
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float)options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;
            String filename = StorageUtils.saveAsPostedEventImage();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedImageString = Base64.encodeToString(byte_arr, 0);

                ByteArrayOutputStream stream_thumb = new ByteArrayOutputStream();
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(scaledBitmap, 400, 400);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 1, stream_thumb);
                byte[] byte_arr_thumb = stream_thumb.toByteArray();
                encodedThumbnailString = Base64.encodeToString(byte_arr_thumb, 0);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            imagePath = result;
            utils = new ImageLoadingUtils(NewEventActivity.this);
            ivImageView.setImageBitmap(utils.decodeBitmapFromPath(result));
        }

    }

    private boolean etIsEmpty(EditText et, TextInputLayout inputLayout) {
        if (et.getText().toString().isEmpty()){
            inputLayout.setError(getString(R.string.must_fill_error));
            return true;
        } else {
            return false;
        }
    }

    private int getIntegerValue(EditText editText){
        int value;
        try {
            value = Integer.parseInt(editText.getText().toString());
        } catch (Exception e){
            e.printStackTrace();
            value = 0;
        }
        return value;
    }

    private String getAllowedEmptyValue(EditText editText){
        if (editText.getText().toString().isEmpty()){
            return "Not provided";
        } else {
            return editText.getText().toString();
        }
    }

    private String createImageUrl(){
        return etName.getText().toString().replace(" ","_").trim()+"_"+System.currentTimeMillis()+".jpg";
    }

    public void submitNewEvent(View view){
        if (etIsEmpty(etName, nameInputLayout) || etIsEmpty(etVenue, venueInputLayout)
                || etIsEmpty(etDescription, descriptionInputLayout)){
            Helper.toast(this, getString(R.string.empty_fields));
        } else {
            Event event = new Event();
            event.setName(etName.getText().toString());
            event.setVenue(etVenue.getText().toString());
            event.setAdvance(getIntegerValue(etAdvancePrice));
            event.setRegular(getIntegerValue(etRegularPrice));
            event.setVip(getIntegerValue(etVipPrice));
            event.setDescription(etDescription.getText().toString());
            event.setCounty(tvCounty.getText().toString());
            event.setCategory(tvCategory.getText().toString());
            event.setTime(tvStartTime.getText().toString() + " to " + tvEndTime.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateStart = null;
            Date dateEnd = null;
            try {
                dateStart = DateFormat.getDateInstance().parse(tvStartDate.getText().toString());
                dateEnd = DateFormat.getDateInstance().parse(tvEndDate.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            event.setStringStartDate(sdf.format(dateStart));
            event.setStringEndDate(sdf.format(dateEnd));
            event.setPromoter(getAllowedEmptyValue(etPromoter));
            event.setOrganizer(getAllowedEmptyValue(etOrganizer));
            event.setParkingInfo(getAllowedEmptyValue(etParkingInfo));
            event.setSecurityInfo(getAllowedEmptyValue(etSecurityInfo));
            event.setImageUrl(createImageUrl());
            event.setTicketsLink(getAllowedEmptyValue(etTicketsLink));
            event.setHasLoadedDetails(1);
            verifyTicketsLink(event
            );
        }
    }

    private void verifyTicketsLink(Event e){
        if (e.getTicketsLink().equalsIgnoreCase("Not provided")){
            verifyImagePath(e);
        } else {
            if (Patterns.WEB_URL.matcher(e.getTicketsLink()).matches()){
                verifyImagePath(e);
            } else {
                Helper.toast(this, "Invalid tickets link!");
                ticketsLinkInputLayout.setError("Invalid tickets link!");
            }
        }
    }

    private void verifyImagePath(Event event){
        if (imagePath != null && !imagePath.isEmpty()){
            preview(event);
        } else {
            Helper.toast(this, "You must select an image for your event!");
        }
    }

    private void preview(Event event){
        Intent intent = new Intent(this, NewEventPreviewActivity.class);
        ArrayList<Event> parcelableEvent = new ArrayList<>(1);
        parcelableEvent.add(event);
        intent.putParcelableArrayListExtra(NewEventPreviewActivity.EXTRA_EVENT, parcelableEvent);
        intent.putExtra(NewEventPreviewActivity.EXTRA_IMAGE_PATH, imagePath);
        intent.putExtra(NewEventPreviewActivity.EXTRA_ENCODED_IMAGE, encodedImageString);
        intent.putExtra(NewEventPreviewActivity.EXTRA_ENCODED_THUMBNAIL, encodedThumbnailString);
        startActivity(intent);
    }

}
