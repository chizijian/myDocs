package com.lovelyhq.lovelydocs.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lovelyhq.lovelydocs.models.Docset;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import java.util.ArrayList;
import java.util.List;

public class DocsetsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DocsetsDatabase";
    private static final int DATABASE_VERSION = 3;
    private static final String LOG = "DocsetsDatabaseHelper";
    private static final String TABLE_DOCSETS = "t_docsets";
    private static final String TABLE_DOCSETS_KEY_AB_ICON = "action_bar_icon";
    private static final String TABLE_DOCSETS_KEY_ICON = "icon";
    private static final String TABLE_DOCSETS_KEY_ID = "_id";
    private static final String TABLE_DOCSETS_KEY_NAME = "name";
    private static final String TABLE_DOCSETS_KEY_STATUS = "status";
    private static final String TABLE_DOCSETS_KEY_URL = "url";
    private static final String TABLE_DOCSET_V = "t_docset_versions";
    private static final String TABLE_DOCSET_V_KEY_DOCSET_ID = "docsetId";
    private static final String TABLE_DOCSET_V_KEY_HAS_TARIX = "hasTarix";
    private static final String TABLE_DOCSET_V_KEY_ID = "_id";
    private static final String TABLE_DOCSET_V_KEY_IS_LATEST = "isLatest";
    private static final String TABLE_DOCSET_V_KEY_PATH = "path";
    private static final String TABLE_DOCSET_V_KEY_STATUS = "status";
    private static final String TABLE_DOCSET_V_KEY_VERSION = "version";
    private static final String TABLE_DOWNLOADS = "t_downloads";
    private static final String TABLE_DOWNLOADS_KEY_DOCSET_VERSION_ID = "doc_v_id";
    private static final String TABLE_DOWNLOADS_KEY_ID = "_id";

    public DocsetsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE t_docsets ( _id INTEGER PRIMARY KEY, icon INTEGER, action_bar_icon INTEGER, name TEXT, url TEXT, status STATUS )");
        addListOfDocsets(InitialDocsetsGenerator.getFullList(), sqLiteDatabase);
        sqLiteDatabase.execSQL("CREATE TABLE t_docset_versions ( _id INTEGER PRIMARY KEY, docsetId INTEGER, version TEXT, status INTEGER, path TEXT, isLatest INTEGER, hasTarix BOOLEAN )");
        sqLiteDatabase.execSQL("CREATE TABLE t_downloads ( _id INTEGER PRIMARY KEY, doc_v_id INTEGER )");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
                sqLiteDatabase.execSQL("ALTER TABLE t_docset_versions ADD COLUMN hasTarix BOOLEAN ");
                updateIconsOfDocsets(InitialDocsetsGenerator.getFullList(), sqLiteDatabase);
                return;
            case 3:
                addListOfDocsets(InitialDocsetsGenerator.generateSeptember2016List(), sqLiteDatabase);
                return;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion" + newVersion);
        }
    }

    public void addListOfDocsets(List<Docset> docsets, SQLiteDatabase database) {
        for (Docset docset : docsets) {
            database.insert(TABLE_DOCSETS, null, docsetToContentValues(docset));
        }
    }

    public static void updateIconsOfDocsets(List<Docset> docsets, SQLiteDatabase database) {
        for (Docset docset : docsets) {
            if (!(docset.getIcon() == 0 || docset.getActionBarIcon() == 0)) {
                String updateDocsetIcon = "UPDATE t_docsets SET icon=" + docset.getIcon() + " WHERE " + TABLE_DOCSETS_KEY_NAME + "=\"" + docset.getName() + "\"";
                database.execSQL("UPDATE t_docsets SET action_bar_icon=" + docset.getActionBarIcon() + " WHERE " + TABLE_DOCSETS_KEY_NAME + "=\"" + docset.getName() + "\"");
                database.execSQL(updateDocsetIcon);
            }
        }
    }

    public List<Docset> getListOfAllDocsets() {
        SQLiteDatabase db = getReadableDatabase();
        List<Docset> docsets = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docsets ORDER BY name ASC", null);
        if (cursor.moveToFirst()) {
            do {
                docsets.add(cursorToDocset(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return docsets;
    }

    public List<DocsetVersion> getListOfDownloadedVersions() {
        SQLiteDatabase db = getReadableDatabase();
        List<DocsetVersion> docsetVersions = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE status = " + String.valueOf(1), null);
        if (cursor.moveToFirst()) {
            do {
                docsetVersions.add(cursorToDocsetVersion(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return docsetVersions;
    }

    public List<DocsetVersion> getListOfActiveVersions() {
        SQLiteDatabase db = getReadableDatabase();
        List<DocsetVersion> docsetVersions = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE (status = " + String.valueOf(1) + " OR " + "status" + " = " + String.valueOf(0) + ")", null);
        if (cursor.moveToFirst()) {
            do {
                docsetVersions.add(cursorToDocsetVersion(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return docsetVersions;
    }

    public Docset getDocsetById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Docset docset = new Docset();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docsets WHERE _id=" + String.valueOf(id), null);
        if (cursor.moveToFirst()) {
            docset = cursorToDocset(cursor);
        }
        cursor.close();
        return docset;
    }

    public DocsetVersion getDocsetVersionById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        DocsetVersion docsetVersion = new DocsetVersion();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE _id=" + String.valueOf(id), null);
        if (cursor.moveToFirst()) {
            docsetVersion = cursorToDocsetVersion(cursor);
        }
        cursor.close();
        return docsetVersion;
    }

    public DocsetVersion getDocsetVersionByPath(String path) {
        SQLiteDatabase db = getReadableDatabase();
        DocsetVersion docsetVersion = new DocsetVersion();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE path='" + String.valueOf(path) + "'", null);
        if (cursor.moveToFirst()) {
            docsetVersion = cursorToDocsetVersion(cursor);
        }
        cursor.close();
        return docsetVersion;
    }

    public DocsetVersion getDocsetToOverwrite(DocsetVersion docsetVersion) {
        for (DocsetVersion dv : getListOfDownloadedVersions()) {
            if (dv.getDocset().getName().equals(docsetVersion.getDocset().getName()) && dv.isLatest() && dv.getId() != docsetVersion.getId()) {
                return dv;
            }
        }
        return null;
    }

    public int createDocsetVersion(DocsetVersion docsetVersion) {
        SQLiteDatabase db = getWritableDatabase();
        long l = db.insert(TABLE_DOCSET_V, null, docsetVersionToContentValues(docsetVersion));
        db.close();
        return (int) l;
    }

    public void removeDocsetVersion(DocsetVersion docsetVersion) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DOCSET_V, "_id = ?", new String[]{String.valueOf(docsetVersion.getId())});
        db.close();
    }

    public int updateDocset(Docset docset) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_DOCSETS, docsetToContentValues(docset), "_id = ?", new String[]{String.valueOf(docset.getId())});
        db.close();
        return rows;
    }

    public int updateDocsetVersion(DocsetVersion docsetVersion) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_DOCSET_V, docsetVersionToContentValues(docsetVersion), "_id = ?", new String[]{String.valueOf(docsetVersion.getId())});
        db.close();
        return rows;
    }

    public int determineDocsetStatus(Docset docset) {
        int downloading;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE status=" + String.valueOf(1) + " AND " + TABLE_DOCSET_V_KEY_DOCSET_ID + "=" + String.valueOf(docset.getId()), null);
        int saved;
        if (cursor == null || cursor.getCount() <= 0) {
            saved = 0;
        } else {
            saved = cursor.getCount();
        }
        cursor = db.rawQuery("SELECT * FROM t_docset_versions WHERE status=" + String.valueOf(0) + " AND " + TABLE_DOCSET_V_KEY_DOCSET_ID + "=" + String.valueOf(docset.getId()), null);
        if (cursor == null || cursor.getCount() <= 0) {
            downloading = 0;
        } else {
            downloading = cursor.getCount();
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        if (downloading == 0 && saved == 0) {
            return 0;
        }
        if (downloading > 0) {
            return 1;
        }
        return 2;
    }

    private Docset cursorToDocset(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        Docset docset = new Docset(cursor.getInt(cursor.getColumnIndex(TABLE_DOCSETS_KEY_ICON)), cursor.getInt(cursor.getColumnIndex(TABLE_DOCSETS_KEY_AB_ICON)), cursor.getString(cursor.getColumnIndex(TABLE_DOCSETS_KEY_NAME)), cursor.getString(cursor.getColumnIndex("url")), cursor.getInt(cursor.getColumnIndex("status")));
        docset.setId(id);
        return docset;
    }

    private DocsetVersion cursorToDocsetVersion(Cursor cursor) {
        boolean isLatest;
        boolean hasTarix;
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        int docsetId = cursor.getInt(cursor.getColumnIndex(TABLE_DOCSET_V_KEY_DOCSET_ID));
        String version = cursor.getString(cursor.getColumnIndex("version"));
        int status = cursor.getInt(cursor.getColumnIndex("status"));
        String path = cursor.getString(cursor.getColumnIndex(TABLE_DOCSET_V_KEY_PATH));
        if (cursor.getInt(cursor.getColumnIndex(TABLE_DOCSET_V_KEY_IS_LATEST)) == 1) {
            isLatest = true;
        } else {
            isLatest = false;
        }
        if (cursor.getInt(cursor.getColumnIndex(TABLE_DOCSET_V_KEY_HAS_TARIX)) == 1) {
            hasTarix = true;
        } else {
            hasTarix = false;
        }
        DocsetVersion docsetVersion = new DocsetVersion(getDocsetById(docsetId), version, status, path, isLatest, hasTarix);
        docsetVersion.setId(id);
        return docsetVersion;
    }

    private ContentValues docsetToContentValues(Docset docset) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_DOCSETS_KEY_ICON, Integer.valueOf(docset.getIcon()));
        contentValues.put(TABLE_DOCSETS_KEY_AB_ICON, Integer.valueOf(docset.getActionBarIcon()));
        contentValues.put(TABLE_DOCSETS_KEY_NAME, docset.getName());
        contentValues.put("url", docset.getUrl());
        contentValues.put("status", Integer.valueOf(docset.getStatus()));
        return contentValues;
    }

    private ContentValues docsetVersionToContentValues(DocsetVersion docsetVersion) {
        int i;
        int i2 = 1;
        ContentValues contentValues = new ContentValues();
        if (docsetVersion.getDocset() != null) {
            contentValues.put(TABLE_DOCSET_V_KEY_DOCSET_ID, Integer.valueOf(docsetVersion.getDocset().getId()));
        } else {
            contentValues.put(TABLE_DOCSET_V_KEY_DOCSET_ID, Integer.valueOf(docsetVersion.getId()));
        }
        contentValues.put("version", docsetVersion.getVersion());
        contentValues.put("status", Integer.valueOf(docsetVersion.getStatus()));
        contentValues.put(TABLE_DOCSET_V_KEY_PATH, docsetVersion.getPath());
        String str = TABLE_DOCSET_V_KEY_IS_LATEST;
        if (docsetVersion.isLatest()) {
            i = 1;
        } else {
            i = 0;
        }
        contentValues.put(str, Integer.valueOf(i));
        String str2 = TABLE_DOCSET_V_KEY_HAS_TARIX;
        if (!docsetVersion.hasTarix()) {
            i2 = 0;
        }
        contentValues.put(str2, Integer.valueOf(i2));
        String s;
        if (docsetVersion.hasTarix()) {
            s = "ok";
        } else {
            s = "Failed";
        }
        return contentValues;
    }

    public void saveDownload(long downloadId, int docsetVersionId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", Long.valueOf(downloadId));
        contentValues.put(TABLE_DOWNLOADS_KEY_DOCSET_VERSION_ID, Integer.valueOf(docsetVersionId));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_DOWNLOADS, null, contentValues);
        db.close();
    }

    public DocsetVersion getDocsetVersionFromDownloadId(long downloadId) {
        SQLiteDatabase db = getReadableDatabase();
        int docsetId = -1;
        Cursor cursor = db.rawQuery("SELECT doc_v_id FROM t_downloads WHERE _id='" + String.valueOf(downloadId) + "'", null);
        if (cursor.moveToFirst()) {
            docsetId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        if (docsetId >= 0) {
            return getDocsetVersionById(docsetId);
        }
        return null;
    }
}
