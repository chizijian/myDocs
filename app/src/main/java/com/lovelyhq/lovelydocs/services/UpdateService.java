package com.lovelyhq.lovelydocs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.lovelyhq.lovelydocs.helpers.Updater;
import com.lovelyhq.lovelydocs.models.DocsetVersion;

public class UpdateService extends Service {
    public static final String ARG_DOCSET_VERSION = "docset_version";

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            new Updater(this, (DocsetVersion) intent.getParcelableExtra(ARG_DOCSET_VERSION)).updateDocset();
        }
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
