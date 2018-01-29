package com.mustmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
	
	private String id, content, time, author, eclassvideo;

	public Comment(){

	}

	protected Comment(Parcel in) {
		id = in.readString();
		content = in.readString();
		time = in.readString();
		author = in.readString();
		eclassvideo = in.readString();
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getEclassvideo() {
		return eclassvideo;
	}

	public void setEclassvideo(String eclassvideo) {
		this.eclassvideo = eclassvideo;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(author);
		dest.writeString(eclassvideo);
	}
}
