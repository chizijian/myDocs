package com.lovelyhq.lovelydocs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lovelyhq.lovelydocs.helpers.NetworkHelper;
import de.greenrobot.event.EventBus;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    public class Signal {
        private boolean isConnected;

        Signal(boolean isConnected) {
            this.isConnected = isConnected;
        }

        public boolean isConnected() {
            return this.isConnected;
        }
    }

    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new Signal(NetworkHelper.isOnline(context)));
    }
}
