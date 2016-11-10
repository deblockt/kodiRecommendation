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


import java.io.FileOutputStream;
import java.io.IOException;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.os.ParcelFileDescriptor;

import android.database.Cursor;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 20/01/2016.
 */
public final class  RecommendationBuilder {
    private static final String TAG = "RecommendationBuilder";
    private static final String BACKGROUND_URI_PREFIX = "content://com.deblockt.kodirecomendations/background/";

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
            RecommendationBackgroundContentProvider.addBackground(id, mBackgroundUri);
            Log.d(TAG, "Add background " + id + " <==> " + mBackgroundUri);
            extras.putString(Notification.EXTRA_BACKGROUND_IMAGE_URI,
                    Uri.parse(BACKGROUND_URI_PREFIX + Integer.toString(id)).toString());
        }

        builder.setExtras(extras);

        if(progress != null){
            builder.setProgress(100,progress,false);
        }

        NotificationCompat.BigPictureStyle bigPicture = new NotificationCompat.BigPictureStyle(builder);

        Notification notification = bigPicture.build();

        return notification;
    }

    public static class RecommendationBackgroundContentProvider extends ContentProvider {

        private static Map<Integer, String> backgrounds = new HashMap<>();

        public static void addBackground(int notifId, String backgroundUrl) {
            backgrounds.put(notifId, backgroundUrl);
        }

        @Override
        public boolean onCreate() {
            return true;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            return 0;
        }

        @Override
        public String getType(Uri uri) {
            return null;
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            return null;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
            return null;
        }

        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            return 0;
        }

        private String getFileExtension(String name) {
            try {
                return name.substring(name.lastIndexOf(".") + 1);
            } catch (Exception e) {
                return "";
            }
        }

        public String MD5(String md5) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] array = md.digest(md5.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                }
                return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
            }
            return null;
        }

        @Override
        /*
         * content provider serving files that are saved locally when recommendations are built
         */
        public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
            Log.i(TAG, "openFile " + uri.getPath());
            if (uri.getPathSegments().isEmpty() || !"background".equals(uri.getPathSegments().get(0))) {
                return null;
            }

            int backgroundId = Integer.parseInt(uri.getLastPathSegment());
            if (!backgrounds.containsKey(backgroundId)) {
                Log.e(TAG, "Erreur aucune image de fond pour le background " + backgroundId);
                return null;
            }

            String fileUri = backgrounds.get(backgroundId);
            if (fileUri.startsWith("file://")) {
                fileUri = fileUri.substring(7);
            } else if (fileUri.startsWith("http")) {
                try {
                    String md5 = MD5(fileUri);
                    File destFile = new File(getContext().getCacheDir(), md5 + "." + getFileExtension(fileUri));
                    // don't download file if not already exists
                    if (!destFile.exists()) {
                        Log.d(TAG, "Downloading file " + fileUri);
                        FileUtils.copyURLToFile(new URL(fileUri), destFile);
                        Log.d(TAG, "file downloaded " + destFile.getAbsolutePath());
                    }
                    fileUri = destFile.getAbsolutePath();

                    backgrounds.put(backgroundId, fileUri);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

            }

            File f = new File(fileUri);
            if (!f.exists()) {
                Log.e(TAG, "file " + fileUri + " doesn't exists");
            } else {
                Log.d(TAG, "file " + fileUri + " loaded");
            }
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        }
    }
}

