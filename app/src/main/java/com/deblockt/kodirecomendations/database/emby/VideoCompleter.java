package com.deblockt.kodirecomendations.database.emby;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.deblockt.kodirecomendations.database.Video;
import com.deblockt.kodirecomendations.database.VideoDatabaseHelper;
import com.deblockt.kodirecomendations.database.VideoType;

public class VideoCompleter {
    private final static String IMAGE_URL = "http://%s/emby/Items/%s/Images/Primary?maxHeight=322&tag=%s&quality=90";
    private final static String BACKDROP_URL = "http://%s/emby/Items/%s/Images/Backdrop?tag=%s&quality=100";
    private final EmbyDatabaseHelper HELPER;

    public VideoCompleter(Context context) {
        this.HELPER = new EmbyDatabaseHelper(context);
    }

    public void complete(Video video) {
        if (!TextUtils.isEmpty(video.getFanart()) && !TextUtils.isEmpty(video.getPoster())) {
            return;
        }

       // video.setPath("prosmb://192.168.0.20/multimedia/Multimedia/Film/Ooops.noah.is.gone/Ooops.noah.is.gone.mkv");

        Log.i("VideoCompleter", "Load emby info for " + video.getId() + " : " + video.getTitle() + " : " + video.getType());
        EmbyInfo info = null;
        if (VideoType.EPISODE.equals(video.getType())) {
            info =  HELPER.getEmbyInfo(EmbyDatabaseHelper.VideoType.EPISODE, video.getId() - VideoDatabaseHelper.TV_SHOW_ADD_ID);
        } else {
            info =  HELPER.getEmbyInfo(EmbyDatabaseHelper.VideoType.MOVIE, video.getId() - VideoDatabaseHelper.MOVIE_ADD_ID);
        }

        if (info != null) {
            Log.i("VideoCompleter", "Emby info found " + video.getTitle() + " : " + info);
            String image = String.format(IMAGE_URL, EmbySettings.EMBY_IP, info.getPrimaryItem(), info.getTag());
            String background = String.format(BACKDROP_URL, EmbySettings.EMBY_IP, info.getBackdropItem(), info.getTag());

            Log.i("VideoCompleter", "image " + image);
            Log.i("VideoCompleter", "background " + background);

            if (TextUtils.isEmpty(video.getPoster())) {
                video.setPoster(image);
            }
            if (TextUtils.isEmpty(video.getFanart())) {
                video.setFanart(background);
            }


        } else {
            Log.i("VideoCompleter", "Not emby info found for " + video.getTitle());
        }
    }
}
