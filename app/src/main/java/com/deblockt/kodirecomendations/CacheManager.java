package com.deblockt.kodirecomendations;

import android.content.Context;
import android.util.Log;

import com.deblockt.kodirecomendations.database.TextureDatabaseHelper;

import java.io.File;

/**
 * Created by thomas on 09/04/16.
 */
public class CacheManager {

    private static final String TAG = "CacheManager";
    public static String CACHE_DIRECTORY = "/sdcard/Android/data/org.xbmc.kodi/files/.kodi/userdata/Thumbnails/";
    TextureDatabaseHelper helper ;


    public CacheManager(Context context) {
        helper = new TextureDatabaseHelper(context);
    }

    /**
     * return the cache file or the url if not cache found
     *
     * @param url the image URL
     *
     * @return the cache file
     */
    public String getCacheFile(String url) {
        String cacheFile = helper.getCachedFile(url);
        if (cacheFile  == null) {
            Log.d(TAG, "Url cache not found on database " + url);
            return url;
        }

        File cachedFile = new File(CACHE_DIRECTORY + cacheFile);
        if (!cachedFile.exists()) {
            Log.d(TAG, "Le fichier n'existe pas " + cachedFile.getAbsolutePath());
            return url;
        }

        Log.d(TAG, url + " <=> " + cachedFile.getAbsolutePath());
        return "file://"+cachedFile.getAbsolutePath();
    }
}
