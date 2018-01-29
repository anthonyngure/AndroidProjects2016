package com.mustmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tosh on 10/28/2015.
 */
public class Forum {

    private String name, description, iconUrl, code;

    public Forum(String name, String code, String description, String iconUrl) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.iconUrl = iconUrl;
    }

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
