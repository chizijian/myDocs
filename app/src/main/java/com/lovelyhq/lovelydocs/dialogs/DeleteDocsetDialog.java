package com.lovelyhq.lovelydocs.dialogs;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.services.DeleteService;

public class DeleteDocsetDialog extends DialogFragment {
    public static final String KEY_DOCSET_VERSION = "docset_version";
    private DocsetVersion mDocsetVersion;

    private class NegativeButtonListener implements OnClickListener {
        private NegativeButtonListener() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    private class PositiveButtonListener implements OnClickListener {
        private PositiveButtonListener() {
        }

        public void onClick(DialogInterface dialogInterface, int id) {
            Intent i = new Intent(DeleteDocsetDialog.this.getActivity(), DeleteService.class);
            i.putExtra(KEY_DOCSET_VERSION, DeleteDocsetDialog.this.mDocsetVersion);
            DeleteDocsetDialog.this.getActivity().startService(i);
        }
    }

    public static DialogFragment newInstance(DocsetVersion docsetVersion) {
        DeleteDocsetDialog df = new DeleteDocsetDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DOCSET_VERSION, docsetVersion);
        df.setArguments(bundle);
        return df;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mDocsetVersion =  savedInstanceState.getParcelable(KEY_DOCSET_VERSION);
        } else {
            this.mDocsetVersion =  getArguments().getParcelable(KEY_DOCSET_VERSION);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_DOCSET_VERSION, this.mDocsetVersion);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(this.mDocsetVersion.getDocset().getName() + " " + this.mDocsetVersion.getVersion()).setMessage(R.string.dialog_delete_version).setPositiveButton(R.string.dialog_yes, new PositiveButtonListener()).setNegativeButton(R.string.dialog_no, new NegativeButtonListener());
        return builder.create();
    }
}
