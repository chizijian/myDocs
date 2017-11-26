package com.lovelyhq.lovelydocs.events;

import android.app.DialogFragment;

public class StartDialogEvent {
    public static final String TAG = "start_dialog_fragment";
    private DialogFragment fragment;

    public StartDialogEvent(DialogFragment fragment) {
        this.fragment = fragment;
    }

    public DialogFragment getFragment() {
        return this.fragment;
    }

    public void setFragment(DialogFragment fragment) {
        this.fragment = fragment;
    }
}
