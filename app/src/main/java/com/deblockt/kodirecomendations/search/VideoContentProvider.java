package com.deblockt.kodirecomendations.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.deblockt.kodirecomendations.RecommendationBackgroundContentProvider;
import com.deblockt.kodirecomendations.database.Video;
import com.deblockt.kodirecomendations.database.VideoDatabaseHelper;
import com.deblockt.kodirecomendations.search.parser.QueryParser;

import java.util.ArrayList;
import java.util.List;

import static com.deblockt.kodirecomendations.database.VideoDatabaseHelper.MOVIE_ADD_ID;

/**
 * Created by thomas on 12/11/16.
 */
public class VideoContentProvider extends ContentProvider {

    public static final String[] MENU_COLS = new String[]{
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_DURATION,
            SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR,
            SearchManager.SUGGEST_COLUMN_CONTENT_TYPE,
            SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE,
            SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
    };

    @Override
    public boolean onCreate() {
        return false;
    }

    public QueryData parseQuery(String query) {
        return QueryParser.DEFAULT_PARSER.parse(query);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String videoName = selectionArgs[0];

        QueryData queryData = this.parseQuery(videoName);

        VideoDatabaseHelper database = new VideoDatabaseHelper(getContext());
        List<Video> videoList = null;
        if (queryData.season == null) {
            videoList = database.findMovieByName(queryData.videoName);
            boolean isTvShow = false;
            if (videoList.size() == 0) {
                isTvShow = true;
                videoList = database.findTvShowNextEpisodes(queryData.videoName);
            }
            if (videoList.size() == 0) {
                // if no next episodes show saison 1
                isTvShow = true;
                videoList = database.findSeasonEpisodesByName(queryData.videoName, 1);
            }

            if (isTvShow && videoList.size() > 0) {
                Video firstVideo = videoList.get(0);

                firstVideo.setDescription(firstVideo.getDescription() + " : " + firstVideo.getTitle());
                firstVideo.setTitle(videoName);
            }
        } else if (queryData.season != null && queryData.episode != null) {
            videoList = database.findEpisodeByName(queryData.videoName, queryData.season, queryData.episode);
        } else {
            videoList = database.findSeasonEpisodesByName(queryData.videoName, queryData.season);
        }

        Log.d("VideoContentProvider", "Query " + queryData + " " + videoList.size() + " found");
        MatrixCursor mc = new MatrixCursor(MENU_COLS);

        for (Video video : videoList) {
            mc.addRow(new Object[]{
                    video.getId(),
                    video.getId() - MOVIE_ADD_ID,
                    video.getTitle(),
                    video.getDescription(),
                    video.getDuration() * 60000,
                    video.getYear(),
                    "video/mkv",
                    RecommendationBackgroundContentProvider.addBackground(video.getPoster()),
                    video.getPath()
            });
        }

        return mc;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
