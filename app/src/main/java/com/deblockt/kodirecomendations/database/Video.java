package com.deblockt.kodirecomendations.database;

import android.util.Log;

/**
 * Created by thomas on 24/01/2016.
 */
public class Video {

    private final Integer duration;
    private final Integer year;
    private VideoType type;
    private String title;
    private String poster;
    private String fanart;
    private String path;
    private Integer id;
    private Integer progress;
    private String description;
    private String group;

    public Video(Integer id, String title, String poster, String fanart, String path, String description, Integer progress, String group, VideoType type, Integer year, Integer duration) {
        Log.d("Video", "Fanart : " + fanart);
        this.title = title;
        this.poster = poster;
        this.fanart = fanart;
        this.path = path;
        this.id = id;
        this.progress = progress;
        this.description = description;
        this.group = group;
        this.type = type;
        this.year = year;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getFanart() {
        return fanart;
    }

    public String getPath() {
        return path;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getProgress() {
        return progress;
    }

    public String getGroup() {
        return group;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setFanart(String fanart) {
        this.fanart = fanart;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setType(VideoType type) {
        this.type = type;
    }

    public VideoType getType() {
        return type;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getYear() {
        return year;
    }
}
