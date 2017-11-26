package com.lovelyhq.lovelydocs.helpers;

import android.util.Log;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import xmlwise.Plist;

public class PlistHelper {
    public static String getValue(String key, Map<String, Object> dict) {
        String result = "";
        try {
            for (Entry<String, Object> entry : dict.entrySet()) {
                if (key.equals(entry.getKey())) {
                    result = entry.getValue().toString();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("Plist", "PlistHelper getValue " + e);
        }
        return result;
    }

    public static Boolean setValue(String key, String value, Map<String, Object> dict) {
        try {
            dict.put(key, value);
        } catch (Exception e) {
            Log.e("Plist", "PlistHelper setValue " + e);
        }
        return Boolean.TRUE;
    }

    public static String storePlist(String plistLocation, Map<String, Object> dict) {
        try {
            Plist.store((Map) dict, plistLocation);
            return plistLocation;
        } catch (IOException e) {
            Log.e("Plist", "PlistHelper storePlist " + e);
            return null;
        }
    }

    public static Map<String, Object> getDictForKey(String key, Map<String, Object> dict) {
        try {
            for (Entry<String, Object> entry : dict.entrySet()) {
                if (key.equals(entry.getKey())) {
                    return (Map) entry.getValue();
                }
            }
            return null;
        } catch (Exception e) {
            Log.e("Plist", "PlistHelper getDictForKey " + e);
            return null;
        }
    }
}
