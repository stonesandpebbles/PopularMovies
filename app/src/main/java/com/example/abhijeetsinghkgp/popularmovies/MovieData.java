package com.example.abhijeetsinghkgp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Abhijeet on 12-03-2016.
 */
public class MovieData implements Parcelable{
    private String posterImageUrl;
    private String title;
    private String backdropImageUrl;
    private String plot;
    private String rating;
    private String releaseDate;

    public MovieData(){

    }
    protected MovieData(Parcel in) {
        posterImageUrl = in.readString();
        title = in.readString();
        backdropImageUrl = in.readString();
        plot = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public void setBackdropImageUrl(String backdropImageUrl) {
        this.backdropImageUrl = backdropImageUrl;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterImageUrl);
        dest.writeString(title);
        dest.writeString(backdropImageUrl);
        dest.writeString(plot);
        dest.writeString(rating);
        dest.writeString(releaseDate);

    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

}
