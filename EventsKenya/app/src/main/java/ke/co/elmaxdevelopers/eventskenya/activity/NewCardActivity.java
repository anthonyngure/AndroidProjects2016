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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.model.Service;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.ImageLoadingUtils;
import ke.co.elmaxdevelopers.eventskenya.views.SquaredImageView;

public class NewCardActivity extends AppCompatActivity {

    private static final int IMAGE_ONE_PICK = 100;
    private static final int IMAGE_TWO_PICK = 200;
    private EditText etName, etPhone, etEmail, etAbout;
    private String imageOnePath = "", imageTwoPath = "";
    private String imageOneEncodedString = "", imageTwoEncodedString = "";
    private SquaredImageView imageOne, imageTwo;
    private TextView tvServiceType;
    private ImageLoadingUtils utils;
    private TextInputLayout aboutIL;
    private TextInputLayout nameInputLayout;
    private TextInputLayout phoneIL;
    private TextInputLayout emailIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card);

        utils = new ImageLoadingUtils(this);

        nameInputLayout = (TextInputLayout) findViewById(R.id.name_inputlayout);
        etName = (EditText) findViewById(R.id.set_service_name);
        etName.addTextChangedListener(Helper.createTextWatcher(80, nameInputLayout, etName,
                getString(R.string.hint_service_name)));

        phoneIL = (TextInputLayout) findViewById(R.id.phone_number_inputlayout);
        etPhone = (EditText) findViewById(R.id.card_phone_number);
        etPhone.addTextChangedListener(Helper.createTextWatcher(10, phoneIL, etPhone,
                getString(R.string.hint_phone_number)));

        emailIL = (TextInputLayout) findViewById(R.id.email_inputlayout);
        etEmail = (EditText) findViewById(R.id.card_email);
        etEmail.addTextChangedListener(Helper.createTextWatcher(100, emailIL, etEmail,
                getString(R.string.hint_email)));

        aboutIL = (TextInputLayout) findViewById(R.id.about_inputlayout);
        etAbout = (EditText) findViewById(R.id.about_service);
        etAbout.addTextChangedListener(Helper.createTextWatcher(320, aboutIL, etAbout,
                getString(R.string.hint_about)));

        tvServiceType = (TextView) findViewById(R.id.set_service_type);
        tvServiceType.setText(Service.EVENT_ORGANIZER);

        imageOne = (SquaredImageView) findViewById(R.id.service_image_one);
        imageTwo = (SquaredImageView) findViewById(R.id.service_image_two);

    }

    public void selectServiceType(View view){
        final CharSequence[] serviceTypes = {Service.EVENT_ORGANIZER, Service.EVENT_ENTERTAINER, Service.EVENT_SERVICES};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Service type")
                .setCancelable(true)
                .setItems(serviceTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvServiceType.setText(serviceTypes[which]);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        try {
            if (requestCode == IMAGE_ONE_PICK && resultCode == RESULT_OK) {
                processImage(result.getData(), 1);
            } else if (requestCode == IMAGE_TWO_PICK && resultCode == RESULT_OK) {
                processImage(result.getData(), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void processImage(Uri path, final int image){

        new  AsyncTask<Uri, Void, String>() {

            String encodedImageString = "";

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
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
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
                String filename = getFilename();
                try {
                    out = new FileOutputStream(filename);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] byte_arr = stream.toByteArray();
                    encodedImageString = Base64.encodeToString(byte_arr, 0);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return filename;

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

            public String getFilename() {
                File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
                return uriSting;

            }



            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                switch (image){
                    case 1:
                        setImageOnePath(result);
                        setImageOneEncodedString(encodedImageString);
                        imageOne.setImageBitmap(utils.decodeBitmapFromPath(result));
                        break;
                    case 2:
                        setImageTwoPath(result);
                        setImageTwoEncodedString(encodedImageString);
                        imageTwo.setImageBitmap(utils.decodeBitmapFromPath(result));
                        break;
                }
            }

        }.execute(path);
    }

    private boolean etIsEmpty(EditText et, TextInputLayout inputLayout) {
        if (et.getText().toString().isEmpty()){
            inputLayout.setError(getString(R.string.must_fill_error));
            return true;
        } else {
            return false;
        }
    }

    public void submitNewCard(View view){
        if (etIsEmpty(etName, nameInputLayout) || etIsEmpty(etPhone, phoneIL)
                || etIsEmpty(etEmail, emailIL) || etIsEmpty(etAbout, aboutIL)){
            Helper.toast(this, getString(R.string.empty_fields));
        } else {
            Service s = new Service();
            s.setName(etName.getText().toString());
            s.setPhone(etPhone.getText().toString());
            s.setEmail(etEmail.getText().toString());
            s.setAbout(etAbout.getText().toString());
            s.setImageOneUrl(imageOneEncodedString);
            s.setImageTwoUrl(imageTwoEncodedString);
            s.setServiceType(tvServiceType.getText().toString());
            verifyEmail(s);
        }
    }

    private void verifyEmail(Service s){
        if (Patterns.EMAIL_ADDRESS.matcher(s.getEmail()).matches()){
            verifyImagePath(s);
        } else {
            Helper.toast(this, "Invalid email address!");
            emailIL.setError("Invalid email address!");
        }
    }

    private void verifyImagePath(Service s){
        if (s.getImageOneUrl().isEmpty() || s.getImageTwoUrl().isEmpty()){
            Helper.toast(this, "You must select two images for your card!");
        } else {
            preview(s);
        }
    }

    private void preview(Service service){
        Intent intent = new Intent(this, NewCardPreviewActivity.class);
        ArrayList<Service> parcelableServices = new ArrayList<>(1);
        parcelableServices.add(service);
        intent.putParcelableArrayListExtra(NewCardPreviewActivity.EXTRA_SERVICE, parcelableServices);
        intent.putExtra(NewCardPreviewActivity.EXTRA_IMAGE_ONE_PATH, imageOnePath);
        intent.putExtra(NewCardPreviewActivity.EXTRA_IMAGE_TWO_PATH, imageTwoPath);
        startActivity(intent);
    }


    public void selectServiceImageOne(View view){
        Crop.pickImage(this, IMAGE_ONE_PICK);
    }

    public void selectServiceImageTwo(View view){
        Crop.pickImage(this, IMAGE_TWO_PICK);
    }

    public void setImageOnePath(String imageOnePath) {
        this.imageOnePath = imageOnePath;
    }

    public void setImageTwoPath(String imageTwoPath) {
        this.imageTwoPath = imageTwoPath;
    }

    public void setImageOneEncodedString(String imageOneEncodedString) {
        this.imageOneEncodedString = imageOneEncodedString;
    }

    public void setImageTwoEncodedString(String imageTwoEncodedString) {
        this.imageTwoEncodedString = imageTwoEncodedString;
    }
}
