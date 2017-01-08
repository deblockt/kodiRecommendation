package com.deblockt.kodirecomendations.database.emby;

/**
 * Created by thomas on 30/04/16.
 */
public class EmbyInfo {

    public String primaryItem;
    private String backdropItem;
    private String tag;


    public String getPrimaryItem() {
        return primaryItem;
    }

    public void setPrimaryItem(String primaryItem) {
        this.primaryItem = primaryItem;
    }

    public String getBackdropItem() {
        return backdropItem;
    }

    public void setBackdropItem(String backdropItem) {
        this.backdropItem = backdropItem;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Emby info [ "+primaryItem+", "+backdropItem+", "+tag+" ]";
    }
}
