package ke.co.elmaxdevelopers.eventskenya.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import ke.co.elmaxdevelopers.eventskenya.views.SquaredImageView;

/**
 * Created by Tosh on 12/14/2015.
 */
public class Service implements Parcelable {

    public static final String EVENT_ORGANIZER = "Event organizer";
    public static final String EVENT_ENTERTAINER = "Event entertainer";
    public static final String EVENT_SERVICES = "Event service";
    private String name, about, phone, email;
    private int id, saved;
    private String imageOneUrl, imageTwoUrl, serviceType;
    private byte[] imageOne, imageTwo;

    public Service (){

    }

    protected Service(Parcel in) {
        name = in.readString();
        about = in.readString();
        phone = in.readString();
        email = in.readString();
        id = in.readInt();
        saved = in.readInt();
        imageOneUrl = in.readString();
        imageTwoUrl = in.readString();
        serviceType = in.readString();
        imageOne = in.createByteArray();
        imageTwo = in.createByteArray();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImageOneUrl() {
        return imageOneUrl;
    }

    public void setImageOneUrl(String imageOneUrl) {
        this.imageOneUrl = imageOneUrl;
    }

    public String getImageTwoUrl() {
        return imageTwoUrl;
    }

    public void setImageTwoUrl(String imageTwoUrl) {
        this.imageTwoUrl = imageTwoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public byte[] getImageOne() {
        return imageOne;
    }

    public void setImageOne(byte[] imageOne) {
        this.imageOne = imageOne;
    }

    public byte[] getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(byte[] imageTwo) {
        this.imageTwo = imageTwo;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(about);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(id);
        dest.writeInt(saved);
        dest.writeString(imageOneUrl);
        dest.writeString(imageTwoUrl);
        dest.writeString(serviceType);
        dest.writeByteArray(imageOne);
        dest.writeByteArray(imageTwo);
    }

    public boolean hasImageOne() {
        return (this.getImageOne() != null && this.getImageOne().length > 0);
    }

    public boolean hasImageTwo() {
        return (this.getImageTwo() != null && this.getImageTwo().length > 0);
    }

    public Bitmap decodeImageOne() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.getImageOne());
        return BitmapFactory.decodeStream(bais);
    }

    public Bitmap decodeImageTwo() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.getImageTwo());
        return BitmapFactory.decodeStream(bais);
    }

    public void addImageOne(ImageView imageView) {
        this.setImageOne(getImageBytes(imageView));
    }

    public void addImageTwo(ImageView imageView) {
        this.setImageTwo(getImageBytes(imageView));
    }

    private synchronized byte[] getImageBytes(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
