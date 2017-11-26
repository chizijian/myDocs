package com.lovelyhq.lovelydocs.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Docset implements Parcelable {
    public static final Creator<Docset> CREATOR = new Creator<Docset>() {
        public Docset createFromParcel(Parcel source) {
            return new Docset(source);
        }

        public Docset[] newArray(int size) {
            return new Docset[size];
        }
    };
    public static final String KEY = "docset";
    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_SAVED = 2;
    private int actionBarIcon;
    private int icon;
    private int id;
    private String name;
    private int status;
    private String url;

    public Docset(int icon, int actionBarIcon, String name, String url, int status) {
        this.icon = icon;
        this.actionBarIcon = actionBarIcon;
        this.name = name;
        this.url = url;
        this.status = status;
    }

    private Docset(Parcel in) {
        this.id = in.readInt();
        this.icon = in.readInt();
        this.actionBarIcon = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
        this.status = in.readInt();
    }

    public Docset() {

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getActionBarIcon() {
        return this.actionBarIcon;
    }

    public void setActionBarIcon(int actionBarIcon) {
        this.actionBarIcon = actionBarIcon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return "Docset{id=" + this.id + ", icon=" + this.icon + ", actionBarIcon=" + this.actionBarIcon + ", name='" + this.name + '\'' + ", url='" + this.url + '\'' + ", status=" + this.status + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.icon);
        dest.writeInt(this.actionBarIcon);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeInt(this.status);
    }
}
