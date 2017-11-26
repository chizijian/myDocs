package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import java.io.File;

public class FileHelper {
    public static boolean isDocsetVersionSaved(String name, String version, boolean latestVersion, Context context) {
        String folder;
        String fileName;
        if (latestVersion) {
            folder = name + "_Latest";
            fileName = folder + ".tgz";
        } else {
            folder = name + "_" + version;
            fileName = folder + ".tgz";
        }
        return new File(context.getExternalFilesDir("/docsets/" + name + "/" + folder + "/"), fileName).exists();
    }
}
