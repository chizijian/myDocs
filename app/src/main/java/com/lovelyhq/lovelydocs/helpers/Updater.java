package com.lovelyhq.lovelydocs.helpers;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.lovelyhq.lovelydocs.events.DownloadCompletedEvent;
import com.lovelyhq.lovelydocs.events.DownloadStartedEvent;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import de.greenrobot.event.EventBus;
import java.io.File;

public class Updater {
    private Context mContext;
    private DocsetsDatabaseHelper mDbHelper;
    private DocsetVersion mDocsetVersion;
    private DownloadManager mDownloadManager;
    private boolean mMobileEnabled;

    class DownloadCompleteReceiver extends BroadcastReceiver {
        DownloadCompleteReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra("extra_download_id", -1);
            Cursor c = Updater.this.mDownloadManager.query(new Query().setFilterById(new long[]{downloadId}));
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex("status"));
                c.close();
                DocsetVersion dv = new DocsetsDatabaseHelper(context).getDocsetVersionFromDownloadId(downloadId);
                switch (status) {
                    case 8:
                        if (dv != null) {
                            dv.setStatus(1);
                            Updater.this.mDbHelper.updateDocsetVersion(dv);
                            dv.getDocset().setStatus(Updater.this.mDbHelper.determineDocsetStatus(dv.getDocset()));
                            Updater.this.mDbHelper.updateDocset(dv.getDocset());
                            EventBus.getDefault().post(new DownloadCompletedEvent(dv));
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public Updater(Context context, DocsetVersion docsetVersion) {
        this.mContext = context;
        this.mDocsetVersion = docsetVersion;
        this.mDbHelper = new DocsetsDatabaseHelper(context);
        this.mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.mMobileEnabled = new PreferencesHelper(context).isMobileEnabled();
    }

    public void updateDocset() {
        if (externalStorageWritable()) {
            new Thread(new Runnable() {
                public void run() {
                    String version = (String) XmlParser.parseVersions(Updater.this.mDocsetVersion.getDocset().getUrl()).get(0);
                    String url = (String) XmlParser.parseServers(Updater.this.mDocsetVersion.getDocset().getUrl()).get(0);
                    System.out.println("URL " + url);
                    long tgzId = Updater.this.downloadTgz(url, version);
                    Updater.this.downloadTarix(url, version);
                    Updater.this.saveDownload(tgzId, version);
                }
            }).start();
        }
    }

    private void downloadTarix(String url, String version) {
        Request request = generateDownloadRequest(url);
        String folder = this.mDocsetVersion.getDocset().getName() + "_Latest";
        String type = "/docsets/" + this.mDocsetVersion.getDocset().getName() + "/" + folder + "/";
        request.setDestinationInExternalFilesDir(this.mContext, type, folder + ".tarix");
        File file = this.mContext.getExternalFilesDir(type);
        System.out.println("File to delete: " + file.getAbsolutePath());
        if (file != null && file.delete()) {
            Log.d("Updater", "Previous version deleted.");
        }
    }

    private void saveDownload(long tgzId, String version) {
        this.mDbHelper.saveDownload(tgzId, this.mDocsetVersion.getId());
        this.mDocsetVersion.setVersion(version);
        this.mDocsetVersion.setStatus(0);
        this.mDbHelper.updateDocsetVersion(this.mDocsetVersion);
        EventBus.getDefault().post(new DownloadStartedEvent(this.mDocsetVersion));
    }

    private long downloadTgz(String url, String version) {
        Request request = generateDownloadRequest(url);
        String folder = this.mDocsetVersion.getDocset().getName() + "_Latest";
        String type = "/docsets/" + this.mDocsetVersion.getDocset().getName() + "/" + folder + "/";
        request.setDestinationInExternalFilesDir(this.mContext, type, folder + ".tgz");
        File file = this.mContext.getExternalFilesDir(type);
        System.out.println("File to delete: " + file.getAbsolutePath());
        if (file != null && file.delete()) {
            Log.d("Updater", "Previous version deleted.");
        }
        return this.mDownloadManager.enqueue(request);
    }

    public Request generateDownloadRequest(String url) {
        Request request = new Request(Uri.parse(url));
        if (this.mMobileEnabled) {
            request.setAllowedNetworkTypes(3);
        } else if (NetworkHelper.isWifiEnabled(this.mContext)) {
            request.setAllowedNetworkTypes(2);
        }
        request.setTitle(this.mDocsetVersion.getDocset().getName() + " - " + this.mDocsetVersion.getVersion());
        request.setDescription("LovelyDocs");
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(0);
        return request;
    }

    public boolean externalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
}
