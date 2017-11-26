package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import android.content.res.Resources;

public class AliasHelper {
    public static final String[] colors = new String[]{"black", "brick", "gray", "olive", "pink", "red", "blue", "brown", "green", "orange", "purple", "turquoise", "yellow"};

    public static String getAlias(String typeName, Context context) {
        Resources R = context.getResources();
        String packageName = context.getPackageName();
        String[] aliases = R.getStringArray(R.getIdentifier("aliases", "array", packageName));
        String typeAlias = search(typeName, aliases);
        if (typeAlias == null) {
            for (String alias : aliases) {
                if (search(typeName, R.getStringArray(R.getIdentifier(alias, "array", packageName))) != null) {
                    typeAlias = alias;
                    break;
                }
            }
        }
        if (typeAlias == null) {
            return typeName;
        }
        return typeAlias;
    }

    public static short getColor(String typeName, Context context) {
        Resources R = context.getResources();
        String packageName = context.getPackageName();
        short i = (short) 0;
        for (String color : colors) {
            if (search(typeName, R.getStringArray(R.getIdentifier(color, "array", packageName))) != null) {
                return i;
            }
            i = (short) (i + 1);
        }
        return (short) 0;
    }

    private static String search(String searchValue, String[] values) {
        for (String value : values) {
            if (searchValue.equalsIgnoreCase(value)) {
                return value;
            }
        }
        return null;
    }
}
