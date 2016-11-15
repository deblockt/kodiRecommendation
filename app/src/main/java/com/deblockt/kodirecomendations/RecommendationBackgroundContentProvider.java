package com.deblockt.kodirecomendations;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class RecommendationBackgroundContentProvider extends ContentProvider {
        private final static String TAG = "RecoBackProv";

        private static final String BACKGROUND_URI_PREFIX = "content://com.deblockt.kodirecomendations.background/";
        private static Integer SEQ = 0;

        private static Map<Integer, String> backgrounds = new HashMap<>();

        public synchronized static String addBackground(String backgroundUrl) {
            backgrounds.put(++SEQ, backgroundUrl);
            Log.d(TAG, SEQ + "<<=>>" + backgroundUrl);
            return BACKGROUND_URI_PREFIX + SEQ;
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
            if (uri.getPathSegments().isEmpty()) {
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
