package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import android.util.Log;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TarixItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import xmlwise.Plist;
import xmlwise.XmlParseException;

public class DocsetLookupHelper {
    private static final String LOG_TAG = "DocsetLookupHelper";

    public static String findDocsetIndex(Context context, DocsetVersion docsetVersion) {
        try {
            TarixItem tarixItem;
            String docsetDirectoryPath = docsetVersion.getPath();
            String docsetDirName = docsetVersion.getExtractedDirectory();
            String extractedDocsetPath = docsetDirectoryPath + File.separator + docsetDirName;
           // File file = new File(docsetVersion.getPath() + "/" + docsetVersion.getTgzName());
            TarixDatabaseHelper tarixDatabaseHelper = new TarixDatabaseHelper(context, docsetVersion);
            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            Map<String, Object> properties = Plist.load(extractedDocsetPath + "/Contents/Info.plist");
            String dashIndexFilePath = PlistHelper.getValue("dashIndexFilePath", properties);
            if (!(dashIndexFilePath == null || dashIndexFilePath.isEmpty())) {
                tarixItem = tarixDatabaseHelper.getEntry(docsetDirName + "/" + ("Contents/Resources/Documents/" + dashIndexFilePath), true);
                if (!(tarixItem == null || tarixItem == new TarixItem() || tarixItem.getPath() == null || tarixItem.getPath().equals(""))) {
                    return tarixItem.getPath();
                }
            }
            String docsetPlatformFamily = PlistHelper.getValue("DocSetPlatformFamily", properties);
            List<String> indexPaths = new ArrayList(Arrays.asList(context.getResources().getStringArray(context.getResources().getIdentifier("IndexPaths", "array", context.getPackageName()))));
            if (docsetPlatformFamily.equalsIgnoreCase("c")) {
                indexPaths.remove("output/en/cpp.html");
                indexPaths.remove("output/en.cppreference.com/w/cpp.html");
                indexPaths.add("output/en/c.html");
                indexPaths.add("output/en.cppreference.com/w/c.html");
            }
            List<String> firstIndexPlatforms = new ArrayList(Arrays.asList(context.getResources().getStringArray(context.getResources().getIdentifier("FirstIndex", "array", context.getPackageName()))));
            String topPath = docsetDirName + "/Contents/Resources/Documents/";
            indexPaths.add(0, "index.html");
            for (String path : indexPaths) {
                tarixItem = tarixDatabaseHelper.getEntry(topPath + path, true);
                if (tarixItem != null && tarixItem != new TarixItem() && tarixItem.getPath() != null && !tarixItem.getPath().equals("")) {
                    return tarixItem.getPath();
                }
            }
        } catch (XmlParseException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean isDashDatabase(DocsetVersion docsetVersion) {
        try {
            String docsetDirectoryPath = docsetVersion.getPath();
            String extractedDocsetPath = docsetDirectoryPath + File.separator + docsetVersion.getExtractedDirectory();
            String isDashDocset = PlistHelper.getValue("isDashDocset", Plist.load(extractedDocsetPath + "/Contents/Info.plist"));
            if (isDashDocset == null || !isDashDocset.equals("true")) {
                return false;
            }
            return true;
        } catch (XmlParseException e) {
            Log.e(LOG_TAG, "Unable to parse XML.", e);
            return false;
        } catch (IOException e2) {
            Log.e(LOG_TAG, "Unable to read from stream.", e2);
            return false;
        }
    }
}
