package com.mustmobile.model;

/**
 * Created by Tosh on 10/19/2015.
 */
public class PastPaper {

    private String name, url;

    public PastPaper(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
