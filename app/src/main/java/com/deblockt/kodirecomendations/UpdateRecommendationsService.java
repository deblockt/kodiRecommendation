package com.deblockt.kodirecomendations;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.deblockt.kodirecomendations.database.Video;
import com.deblockt.kodirecomendations.database.VideoDatabaseHelper;
import com.deblockt.kodirecomendations.time.TimeChecker;

import java.io.IOException;
import java.util.List;

/**
 * Created by thomas on 20/01/2016.
 */
public class UpdateRecommendationsService extends IntentService {
    private static final String TAG = "UpdateRecommendations";

    public UpdateRecommendationsService() {
        super("UpdateRecommendationsService");
        Log.d(TAG, "Service RecommandationSErvice créé");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Updating recommendation cards");
        CacheManager manager = new CacheManager(getApplicationContext());
        VideoDatabaseHelper database = new VideoDatabaseHelper(getApplicationContext());

        TimeChecker.check("start video loading");
        List<Video> lastVideos = database.getLastVideoAdded();
        TimeChecker.check("end video loaded");

        if (lastVideos.isEmpty()) {
            Log.e(TAG, "No video found.");
        }

        try {
            RecommendationBuilder builder = new RecommendationBuilder()
                    .setContext(getApplicationContext())
                    .setSmallIcon(R.drawable.kodi_reco_icon);

            
	   	for (Video lastVideo : lastVideos) {
			Log.d(TAG, "Recommendation - " + lastVideo.getTitle());

		    Notification notification = builder.setBackground(manager.getCacheFile(lastVideo.getFanart()))
		            .setId(lastVideo.getId())
		            .setPriority(0)
		            .setTitle(lastVideo.getTitle())
		            .setDescription(lastVideo.getDescription())
		            .setImage(manager.getCacheFile(lastVideo.getPoster()))
		            .setIntent(buildPendingIntent(lastVideo))
                    .setProgress(lastVideo.getProgress())
                    .setGroup(lastVideo.getGroup())
                    .build();

		    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(lastVideo.getId(), notification);
	   	}
        } catch (IOException e) {
            Log.e(TAG, "Unable to update recommendation", e);
        }
    }

    private PendingIntent buildPendingIntent(Video movie) {

        Intent intent = new Intent();
        Uri videoUri = Uri.parse(movie.getPath());
        intent.setDataAndType(videoUri, "video/*");
        intent.setComponent(new ComponentName("org.xbmc.kodi", "org.xbmc.kodi.Splash"));
        intent.setAction(Intent.ACTION_VIEW);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        return pendingIntent;
    }
}
