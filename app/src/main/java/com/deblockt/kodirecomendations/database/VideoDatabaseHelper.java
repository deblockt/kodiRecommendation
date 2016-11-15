package com.deblockt.kodirecomendations.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by thomas on 23/01/2016.
 */
public class VideoDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "VideoDatabaseHelper";
    // db name
    private static final String DB_NAME = "MyVideos";

    // name with version scan database director for find database file
    private static final String DB_PATH;

    // Integer add on kodi id to have unique id
    public static final Integer TV_SHOW_ADD_ID = 200000;
    public static final Integer MOVIE_ADD_ID = 100000;


    // init db file name
    static {
        DB_PATH = KodiSettings.getDBPath(DB_NAME);

        if (DB_PATH == null) {
            Log.e(TAG, "Kodi database " + DB_NAME + " can not be found");
        } else {
            Log.i(TAG, "Kodi database found " + DB_PATH);
        }
    }


    /*
     * Videos stoped before end 
     */
    private static final String MOVIE_RESUME = "SELECT " +
            "idMovie + " + MOVIE_ADD_ID + " as id "+
            ", c00 as title "+
            ", c08 as poster "+
            ", c20 as fanart "+
            ", case when strFilename like strPath  || '%' then  strFilename else  'file://' || strPath || strFilename end  as path "+
            ", (resumeTimeInSeconds / totalTimeInSeconds) * 100 as progress "+
            ", 'Reprendre' as description "+
            ", 0 as year, 0 as duration " +
            "FROM movie_view " +
            "WHERE "+
            " resumeTimeInSeconds / totalTimeInSeconds > 0.05 "+
            "AND resumeTimeInSeconds / totalTimeInSeconds < 0.9 "+
            "ORDER BY lastPlayed DESC "+
            "LIMIT 2";

    private static final String EPISODE_RESUME = "SELECT "+
            "idEpisode + " + TV_SHOW_ADD_ID + " as id "+
            ", c00 as title "+
            ", c06 as poster "+
            ", null as fanart "+
            ", case when strFilename like strPath  || '%' then  strFilename else  'file://' || strPath || strFilename end as path "+
            ", (resumeTimeInSeconds / totalTimeInSeconds) * 100 as progress "+
            ", 'Reprendre' as description "+
            ", 0 as year, 0 as duration " +
            "FROM episode_view  "+
            "WHERE  "+
            " resumeTimeInSeconds / totalTimeInSeconds > 0.05  "+
            "AND resumeTimeInSeconds / totalTimeInSeconds < 0.9 "+
            "ORDER BY lastPlayed DESC  "+
            "LIMIT 2";

    public static final String NEW_MOVIE = "SELECT  "+
            "idMovie + " + MOVIE_ADD_ID + " as id "+
            ", c00 as title "+
            ", c08 as poster "+
            ", c20 as fanart "+
            ", case when strFilename like strPath  || '%' then  strFilename else  'file://' || strPath || strFilename end as path "+
            ", null as progress "+
            ", 'Nouveau' as description "+
            ", 0 as year, 0 as duration " +
            "FROM movie_view  "+
            "WHERE  "+
            "playCount is null "+
            "and resumeTimeInSeconds is null "+
            "order by dateadded desc, c13 desc "+
            "LIMIT 3";

    public static final String NEW_SHOW = "select episode_view.idEpisode + " + TV_SHOW_ADD_ID + " as id "+
            ", tvshow_view.c00 as title "+
            ", tvshow_view.c06 as poster "+
            ", tvshow_view.c11 as fanart "+
            ", case when episode_view.strFilename like episode_view.strPath  || '%' then  episode_view.strFilename else  'file://' || episode_view.strPath || episode_view.strFilename end as path "+
            ", null as progress "+
            ", 'Nouvelle série' as description "+
            ", 0 as year, 0 as duration " +
            "	from episode_view "+
            "	join season_view on episode_view.idSeason = season_view.idSeason  "+
            "	join tvshow_view on tvshow_view.idShow = season_view.idShow "+
            "	join ( "+
            "		select idShow, min (c12 * 100 + c13) episodeId from episode  "+
            "			where idShow in (SELECT tvshow_view.idShow "+
            "						FROM tvshow_view  "+
            "						WHERE  tvshow_view.watchedcount = 0 and tvshow_view.dateAdded >  datetime('now',  '-1 months')  "+
            "					order by c04 desc "+
            "					limit 2) "+
            "			group by idShow "+
            "	) nextEpisodes  on nextEpisodes.idShow = season_view.idShow and episode_view.c12 * 100 + episode_view.c13 = nextEpisodes.episodeId";


    public static final String LAST_PLAYED_EPISODE = "select  "+
            "season_view.idShow as idShow, "+
            "season_view.idSeason as idSeason, "+
            "max(episode_view.c12 * 100 + episode_view.c13) as lastPlayed," +
            "tvshow_view.lastPlayed as lastPlayedDate "+
            "from tvshow_view "+
            "	join season_view on season_view.idShow = tvshow_view.idShow  "+
            "	join episode_view on episode_view.idSeason = season_view.idSeason "+
            "where episode_view.playCount is not null  "+
            "	and tvshow_view.totalCount <> tvshow_view.watchedcount "+
            "group by season_view.idSeason ";

    public static final String TO_PLAY_EPISODE = "select req.idShow, max(episodeId) as episodeId from (" +
            "select  season_view.idShow, lastEpisode.lastPlayed, "+
            "min(episode_view.c12 * 100 + episode_view.c13) episodeId, " +
            "min(lastEpisode.lastPlayedDate) lastPlayedDate  "+
            "from episode_view "+
            "	join season_view on episode_view.idSeason = season_view.idSeason   "+
            "	join (" + LAST_PLAYED_EPISODE + ") " +
            "lastEpisode on lastEpisode.idShow = season_view.idShow "+
            "where episode_view.c12 * 100 + episode_view.c13 = lastEpisode.lastPlayed + 1  "+
            "	   or episode_view.c12 * 100 + episode_view.c13 = (lastEpisode.lastPlayed / 100) * 100 + 101 "+
            "group by season_view.idShow, lastEpisode.lastPlayed" +
            ") req " +
            "group by req.idShow " +
            "order by req.lastPlayedDate desc " +
            "limit 3";

    public static final String CONTINUE_TVSHOW = " select episode_view.idEpisode + " + TV_SHOW_ADD_ID + " as id  "+
            ", tvshow_view.c00 as title "+
            ", tvshow_view.c06 as poster "+
            ", tvshow_view.c11 as fanart "+
            ", case when episode_view.strFilename like episode_view.strPath  || '%' then  episode_view.strFilename else  'file://' || episode_view.strPath || episode_view.strFilename end as path "+
            ", episode_view.c13 * 100 / season_view.episodes as progress "+
            ", 'saison ' || episode_view.c12 || ' ep. ' ||  episode_view.c13 as description "+
            ", 0 as year, 0 as duration " +
            "	from episode_view "+
            "	join season_view on episode_view.idSeason = season_view.idSeason  "+
            "	join tvshow_view on tvshow_view.idShow = season_view.idShow "+
            "	join (" + TO_PLAY_EPISODE +") nextEpisodes on nextEpisodes.idShow = season_view.idShow and episode_view.c12 * 100 + episode_view.c13 = nextEpisodes.episodeId" +
            " limit 3";

    private Context context;
    public VideoDatabaseHelper(Context context) {
        super(context, DB_PATH, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing to do here
        // kody manage her databse
    }

    /**
     * get last added video
     * @return
     */
    public List<Video> getLastVideoAdded() {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();

        List<Video> list = new ArrayList<Video>();
        addToVideoList(readableDatabase, list, NEW_MOVIE, "Nouveaux films", VideoType.MOVIE);
        addToVideoList(readableDatabase, list, NEW_SHOW, "Nouvelles series", VideoType.EPISODE);
        //addToVideoList(readableDatabase, list, MOVIE_RESUME, "films à finir");
        //addToVideoList(readableDatabase, list, EPISODE_RESUME, "episode à finir");
        addToVideoList(readableDatabase, list, CONTINUE_TVSHOW, "serie à continuer", VideoType.EPISODE);

        readableDatabase.close();
        return list;
    }

    private void addToVideoList(SQLiteDatabase readableDatabase, List<Video> list, String query, String group, VideoType type, String... parameters) {
        Cursor cursor = null;
        try {
            cursor = readableDatabase.rawQuery(query, parameters);

            while (cursor.moveToNext()) {
                Log.i(TAG, cursor.getString(cursor.getColumnIndex("title")) + " is found!!! ");

                Video v = VideoFactory.createVideo(
                        this.context,
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        extractFilePath(cursor.getString(cursor.getColumnIndex("poster"))),
                        extractFilePath(cursor.getString(cursor.getColumnIndex("fanart"))),
                        cursor.getString(cursor.getColumnIndex("path")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getInt(cursor.getColumnIndex("progress")),
                        group,
                        cursor.getInt(cursor.getColumnIndex("duration")),
                        cursor.getInt(cursor.getColumnIndex("year")),
                        type
                );

                list.add(v);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * extract first info from fanart XML
     * @param xml
     * @return
     */
    private String extractFilePath(String xml) {
        try {
            int indexOfThumb =  xml.indexOf("<thumb");
            if (indexOfThumb > 0) {
                Random r = new Random();
                for (int i = xml.indexOf("<thumb", indexOfThumb + 1); i != -1 && r.nextBoolean(); i = xml.indexOf("<thumb", indexOfThumb + 1)) {
                    indexOfThumb = i;
                }

                int start = xml.indexOf(">", indexOfThumb + 1) + 1;
                int end = xml.indexOf("</", start);


                int startUrl = xml.indexOf("url=\"");
                String beginUrl = "";
                if (startUrl != -1) {
                    beginUrl = xml.substring(startUrl + 5, xml.indexOf("\">", startUrl + 5));
                }
                Log.d(TAG, "Chargement du fanart " + beginUrl + "<==>" + xml.substring(start, end));
                return beginUrl + xml.substring(start, end);
            }
            int indexOfPreview =  xml.indexOf("preview");
            if (indexOfPreview > 0) {
                int start = xml.indexOf(">", indexOfPreview) + 1;
                int end = xml.indexOf("<", start);
                return xml.substring(start, end);
            }
            int indexOfPoster =  xml.indexOf("poster");
            if (indexOfPoster > 0) {
                int start = xml.indexOf(">", indexOfPoster) + 1;
                int end = xml.indexOf("<", start);
                return xml.substring(start, end);
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public List<Video> findByName(String name) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        List<Video> retour = new ArrayList<>();

        String query = "Select idMovie + " + MOVIE_ADD_ID + " as id, c00 as title, c08 as poster, c20 as fanart " +
                ", case when strFilename like strPath  || '%' then  strFilename else  'file://' || strPath || strFilename end as path " +
                ", null as progress "+
                ", c01 as description " +
                ", c07 as year " +
                ", c11 / 60 as duration" +
                " from movie_view " +
                " where c00 like ?";

        addToVideoList(readableDatabase, retour, query, "finded", VideoType.MOVIE, "%" + name + "%");

        readableDatabase.close();

        return retour;
    }

    public Video findMovieById(String id) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        List<Video> retour = new ArrayList<>();

        String query = "Select idMovie + "  + MOVIE_ADD_ID + " as id, c00 as title, c08 as poster, c20 as fanart " +
                ", case when strFilename like strPath  || '%' then  strFilename else  'file://' || strPath || strFilename end as path " +
                ", null as progress "+
                ", c01 as description " +
                ", c07 as year " +
                ", c11 / 60 as duration" +
                " from movie_view " +
                " where idMovie = ?";

        addToVideoList(readableDatabase, retour, query, "finded", VideoType.MOVIE, id);

        readableDatabase.close();

        return retour.get(0);
    }
}

