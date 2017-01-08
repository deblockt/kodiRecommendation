package com.deblockt.kodirecomendations.database;

import android.content.Context;
import android.util.Log;

import com.deblockt.kodirecomendations.database.emby.EmbySettings;
import com.deblockt.kodirecomendations.database.emby.VideoCompleter;


public class VideoFactory {
    /**
     * create a video
     * Use enabled plugin (emby)
     * @param context
     * @param id
     * @param title
     * @param poster
     * @param fanart
     * @param path
     * @param description
     * @param progress
     * @param group
     * @param type
     * @return
     */
    public static Video createVideo(Context context, Integer id, String title, String poster, String fanart, String path, String description, Integer progress, String group, Integer duration, Integer year, VideoType type) {
        Video v = new Video(id, title, poster, fanart, path, description, progress, group, type, year, duration);

        Log.d("VideoFactory", "createVideo");
        if (EmbySettings.exists()) {
            Log.d("VideoFactory", "EmbySettings exists OK");
            new VideoCompleter(context).complete(v);
        }

        return v;
    }
}
