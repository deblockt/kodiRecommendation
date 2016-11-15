package com.deblockt.kodirecomendations;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 11/11/16.
 */

public class KodiRunActivity extends Activity {

    private static final String TAG = "KodiRunActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "ON EST ICI");
        String path = getIntent().getStringExtra("url");

        Log.d(TAG, "Data : " + getIntent().getData());

        Intent intent = new Intent();
        Uri videoUri = Uri.parse(path);
        intent.setDataAndType(videoUri, "video/*");
        intent.setComponent(new ComponentName("org.xbmc.kodi", "org.xbmc.kodi.Splash"));
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Log.d(TAG, "URI : " + videoUri);
        startActivity(intent);
        finish();
    }
}
