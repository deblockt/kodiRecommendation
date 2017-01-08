package com.deblockt.kodirecomendations.search;

/**
 * Created by thomas on 15/11/16.
 * <p>
 * parsed Query from content provider
 */
public class QueryData {

    public String videoName;
    public Integer season;
    public Integer episode;

    public QueryData(String videoName) {
        this.videoName = videoName;
    }


    @Override
    public String toString() {
        if (this.season != null && this.episode != null) {
            return "." + this.videoName + ". S" + this.season + "E" + this.episode;
        } else if (this.season != null) {
            return "." + this.videoName + ". S" + this.season;
        } else {
            return "." + this.videoName + ".";
        }
    }
}
