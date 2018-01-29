package ke.co.elmaxdevelopers.eventskenya.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable{
	
	private String  content;
    private String createdAt;
    private String username;
	private long id, eventId, eventStartDate;

    public Comment(){

    }

    protected Comment(Parcel in) {
        content = in.readString();
        createdAt = in.readString();
        username = in.readString();
        id = in.readLong();
        eventId = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(createdAt);
        dest.writeString(username);
        dest.writeLong(id);
        dest.writeLong(eventId);
    }

    public long getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(long eventStartDate) {
        this.eventStartDate = eventStartDate;
    }
}
