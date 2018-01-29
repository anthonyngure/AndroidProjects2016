package com.mustmobile.model;

/**
 * Created by Tosh on 10/12/2015.
 */
public class Update {

    private String content, timeCreated, title, views, updateId;

    public Update(String content, String title, String timeCreated, String views, String id){
        this.content = content;
        this.title = title;
        this.timeCreated = timeCreated;
        this.views = views;
        this.updateId = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }
}
