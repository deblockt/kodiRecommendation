package com.deblockt.kodirecomendations;

/**
 * Created by thomas on 20/01/2016.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;

import android.net.Uri;
import android.util.Log;


import java.io.IOException;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.os.ParcelFileDescriptor;

import android.database.Cursor;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 20/01/2016.
 */
public final class  RecommendationBuilder {
    private static final String TAG = "RecommendationBuilder";

    private String mTitle;
    private String mDescription;
    private String mImageUri;
    private String mBackgroundUri;
    private Context context;
    private int smallIcon;
    private int id;
    private int priority;
    private PendingIntent intent;
    private Integer progress = -1;
    private String group;

    public RecommendationBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public RecommendationBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public RecommendationBuilder setDescription(String description) {
        mDescription = description;
        return this;
    }

    public RecommendationBuilder setImage(String uri) {
        mImageUri = uri;
        return this;
    }

    public RecommendationBuilder setBackground(String uri) {
        mBackgroundUri = uri;
        return this;
    }

    public RecommendationBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public RecommendationBuilder setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public RecommendationBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public RecommendationBuilder setProgress(Integer progress) {
        this.progress = progress;
        return this;
    }

    public RecommendationBuilder setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public RecommendationBuilder setIntent(PendingIntent intent) {
        this.intent = intent;
        return this;
    }

    public Notification build() throws IOException {
        Log.d(TAG, "mBackgroundUri URL - " + mBackgroundUri);
        Log.d(TAG, "mImageUri URL - " + mImageUri);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(mTitle)
                .setContentText(mDescription)
                .setPriority(priority)
                .setLocalOnly(true)
                .setOngoing(true)
                .setGroup(group)
                .setColor(context.getResources().getColor(R.color.fastlane_background))
                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setSmallIcon(smallIcon)
                .setContentIntent(intent);

        try {
            builder.setLargeIcon(BitmapFactory.decodeStream(new java.net.URL(mImageUri).openStream()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


        Bundle extras = new Bundle();

        if (mBackgroundUri != null) {
            String backgroundURI = RecommendationBackgroundContentProvider.addBackground( mBackgroundUri);
            Log.d(TAG, "Add background " + id + " <==> " + mBackgroundUri);
            extras.putString(Notification.EXTRA_BACKGROUND_IMAGE_URI, backgroundURI);
        }

        builder.setExtras(extras);

        if(progress != null){
            builder.setProgress(100,progress,false);
        }

        NotificationCompat.BigPictureStyle bigPicture = new NotificationCompat.BigPictureStyle(builder);

        Notification notification = bigPicture.build();

        return notification;
    }

}

