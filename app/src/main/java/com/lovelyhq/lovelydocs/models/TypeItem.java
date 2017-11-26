package com.lovelyhq.lovelydocs.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TypeItem implements Parcelable {
    public static final Creator<TypeItem> CREATOR = new Creator<TypeItem>() {
        public TypeItem createFromParcel(Parcel source) {
            return new TypeItem(source);
        }

        public TypeItem[] newArray(int size) {
            return new TypeItem[size];
        }
    };
    private String name;
    private String path;
    private String type;

    public TypeItem(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    private TypeItem(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.path = in.readString();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String toString() {
        return "TypeItem{name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", path='" + this.path + '\'' + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.path);
    }
}
