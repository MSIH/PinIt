package com.vishwa.pinit;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Note implements Parcelable{
	
	private String id;
	private String title;
	private String body;
	private String creator;
	private String thumbnailUrl;
	private String createdAt;
	private String createdAtFull;
	private double latitude;
	private double longitude;
	
	public Note(String id, String creator, String title, String body, LatLng geopoint, 
			String url, String createdAt, String createdAtFull) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.creator = creator;
		this.thumbnailUrl = url;
		this.createdAt = createdAt;
		this.createdAtFull = createdAtFull;
		this.latitude = geopoint.latitude;
		this.longitude = geopoint.longitude;
	}
	
	public Note(Parcel parcel) {
		String[] data = new String[9];
		
		parcel.readStringArray(data);
		id = data[0];
		title = data[1];
		body = data[2];
		creator = data[3];
		thumbnailUrl = data[4];
		createdAt = data[5];
		createdAtFull = data[6];
		latitude = Double.parseDouble(data[7]);
		longitude = Double.parseDouble(data[8]);
	}
	
	public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in); 
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    
	public String getNoteTitle() {
		return title;
	}
	
	public String getNoteBody() {
		return body;
	}
	
	public String getNoteCreator() {
		return creator;
	}
	
	public String getNoteImageThumbnailUrl() {
		return thumbnailUrl;
	}
	
	public double getNoteLatitude() {
		return latitude;
	}
	
	public double getNoteLongitude() {
		return longitude;
	}
	
	public String getNoteCreatedAt() {
		return createdAt;
	}
	
	public String getNoteCreatedAtFull() {
		return createdAtFull;
	}
	
	public String getNoteId() {
		return id;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(creator);
		builder.append(", ");
		builder.append(title);
		builder.append(", ");
		builder.append(body);
		builder.append(", ");
		builder.append(latitude);
		builder.append(", ");
		builder.append(longitude);
		builder.append(", ");
		builder.append(thumbnailUrl);
		builder.append(", ");
		builder.append(createdAt);
		return builder.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		String[] data = new String[9];
		
		data[0] = id;
		data[1] = title;
		data[2] = body;
		data[3] = creator;
		data[4] = thumbnailUrl;
		data[5] = createdAt;
		data[6] = createdAtFull;
		data[7] = Double.toString(latitude);
		data[8] = Double.toString(longitude);
		
		arg0.writeStringArray(data);
	}
}
