package com.lovelyhq.lovelydocs.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TarixItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TarixDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private String databasePath;
    private DocsetVersion docsetVersion;

    public TarixDatabaseHelper(Context context, DocsetVersion docsetVersion) {
        super(context, docsetVersion.getDocset().getName(), null, 1);
        this.context = context;
        this.docsetVersion = docsetVersion;
        String docsetDir = docsetVersion.getPath();
        this.databasePath = docsetDir + File.separator + docsetVersion.getTarixFile().replace(" ", "_").replace(".docset", ".tgz.tarix");
    }

    private SQLiteDatabase open() throws SQLiteException {
        return SQLiteDatabase.openDatabase(this.databasePath, null, 0);
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public List<TarixItem> getExtractionList() {
        List<TarixItem> tItemsList = new ArrayList<>();
        SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery("SELECT * from toextract", null);
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return tItemsList;
        }
        do {
            TarixItem tItem = new TarixItem();
            tItem.setPath(cursor.getString(cursor.getColumnIndex("path")));
            String[] hashEntries = cursor.getString(cursor.getColumnIndex("hash")).split("\\s+");
            tItem.setBlockNum(hashEntries[0]);
            tItem.setOffset(hashEntries[1]);
            tItem.setBlockLength(hashEntries[2]);
            tItemsList.add(tItem);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return tItemsList;
    }

    public TarixItem getEntry(String path, boolean type) {
        String selectQuery = null;
        TarixItem tItem = new TarixItem();
        SQLiteDatabase db;
        if (type) {
            selectQuery = "SELECT * FROM tarindex WHERE path=\"" + path + "\"";
        } else {
            try {
                selectQuery = "SELECT * FROM toextract WHERE path=\"" + path + "\"";
            } catch (SQLiteException e) {
                e.printStackTrace();
                tItem = new TarixItem();
            }
        }
        db = open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst() && cursor.getCount() == 1) {
            tItem.setPath(cursor.getString(cursor.getColumnIndex("path")));
            String[] hashEntries = cursor.getString(cursor.getColumnIndex("hash")).split("\\s+");
            tItem.setBlockNum(hashEntries[0]);
            tItem.setOffset(hashEntries[1]);
            tItem.setBlockLength(hashEntries[2]);
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        return tItem;
    }
}
