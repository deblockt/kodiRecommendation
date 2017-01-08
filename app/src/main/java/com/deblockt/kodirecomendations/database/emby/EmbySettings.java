package com.deblockt.kodirecomendations.database.emby;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.deblockt.kodirecomendations.database.KodiSettings;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;


public class EmbySettings {
    private static final String TAG = "EmbySettings";

    // db name
    private static final String DB_NAME = "emby";
    // db path

    // name with version scan database director for find database file
    public static final String DB_PATH;
    public static final String EMBY_IP;

    // init db file name
    static {
        DB_PATH = KodiSettings.getDBPath(DB_NAME);

        if (DB_PATH == null) {
            EMBY_IP = null;
            Log.i(TAG, "Emby database can not be found");
        } else {
            Log.i(TAG, "Emby database found " + DB_PATH);
            File settingsFile = new File(KodiSettings.DB_DIR + "/../addon_data/plugin.video.emby/settings.xml");
            EMBY_IP = settingsFile.exists() ? loadEmbyIp(settingsFile) : null;
            if (EMBY_IP == null) {
                Log.e(TAG, "Impossible de charger les paramètres du serveur Emby");
            }
        }
    }

    public static boolean exists() {
        return DB_PATH != null && EMBY_IP != null;
    }

    private static String loadEmbyIp(File settingsFile) {
        String ip = null;
        String port = null;
        InputStream in = null;
        try {
            in = new FileInputStream(settingsFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "settings");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                Log.d(TAG, "name : " + name);
                // Starts by looking for the entry tag
                if (name.equals("setting")) {
                    String id = parser.getAttributeValue(null,"id");
                    Log.d(TAG, "id : " + id);
                    if ("ipaddress".equals(id)) {
                        ip = parser.getAttributeValue(null, "value");
                    } else if ("port".equals(id)) {
                        port = parser.getAttributeValue(null,"value");
                    } else if ("server".equals(id)) {
                        String[] splitedUrl = parser.getAttributeValue(null,"value").split(":");
                        port = splitedUrl[2];
                        ip = splitedUrl[1].substring(2);
                    } else {
                        skip(parser);
                    }
                } else {
                    skip(parser);
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ip == null || port == null) {
            return null;
        }
        return ip + ":" + port;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
