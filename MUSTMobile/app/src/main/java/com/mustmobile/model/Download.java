package com.mustmobile.model;

/**
 * Created by Tosh on 10/12/2015.
 */
public class Download {

    private String name, description, timeUploaded, url, timesDownloaded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(String timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimesDownloaded() {
        return timesDownloaded;
    }

    public void setTimesDownloaded(String timesDownloaded) {
        this.timesDownloaded = timesDownloaded;
    }
}
