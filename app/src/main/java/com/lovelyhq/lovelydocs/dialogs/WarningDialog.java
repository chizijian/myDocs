package com.lovelyhq.lovelydocs.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.models.Docset;

import butterknife.ButterKnife;

/**
 * Created by HASEE on 2017/10/24.
 */

public class WarningDialog extends DialogFragment {

    private Context mContext;


    public static WarningDialog newInstance() {
        WarningDialog dialog=new WarningDialog();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity()).setTitle("Warning").setMessage("").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_warning, null, false);
        ButterKnife.bind(this, customView);
        builder.setView(customView);
        return builder.create();
    }
}
