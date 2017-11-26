package com.lovelyhq.lovelydocs.helpers;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import com.lovelyhq.lovelydocs.events.DownloadCompletedEvent;
import com.lovelyhq.lovelydocs.events.DownloadStartedEvent;
import com.lovelyhq.lovelydocs.models.Docset;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.services.DeleteService;
import de.greenrobot.event.EventBus;
import java.io.File;

public class Downloader {
    private Context context;
    private DocsetsDatabaseHelper dbHelper;
    private DownloadManager dm;
    private DocsetVersion docsetVersion;
    private boolean mobileEnabled;
    BroadcastReceiver onClicked = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Downloader.this.dm.remove(new long[]{Downloader.this.tgzId});
        }
    };
    private String serverUrl;
    private long tarixId;
    private String tarixServerUrl;
    private long tgzId;

    class DownloadCompleteReceiver extends BroadcastReceiver {
        DownloadCompleteReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra("extra_download_id", -1);
            Cursor c = Downloader.this.dm.query(new Query().setFilterById(new long[]{downloadId}));
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex("status"));
                c.close();
                DocsetVersion dv = new DocsetsDatabaseHelper(context).getDocsetVersionFromDownloadId(downloadId);
                Intent i;
                switch (status) {
                    case 8:
                        if (dv != null) {
                            if (dv.isLatest()) {
                                DocsetVersion oldLatest = Downloader.this.dbHelper.getDocsetToOverwrite(dv);
                                if (oldLatest != null) {
                                    Downloader.this.dbHelper.removeDocsetVersion(oldLatest);
                                    i = new Intent(context, DeleteService.class);
                                    i.putExtra("docset_version", oldLatest);
                                    context.startService(i);
                                    dv.setStatus(1);
                                    Downloader.this.dbHelper.createDocsetVersion(dv);
                                    dv.getDocset().setStatus(Downloader.this.dbHelper.determineDocsetStatus(dv.getDocset()));
                                    Downloader.this.dbHelper.updateDocset(dv.getDocset());
                                    return;
                                }
                            }
                            dv.setStatus(1);
                            Downloader.this.dbHelper.updateDocsetVersion(dv);
                            dv.getDocset().setStatus(Downloader.this.dbHelper.determineDocsetStatus(dv.getDocset()));
                            Downloader.this.dbHelper.updateDocset(dv.getDocset());
                            EventBus.getDefault().post(new DownloadCompletedEvent(dv));
                            return;
                        }
                        return;
                    case 16:
                        System.out.println("DOWNLOAD FAILED: " + dv.getDocset().getName() + " -v" + dv.getVersion());
                        i = new Intent(context, DeleteService.class);
                        i.putExtra("docset_version", dv);
                        context.startService(i);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public Downloader(Context context, Docset docset, String version, boolean isLatest, String server, boolean mobileEnabled) {
        this.context = context;
        this.docsetVersion = new DocsetVersion();
        this.docsetVersion.setDocset(docset);
        this.docsetVersion.setVersion(version);
        this.docsetVersion.setStatus(0);
        this.docsetVersion.setLatest(isLatest);
        this.docsetVersion.setHasTarix(true);
        this.serverUrl = generateServerUrl(server);
        this.mobileEnabled = mobileEnabled;
        this.tarixServerUrl = this.serverUrl + ".tarix";
        this.dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.dbHelper = new DocsetsDatabaseHelper(context);
    }

    private String generateServerUrl(String serverUrl) {
        if (this.docsetVersion.isLatest()) {
            return serverUrl;
        }
        return serverUrl.replace(this.docsetVersion.getDocset().getUrl() + ".tgz", "zzz/versions/" + this.docsetVersion.getDocset().getUrl() + "/" + this.docsetVersion.getVersion() + "/" + this.docsetVersion.getDocset().getUrl() + ".tgz");
    }

    public void downloadDocumentation() {
        if (isExternalStorageWritable()) {
            this.context.registerReceiver(new DownloadCompleteReceiver(), new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
            this.context.registerReceiver(this.onClicked, new IntentFilter("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED"));
            new Thread(new Runnable() {
                public void run() {
                    Downloader.this.downloadTgz();
                    Downloader.this.downloadTarix();
                    Downloader.this.saveDownload();
                }
            }).run();
            return;
        }
        Toast.makeText(this.context, "External storage not found.", Toast.LENGTH_LONG).show();
    }

    private void downloadTgz() {
        Request request = setupDownloadManagerRequest(this.serverUrl);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(0);
        String folder = getFolder();
        String type = getDocumentType(folder);
        request.setDestinationInExternalFilesDir(this.context, type, folder + ".tgz");
        File file = this.context.getExternalFilesDir(type);
        if (file != null) {
            this.docsetVersion.setPath(file.getAbsolutePath());
        }
        if (this.docsetVersion.isLatest()) {
            DocsetVersion oldLatest = this.dbHelper.getDocsetToOverwrite(this.docsetVersion);
            if (oldLatest != null) {
                oldLatest.getDocset().setStatus(1);
                this.dbHelper.updateDocsetVersion(oldLatest);
            } else {
                this.docsetVersion.setId(this.dbHelper.createDocsetVersion(this.docsetVersion));
                this.docsetVersion.getDocset().setStatus(1);
                this.dbHelper.updateDocset(this.docsetVersion.getDocset());
            }
        } else {
            this.docsetVersion.setId(this.dbHelper.createDocsetVersion(this.docsetVersion));
            this.docsetVersion.getDocset().setStatus(1);
            this.dbHelper.updateDocset(this.docsetVersion.getDocset());
        }
        this.tgzId = this.dm.enqueue(request);
    }

    private void downloadTarix() {
        Request request = setupDownloadManagerRequest(this.tarixServerUrl);
        String folder = getFolder();
        request.setDestinationInExternalFilesDir(this.context, getDocumentType(folder), folder + ".tgz.tarix");
        this.tarixId = this.dm.enqueue(request);
    }

    private void saveDownload() {
        new DocsetsDatabaseHelper(this.context).saveDownload(this.tgzId, this.docsetVersion.getId());
        if (this.docsetVersion.isLatest()) {
            DocsetVersion oldLatest = this.dbHelper.getDocsetToOverwrite(this.docsetVersion);
            if (oldLatest != null) {
                EventBus.getDefault().post(new DownloadStartedEvent(oldLatest));
                return;
            }
        }
        EventBus.getDefault().post(new DownloadStartedEvent(this.docsetVersion));
    }

    private Request setupDownloadManagerRequest(String url) {
        Request request = new Request(Uri.parse(url));
        if (this.mobileEnabled) {
            request.setAllowedNetworkTypes(Request.NETWORK_MOBILE);
        } else {
            request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        }
        request.setTitle(this.docsetVersion.getDocset().getName() + " - " + this.docsetVersion.getVersion());
        request.setDescription("LovelyDocs");
        return request;
    }

    private String getFolder() {
        if (this.docsetVersion.isLatest()) {
            return this.docsetVersion.getDocset().getName() + "_Latest";
        }
        return this.docsetVersion.getDocset().getName() + "_" + this.docsetVersion.getVersion();
    }

    private String getDocumentType(String folder) {
        return "/docsets/" + this.docsetVersion.getDocset().getName() + "/" + folder + "/";
    }

    public boolean isExternalStorageWritable() {
        return "mounted".equals(Environment.getExternalStorageState());
    }
}
