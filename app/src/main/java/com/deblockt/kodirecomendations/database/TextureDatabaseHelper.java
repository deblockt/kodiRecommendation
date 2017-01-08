package com.deblockt.kodirecomendations.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by thomas on 23/01/2016.
 */
public class TextureDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "TextureDatabaseHelper";
    // db name
    private static final String DB_NAME = "Textures";
    // name with version scan database director for find database file
    private static String DB_PATH;

    // init db file name
    static {
        DB_PATH = KodiSettings.getDBPath(DB_NAME);
        if (DB_PATH == null) {
            Log.e(TAG, "Kodi database " + DB_NAME + " can not be found");
        } else {
            Log.i(TAG, "Kodi database found " + DB_PATH);
        }
    }


    /*
     * Videos stoped before end
     */
    private static final String CACHED_URL = "select cachedurl from texture where url = ?";

    public TextureDatabaseHelper(Context context) {
        super(context, DB_PATH, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing to do here
        // kody manage her databse
    }

    public String getCachedFile(String url) {
		SQLiteDatabase readableDatabase = this.getReadableDatabase();

		Cursor cursor = null;
		try {
			cursor = readableDatabase.rawQuery(CACHED_URL, new String[]{url});

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
                readableDatabase.close();
			}
		}
		return null;
	}

}

