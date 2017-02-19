package com.example.abhijeetsinghkgp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhijeet on 07-05-2016.
 */
public class ReviewData implements Parcelable {
    private String author;
    private String content;
    private String url;
    private String id;

    public ReviewData(){

    }

    protected ReviewData(Parcel in) {
        author = in.readString();
        content = in.readString();
        url = in.readString();
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
        parcel.writeString(id);
    }
}
