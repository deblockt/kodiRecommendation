package com.deblockt.kodirecomendations.database;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by thomas on 30/04/16.
 */
public class KodiSettings {
    private static final String TAG = "KodiSettings";
    public static final String DB_DIR = Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.xbmc.kodi/files/.kodi/userdata/Database";

    /**
     * get the dabase path
     *
     * @param dbName the dbname without version info
     * @return the db path or null if db not found
     */
    public static String getDBPath(final String dbName) {
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            Log.e(TAG, "The direcotry " + dir + " doesn't exists");
        } else {
            Log.d(TAG, dir.getAbsolutePath());
            Log.d(TAG, "can read " + dir.canRead());
        }

        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(dbName);
            }
        });

        if (files != null && files.length > 0) {
            return DB_DIR + "/" + files[0];
        }
        return null;
    }
}
