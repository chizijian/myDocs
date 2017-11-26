package com.lovelyhq.lovelydocs.models;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Type {
    private String alias;
    private Context context;
    private String name;

    public Type(Context context, String name) {
        this.context = context;
        this.name = name;
        this.alias = getAliasFromName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAliasFromName() {
        Resources res = this.context.getResources();
        String packageName = this.context.getPackageName();
        //Log.e("Type", "getAliasFromName: "+packageName);
        String[] aliases;
        aliases = res.getStringArray(res.getIdentifier("aliases", "array", packageName));
        String typeAlias = findAlias(this.name, aliases);
        if (typeAlias == null) {
            for (String alias : aliases) {
                if (findAlias(this.name, res.getStringArray(res.getIdentifier(alias, "array", packageName))) != null) {
                    typeAlias = alias;
                    break;
                }
            }
        }
        if (typeAlias == null) {
            return this.name;
        }
        return typeAlias;
    }

    private String findAlias(String aliasToLookFor, String[] aliases) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(aliasToLookFor)) {
                return alias;
            }
        }
        return null;
    }

    public String toString() {
        return "Type{context=" + this.context + ", name='" + this.name + '\'' + ", alias='" + this.alias + '\'' + '}';
    }
}
