package com.deblockt.kodirecomendations;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.deblockt.kodirecomendations.database.Video;
import com.deblockt.kodirecomendations.database.VideoDatabaseHelper;

/**
 * Created by thomas on 12/11/16.
 */
public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = getIntent().getData().getLastPathSegment();

        Video video = new VideoDatabaseHelper(this).findMovieById(id);

        Intent intent = new Intent(this, KodiRunActivity.class);
        intent.putExtra("url", video.getPath());
        startActivity(intent);
        finish();
    }

}
