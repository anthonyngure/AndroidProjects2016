package ke.co.elmaxdevelopers.eventskenya.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class Event implements Parcelable {
	
	private String name, venue, time, date;
	private String description;
	private String ticketsLink;
	private long id;
    private long startDate, endDate;
    private String stringStartDate, stringEndDate;
	private String imageUrl;
	private int noGoing, noInterested;
	private int statusGoing, statusInterested;
	private ArrayList<Comment> comments;
	private int newComments, totalComments, hasLoadedDetails;
	private int advance, regular, vip;
    private String county, category;
    private String promoter, organizer, parkingInfo, securityInfo;
    private byte[] thumbnail, image;
	
	public Event() {
	}

    protected Event(Parcel in) {
        name = in.readString();
        venue = in.readString();
        time = in.readString();
        date = in.readString();
        description = in.readString();
        ticketsLink = in.readString();
        id = in.readLong();
        startDate = in.readLong();
        endDate = in.readLong();
        stringStartDate = in.readString();
        stringEndDate = in.readString();
        imageUrl = in.readString();
        noGoing = in.readInt();
        noInterested = in.readInt();
        statusGoing = in.readInt();
        statusInterested = in.readInt();
        comments = in.createTypedArrayList(Comment.CREATOR);
        newComments = in.readInt();
        totalComments = in.readInt();
        hasLoadedDetails = in.readInt();
        advance = in.readInt();
        regular = in.readInt();
        vip = in.readInt();
        county = in.readString();
        category = in.readString();
        promoter = in.readString();
        organizer = in.readString();
        parkingInfo = in.readString();
        securityInfo = in.readString();
        thumbnail = in.createByteArray();
        image = in.createByteArray();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNoGoing() {
		return noGoing;
	}

	public void setNoGoing(int noGoing) {
		this.noGoing = noGoing;
	}

	public int getNoInterested() {
		return noInterested;
	}

	public void setNoInterested(int noInterested) {
		this.noInterested = noInterested;
	}

	public int getStatusGoing() {
		return statusGoing;
	}

	public void setStatusGoing(int status) {
		this.statusGoing = status;
	}

	public int getStatusInterested() {
		return statusInterested;
	}

	public void setStatusInterested(int statusInterested) {
		this.statusInterested = statusInterested;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

	public String getTicketsLink() {
		return ticketsLink;
	}

	public void setTicketsLink(String ticketsLink) {
		this.ticketsLink = ticketsLink;
	}

	public int getNewComments() {
		return newComments;
	}

	public void setNewComments(int newComments) {
		this.newComments = newComments;
	}

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

	public void copyChangedEvent(Event event) {
        name = event.getName();
        venue = event.getVenue();
        time = event.getTime();
        date = event.getDate();
        description = event.getDescription();
        ticketsLink = event.ticketsLink;
        id = event.getId();
        startDate = event.getStartDate();
        endDate = event.getEndDate();
        stringStartDate = event.stringStartDate;
        stringEndDate = event.stringEndDate;
        imageUrl = event.getImageUrl();
        noGoing = event.getNoGoing();
        noInterested = event.getNoInterested();
        statusGoing = event.getStatusGoing();
        statusInterested = event.getStatusInterested();
        comments = event.getComments();
        newComments = event.getNewComments();
        totalComments = event.getTotalComments();
        hasLoadedDetails = event.getHasLoadedDetails();
        advance = event.getAdvance();
        regular = event.getRegular();
        vip = event.getVip();
        county = event.getCounty();
        category = event.getCategory();
        promoter = event.getPromoter();
        organizer = event.getOrganizer();
        parkingInfo = event.getParkingInfo();
        securityInfo = event.getSecurityInfo();
        thumbnail = event.getThumbnail();
        image = event.getImage();
	}

	public int getAdvance() {
		return advance;
	}

	public void setAdvance(int advance) {
		this.advance = advance;
	}

	public int getRegular() {
		return regular;
	}

	public void setRegular(int regular) {
		this.regular = regular;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStringStartDate() {
        return stringStartDate;
    }

    public void setStringStartDate(String stringStartDate) {
        this.stringStartDate = stringStartDate;
    }

    public String getStringEndDate() {
        return stringEndDate;
    }

    public void setStringEndDate(String stringEndDate) {
        this.stringEndDate = stringEndDate;
    }

    public String getPromoter() {
        return promoter;
    }

    public void setPromoter(String promoter) {
        this.promoter = promoter;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getParkingInfo() {
        return parkingInfo;
    }

    public void setParkingInfo(String parkingInfo) {
        this.parkingInfo = parkingInfo;
    }

    public String getSecurityInfo() {
        return securityInfo;
    }

    public void setSecurityInfo(String securityInfo) {
        this.securityInfo = securityInfo;
    }

    public int getHasLoadedDetails() {
        return hasLoadedDetails;
    }

    public void setHasLoadedDetails(int hasLoadedDetails) {
        this.hasLoadedDetails = hasLoadedDetails;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public boolean hasImage() {
        return (this.getImage() != null && this.getImage().length > 0);
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(venue);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(ticketsLink);
        dest.writeLong(id);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeString(stringStartDate);
        dest.writeString(stringEndDate);
        dest.writeString(imageUrl);
        dest.writeInt(noGoing);
        dest.writeInt(noInterested);
        dest.writeInt(statusGoing);
        dest.writeInt(statusInterested);
        dest.writeTypedList(comments);
        dest.writeInt(newComments);
        dest.writeInt(totalComments);
        dest.writeInt(hasLoadedDetails);
        dest.writeInt(advance);
        dest.writeInt(regular);
        dest.writeInt(vip);
        dest.writeString(county);
        dest.writeString(category);
        dest.writeString(promoter);
        dest.writeString(organizer);
        dest.writeString(parkingInfo);
        dest.writeString(securityInfo);
        dest.writeByteArray(thumbnail);
        dest.writeByteArray(image);
    }

    public synchronized void addThumbnail(ImageView imageView) {
        this.setThumbnail(getPosterBytes(imageView));
    }

    public synchronized boolean hasThumbnail() {
        return (this.getThumbnail() != null && this.getThumbnail().length > 0);
    }

    public synchronized Bitmap decodeThumbnail() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.getThumbnail());
        return BitmapFactory.decodeStream(bais);
    }

    public synchronized void addImage(ImageView imageView) {
        this.setImage(getPosterBytes(imageView));
    }

    public synchronized Bitmap decodeImage() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.getImage());
        return BitmapFactory.decodeStream(bais);
    }

    private synchronized byte[] getPosterBytes(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public boolean hasLoadedDetails() {
        return this.getHasLoadedDetails() == 1;
    }
}
