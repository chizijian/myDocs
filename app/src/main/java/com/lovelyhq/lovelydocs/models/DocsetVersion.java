package com.lovelyhq.lovelydocs.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.HashMap;
import java.util.Map;

public class DocsetVersion implements Parcelable {
    public static final Creator<DocsetVersion> CREATOR = new Creator<DocsetVersion>() {
        public DocsetVersion createFromParcel(Parcel source) {
            return new DocsetVersion(source);
        }

        public DocsetVersion[] newArray(int size) {
            return new DocsetVersion[size];
        }
    };
    public static final String KEY = "docset_version";
    public static final int STATUS_DOWNLOADING = 0;
    public static final int STATUS_SAVED = 1;
    private Docset docset;
    private boolean hasTarix;
    private int id;
    private boolean isLatest;
    private String path;
    private int status;
    private boolean updateExists;
    private String version;

    public DocsetVersion(Docset docset, String version, int status, String path, boolean isLatest, boolean hasTarix) {
        this.docset = docset;
        this.version = version;
        this.status = status;
        this.path = path;
        this.isLatest = isLatest;
        this.updateExists = false;
        this.hasTarix = hasTarix;
    }

    private DocsetVersion(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.id = in.readInt();
        this.docset = (Docset) in.readParcelable(Docset.class.getClassLoader());
        this.version = in.readString();
        this.status = in.readInt();
        this.path = in.readString();
        if (in.readByte() != (byte) 0) {
            z = true;
        } else {
            z = false;
        }
        this.isLatest = z;
        if (in.readByte() == (byte) 0) {
            z2 = false;
        }
        this.updateExists = z2;
    }

    public DocsetVersion() {

    }

    public boolean hasTarix() {
        return this.hasTarix;
    }

    public void setHasTarix(boolean hasTarix) {
        this.hasTarix = hasTarix;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Docset getDocset() {
        return this.docset;
    }

    public void setDocset(Docset docset) {
        this.docset = docset;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLatest() {
        return this.isLatest;
    }

    public void setLatest(boolean isLatest) {
        this.isLatest = isLatest;
    }

    public boolean isUpdateExists() {
        return this.updateExists;
    }

    public void setUpdateExists(boolean updateExists) {
        this.updateExists = updateExists;
    }

    public String toString() {
        return "DocsetVersion{id=" + this.id + ", docset=" + this.docset + ", version='" + this.version + '\'' + ", status=" + this.status + ", path='" + this.path + '\'' + ", isLatest=" + this.isLatest + ", hasTarix=" + this.hasTarix + '}';
    }

    public String getTgzName() {
        if (isLatest()) {
            return getDocset().getName() + "_Latest.tgz";
        }
        return getDocset().getName() + "_" + getVersion() + ".tgz";
    }

    public String getTarixName() {
        if (isLatest()) {
            return getDocset().getName() + "_Latest.tgz.tarix";
        }
        return getDocset().getName() + "_" + getVersion() + ".tgz.tarix";
    }

    public String getExtractedDirectory() {
        Map<String, String> urlDirectoryMap = new HashMap();
        urlDirectoryMap.put(".NET Framework", "");
        urlDirectoryMap.put("Action Script", "ActionScript");
        urlDirectoryMap.put("Apache", "Apache_HTTP_Server");
        urlDirectoryMap.put("Angular Dart", "Angular.dart");
        urlDirectoryMap.put("Backbone", "BackboneJS");
        urlDirectoryMap.put("Bootstrap 2", "Bootstrap");
        urlDirectoryMap.put("Bourbon Neat", "Neat");
        urlDirectoryMap.put("AWS JavaScript", "AWS_JavaScript");
        urlDirectoryMap.put("D3.js", "D3JS");
        urlDirectoryMap.put("Dojo Toolkit", "Dojo");
        urlDirectoryMap.put("Drupal 7", "Drupal");
        urlDirectoryMap.put("Drupal 8", "Drupal");
        urlDirectoryMap.put("Ember.js", "EmberJS");
        urlDirectoryMap.put("Emmet.io", "Emmet");
        urlDirectoryMap.put("Express.js", "Express");
        urlDirectoryMap.put("FontAwesome", "Font_Awesome");
        urlDirectoryMap.put("Java SE6", "Java");
        urlDirectoryMap.put("Java SE7", "Java");
        urlDirectoryMap.put("Java SE8", "Java");
        urlDirectoryMap.put("Java FX", "JavaFX");
        urlDirectoryMap.put("Knockout.js", "KnockoutJS");
        urlDirectoryMap.put("Lua 5.1", "Lua");
        urlDirectoryMap.put("Lua 5.2", "Lua");
        urlDirectoryMap.put("Moment.js", "MomentJS");
        urlDirectoryMap.put("NET Framework", "NET Framework");
        urlDirectoryMap.put("Node.js", "NodeJS");
        urlDirectoryMap.put("OpenGL 2", "OpenGL2");
        urlDirectoryMap.put("OpenGL 3", "OpenGL3");
        urlDirectoryMap.put("OpenGL 4", "OpenGL4");
        urlDirectoryMap.put("Play Java", "Play_Java");
        urlDirectoryMap.put("Play Scala", "Play_Scala");
        urlDirectoryMap.put("Processing.org", "Processing");
        urlDirectoryMap.put("Prototype", "PrototypeJS");
        urlDirectoryMap.put("Qt 4", "Qt");
        urlDirectoryMap.put("Qt 5", "Qt");
        urlDirectoryMap.put("Ruby 2", "Ruby");
        urlDirectoryMap.put("Ruby on Rails 3", "Ruby on Rails");
        urlDirectoryMap.put("Ruby on Rails 4", "Ruby on Rails");
        urlDirectoryMap.put("Underscore.js", "UnderscoreJS");
        urlDirectoryMap.put("Unity3D", "Unity 3D");
        urlDirectoryMap.put("Zend Framework 1", "Zend_Framework");
        urlDirectoryMap.put("Zend Framework 2", "Zend Framework");
        urlDirectoryMap.put("Zepto.js", "ZeptoJS");
        urlDirectoryMap.put("Marionette.js", "MarionetteJS");
        String extractedDir = (String) urlDirectoryMap.get(getDocset().getName());
        if (extractedDir != null) {
            return extractedDir + ".docset";
        }
        return getDocset().getName() + ".docset";
    }

    public String getTarixFile() {
        Map<String, String> urlDirectoryMap = new HashMap();
        urlDirectoryMap.put(".NET Framework", "");
        urlDirectoryMap.put("Action Script", "ActionScript");
        urlDirectoryMap.put("Apache", "Apache_HTTP_Server");
        urlDirectoryMap.put("Angular Dart", "Angular.dart");
        urlDirectoryMap.put("Backbone", "BackboneJS");
        urlDirectoryMap.put("Bourbon Neat", "Neat");
        urlDirectoryMap.put("AWS JavaScript", "AWS_JavaScript");
        urlDirectoryMap.put("D3.js", "D3JS");
        urlDirectoryMap.put("Dojo Toolkit", "Dojo");
        urlDirectoryMap.put("Ember.js", "EmberJS");
        urlDirectoryMap.put("Emmet.io", "Emmet");
        urlDirectoryMap.put("Express.js", "Express");
        urlDirectoryMap.put("FontAwesome", "Font_Awesome");
        urlDirectoryMap.put("Knockout.js", "KnockoutJS");
        urlDirectoryMap.put("Moment.js", "MomentJS");
        urlDirectoryMap.put("NET Framework", "NET_Framework");
        urlDirectoryMap.put("Node.js", "NodeJS");
        urlDirectoryMap.put("Play Java", "Play_Java");
        urlDirectoryMap.put("Play Scala", "Play_Scala");
        urlDirectoryMap.put("Processing.org", "Processing");
        urlDirectoryMap.put("Prototype", "PrototypeJS");
        urlDirectoryMap.put("Underscore.js", "UnderscoreJS");
        urlDirectoryMap.put("Unity3D", "Unity 3D");
        urlDirectoryMap.put("Zepto.js", "ZeptoJS");
        urlDirectoryMap.put("Marionette.js", "MarionetteJS");
        urlDirectoryMap.put("Java FX", "JavaFX");
        String extractedDir = (String) urlDirectoryMap.get(getDocset().getName());
        if (extractedDir != null) {
            return extractedDir + ".docset";
        }
        return getDocset().getName() + ".docset";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        byte b;
        byte b2 = (byte) 1;
        dest.writeInt(this.id);
        dest.writeParcelable(this.docset, 0);
        dest.writeString(this.version);
        dest.writeInt(this.status);
        dest.writeString(this.path);
        if (this.isLatest) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        dest.writeByte(b);
        if (!this.updateExists) {
            b2 = (byte) 0;
        }
        dest.writeByte(b2);
    }
}
