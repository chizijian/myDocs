package com.lovelyhq.lovelydocs.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
import com.lovelyhq.lovelydocs.helpers.DocsetsDatabaseHelper;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import de.greenrobot.event.EventBus;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class DeleteService extends IntentService {
    private static final String NAME = "docset_delete_service";

    public class Signal {
        private boolean success;

        public Signal(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    public DeleteService() {
        super(NAME);
    }

    protected void onHandleIntent(Intent intent) {
        final DocsetVersion dv = (DocsetVersion) intent.getParcelableExtra("docset_version");
        File directory = new File(dv.getPath());
        Handler mHandler = new Handler(getMainLooper());
        try {
            FileUtils.deleteDirectory(directory);
            mHandler.post(new Runnable() {
                public void run() {
                    DocsetsDatabaseHelper dbHelper = new DocsetsDatabaseHelper(DeleteService.this.getApplicationContext());
                    dbHelper.removeDocsetVersion(dv);
                    dv.getDocset().setStatus(dbHelper.determineDocsetStatus(dv.getDocset()));
                    dbHelper.updateDocset(dv.getDocset());
                    Toast.makeText(DeleteService.this.getApplicationContext(), dv.getDocset().getName() + " " + dv.getVersion() + " successfully deleted.", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new Signal(true));
                }
            });
        } catch (IOException e) {
            mHandler.post(new Runnable() {
                public void run() {
                    Toast.makeText(DeleteService.this.getApplicationContext(), "There was a problem deleting " + dv.getDocset().getName() + " " + dv.getVersion() + ".", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new Signal(false));
                }
            });
        }
    }
}
