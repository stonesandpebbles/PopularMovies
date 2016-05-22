package com.example.abhijeetsinghkgp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

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
    private String id;
    private List<TrailerData> trailerDataList;
    private List<ReviewData> reviewDataList;
    private String popularSw = FetchMoviesTask.NO;
    private String topRatedSw = FetchMoviesTask.NO;
    private String bookMarkedSw = FetchMoviesTask.NO;

    public String getPopularSw() {
        return popularSw;
    }

    public void setPopularSw(String popularSw) {
        this.popularSw = popularSw;
    }

    public String getTopRatedSw() {
        return topRatedSw;
    }

    public void setTopRatedSw(String topRatedSw) {
        this.topRatedSw = topRatedSw;
    }

    public String getBookMarkedSw() {
        return bookMarkedSw;
    }

    public void setBookMarkedSw(String bookMarkedSw) {
        this.bookMarkedSw = bookMarkedSw;
    }


    public List<TrailerData> getTrailerDataList() {
        return trailerDataList;
    }

    public void setTrailerDataList(List<TrailerData> trailerDataList) {
        this.trailerDataList = trailerDataList;
    }

    public List<ReviewData> getReviewDataList() {
        return reviewDataList;
    }

    public void setReviewDataList(List<ReviewData> reviewDataList) {
        this.reviewDataList = reviewDataList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieData(){

    }
    protected MovieData(Parcel in) {
        posterImageUrl = in.readString();
        title = in.readString();
        backdropImageUrl = in.readString();
        plot = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        id = in.readString();
        backdropImageUrl = in.readString();
        popularSw = in.readString();
        bookMarkedSw = in.readString();
        topRatedSw = in.readString();
        //reviewDataList = in.read
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
        dest.writeString(id);
        dest.writeString(backdropImageUrl);

        dest.writeString(popularSw);
        dest.writeString(bookMarkedSw);
        dest.writeString(topRatedSw);

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
