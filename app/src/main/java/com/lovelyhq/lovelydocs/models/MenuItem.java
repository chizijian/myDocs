package com.lovelyhq.lovelydocs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable {
    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        public MenuItem createFromParcel(Parcel source) {
            return new MenuItem(source);
        }

        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };
    private int icon;
    private String title;

    public MenuItem(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    private MenuItem(Parcel in) {
        this.icon = in.readInt();
        this.title = in.readString();
    }

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.icon);
        dest.writeString(this.title);
    }
}
