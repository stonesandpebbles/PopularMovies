package com.example.abhijeetsinghkgp.popularmovies.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Abhijeet on 23-04-2016.
 */
public interface MovieReviewColumns {
    @DataType(DataType.Type.TEXT) @PrimaryKey
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @References(table = MovieDatabaseGenerator.MOVIES, column = MovieColumns._ID)
    public static final String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String AUTHOR = "review_author";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String CONTENT = "review_content";
}
