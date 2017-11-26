package com.lovelyhq.lovelydocs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.lovelyhq.lovelydocs.helpers.Downloader;
import com.lovelyhq.lovelydocs.models.Docset;
import java.util.List;

public class DownloadService extends Service {
    public static final String ARG_DOCSET = "docset";
    public static final String ARG_LATEST_VERSION = "latest_version";
    public static final String ARG_MOBILE_ENABLED = "mobile_enabled";
    public static final String ARG_SERVERS = "servers";
    public static final String ARG_VERSION = "version";
    private Downloader mDownloader;

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Docset docset =  intent.getParcelableExtra( ARG_DOCSET );
            String version = intent.getStringExtra(ARG_VERSION);
            boolean isLatest = intent.getBooleanExtra(ARG_LATEST_VERSION, true);
            List<String> servers = intent.getStringArrayListExtra(ARG_SERVERS);
            this.mDownloader = new Downloader(this, docset, version, isLatest,  servers.get(0), intent.getBooleanExtra(ARG_MOBILE_ENABLED, true));
            this.mDownloader.downloadDocumentation();
        }
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
