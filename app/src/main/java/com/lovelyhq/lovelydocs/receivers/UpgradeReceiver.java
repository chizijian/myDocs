package com.lovelyhq.lovelydocs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.lovelyhq.lovelydocs.helpers.DocsetsDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.InitialDocsetsGenerator;

public class UpgradeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        DocsetsDatabaseHelper.updateIconsOfDocsets(InitialDocsetsGenerator.getFullList(), SQLiteDatabase.openDatabase(context.getApplicationContext().getDatabasePath("DocsetsDatabase").getAbsolutePath(), null, 0));
    }
}
