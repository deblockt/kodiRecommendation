package com.deblockt.kodirecomendations.database.emby;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thomas on 23/01/2016.
 *
 * Used for get emby item images
 */
public class EmbyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "EmbyDatabaseHelper";

    public EmbyDatabaseHelper(Context context) {
        super(context, EmbySettings.DB_PATH, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Emby databse " + db.getPath() + " not found.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing to do here
        // kody manage her databse
    }


    /**
     * get last added video
     * @return
     */
    public EmbyInfo getEmbyInfo(VideoType type, Integer kodiId) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            EmbyInfo info = null;

            cursor = readableDatabase.rawQuery(type.getRequest(), new String[]{String.valueOf(kodiId)});

            Log.d(TAG, "run query " + type.getRequest());
            Log.d(TAG, "parameter " + String.valueOf(kodiId));

            if (cursor.moveToNext()) {
                info = new EmbyInfo();
                info.setPrimaryItem(cursor.getString(cursor.getColumnIndex("primary_item")));
                info.setBackdropItem(cursor.getString(cursor.getColumnIndex("backdrop_item")));
                info.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            }

            return info;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            readableDatabase.close();
        }

        return null;
    }



    /*
     * image from episode
     */
    private static final String EPISODE_INFO = "select episode.emby_id as tag , season.emby_id as primary_item,  tvshow.emby_id as backdrop_item " +
            "from emby episode " +
            "left join emby season on season.kodi_id = episode.parent_id and season.emby_type = 'Season' " +
            "left join emby tvshow on tvshow.kodi_id = season.parent_id and tvshow.media_type = 'tvshow' " +
            "where episode.kodi_id = ? and episode.emby_type='Episode'";

    /**
     * Image from movie
     */
    private static final String MOVIE_INFO = "select media_folder as tag, emby_id as primary_item, emby_id as backdrop_item " +
            "from emby "+
            "where kodi_id = ?  and emby_type='Movie'";
    /**
     * enum for emby type. Can only be a video or an episode
     */
    public enum VideoType {
        MOVIE(MOVIE_INFO), EPISODE(EPISODE_INFO);

        public String request;

        VideoType(String request) {
            this.request = request;
        }

        public String getRequest() {
            return request;
        }
    };
}

