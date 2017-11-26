package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.lovelyhq.lovelydocs.models.CondensedType;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.Type;
import com.lovelyhq.lovelydocs.models.TypeItem;
import java.util.ArrayList;
import java.util.List;

public class ReceivedDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private String databasePath;
    private DocsetVersion docsetVersion;

    public ReceivedDatabaseHelper(Context context, DocsetVersion docsetVersion) {
        super(context, docsetVersion.getDocset().getName(), null, 1);
        this.context = context;
        this.docsetVersion = docsetVersion;
        String docsetDir = docsetVersion.getPath();
        this.databasePath = docsetDir + "/" + docsetVersion.getExtractedDirectory() + "/Contents/Resources/docSet.dsidx";
    }

    private SQLiteDatabase open() throws SQLiteException {
        return SQLiteDatabase.openDatabase(this.databasePath, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<CondensedType> getAllTypes() {
        List<Type> types = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            String selectQuery;
            if (DocsetLookupHelper.isDashDatabase(this.docsetVersion)) {
                selectQuery = "SELECT type FROM searchIndex GROUP BY type";
            } else {
                selectQuery = "SELECT ty.ZTYPENAME FROM ZTOKENTYPE ty, ZTOKEN t, ZAPILANGUAGE l WHERE ty.Z_PK = t.ZTOKENTYPE AND (l.ZFULLNAME NOT LIKE '%Java%' OR l.Z_PK = 1) AND l.Z_PK = t.ZLANGUAGE GROUP BY ty.ZTYPENAME";
            }
            db = open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst() && cursor.getCount() != 0) {
                while (!cursor.isAfterLast()) {
                    if (!cursor.isNull(0)) {
                        types.add(new Type(this.context, cursor.getString(0)));
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            types.clear();
        }
        if (db != null) {
            db.close();
        }
        return CondensedType.condenseTypes(types);
    }

    public List<TypeItem> getItemsByType(CondensedType type) {
        List<TypeItem> typeItems = new ArrayList<>();
        boolean isDash = DocsetLookupHelper.isDashDatabase(this.docsetVersion);
        try {
            String selectQuery;
            String criteria = "";
            for (String name : type.getNames()) {
                if (isDash) {
                    criteria = criteria + "type = '" + name + "' OR ";
                } else {
                    criteria = criteria + "ty.ZTYPENAME = '" + name + "' OR ";
                }
            }
            if (!criteria.isEmpty()) {
                criteria = criteria.substring(0, criteria.length() - 3);
            }
            if (isDash) {
                selectQuery = "SELECT name, type, path FROM searchIndex WHERE " + criteria;
            } else {
                selectQuery = "SELECT t.ZTOKENNAME, ty.ZTYPENAME, f.ZPATH, m.ZANCHOR FROM ZFILEPATH f, ZTOKENMETAINFORMATION m, ZTOKEN t, ZTOKENTYPE ty, ZAPILANGUAGE l WHERE (l.ZFULLNAME NOT LIKE '%Java%' OR l.Z_PK = 1) AND l.Z_PK = t.ZLANGUAGE AND f.Z_PK = m.ZFILE AND m.ZTOKEN = t.Z_PK AND ty.Z_PK = t.ZTOKENTYPE AND length(t.ZTOKENNAME) > 0 AND ( " + criteria + " ) ORDER BY LOWER(t.ZTOKENNAME) ";
            }
            SQLiteDatabase db = open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                typeItems.add(new TypeItem(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
            typeItems.clear();
        }
        return typeItems;
    }

    public List<TypeItem> performSearch(String searchQuery) {
        String query = null;
        List<TypeItem> typeItems = new ArrayList<>();
        boolean isDash = DocsetLookupHelper.isDashDatabase(this.docsetVersion);
        StringBuilder sb = new StringBuilder("%");
        for (char valueOf : searchQuery.toCharArray()) {
            sb.append(Character.valueOf(valueOf));
            sb.append("%");
        }
        searchQuery = sb.toString();
        if (isDash) {
            try {
                query = "SELECT name, type, path FROM searchIndex WHERE name LIKE '" + searchQuery + "'";
            } catch (SQLiteException e) {
                e.printStackTrace();
                typeItems.clear();
            }
        } else {
            query = "SELECT t.ZTOKENNAME, ty.ZTYPENAME, f.ZPATH, m.ZANCHOR FROM ZFILEPATH f, ZTOKENMETAINFORMATION m, ZTOKEN t, ZTOKENTYPE ty, ZAPILANGUAGE l WHERE (l.ZFULLNAME NOT LIKE '%Java%' OR l.Z_PK = 1) AND t.ZTOKENNAME LIKE '" + searchQuery + "' AND l.Z_PK = t.ZLANGUAGE AND f.Z_PK = m.ZFILE AND m.ZTOKEN = t.Z_PK AND ty.Z_PK = t.ZTOKENTYPE AND length(t.ZTOKENNAME) > 0 ORDER BY LOWER(t.ZTOKENNAME) ";
        }
        SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            typeItems.add(new TypeItem(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        db.close();
        return typeItems;
    }
}
