package com.lovelyhq.lovelydocs.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.adapters.VersionsSpinnerAdapter;

import com.lovelyhq.lovelydocs.helpers.DocsetsDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.NetworkHelper;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.helpers.XmlParser;
import com.lovelyhq.lovelydocs.models.Docset;
import com.lovelyhq.lovelydocs.receivers.ConnectivityChangeReceiver;
import com.lovelyhq.lovelydocs.services.DownloadService;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class AddDocsetDialog extends DialogFragment {
    public static final String KEY_DOCSET = "docset";
    public static final String KEY_SELECTED_VERSION = "selected_version";
    public static final String KEY_SERVERS = "servers";
    public static final String KEY_VERSIONS = "versions";
    public static final String TAG = "add_docset_dialog";
    private Context mContext;

    private DocsetsDatabaseHelper mDbHelper;

    private AlertDialog mDialog;
    private Docset mDocset;

    @BindView(R.id.layoutFetchingVersions)
    LinearLayout mFetchingVersionsLayout;

    private int mSelectedVersion;
    private List<String> mServers = new ArrayList<>();
    private List<String> mVersions = new ArrayList<>();
    @BindView(R.id.spinVersions)
    Spinner mVersionsSpinner;

    private class DialogOnShowListener implements OnShowListener {
        private DialogOnShowListener() {
        }

        public void onShow(DialogInterface dialogInterface) {
            VersionsSpinnerAdapter adapter = new VersionsSpinnerAdapter(AddDocsetDialog.this.mContext, simple_spinner_item, AddDocsetDialog.this.mVersions, AddDocsetDialog.this.mDocset.getName());
            Log.e(TAG, "onShow: " + 1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            AddDocsetDialog.this.mVersionsSpinner.setAdapter(adapter);
            if (AddDocsetDialog.this.mVersions.isEmpty()) {
                AddDocsetDialog.this.mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                new XmlParseTask().execute(new String[]{AddDocsetDialog.this.mDocset.getUrl()});
                return;
            }
            AddDocsetDialog.this.showSpinner(adapter.getFirstEnabledPosition());
            AddDocsetDialog.this.mVersionsSpinner.setSelection(AddDocsetDialog.this.mSelectedVersion);
            if (NetworkHelper.isOnline(AddDocsetDialog.this.mContext)) {
                AddDocsetDialog.this.mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            }
        }
    }

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
            if (new PreferencesHelper(AddDocsetDialog.this.getActivity()).isMobileEnabled()) {
                AddDocsetDialog.this.sendIntentToDownloadService(true);
            } else if (NetworkHelper.isWifiEnabled(AddDocsetDialog.this.getActivity())) {
                AddDocsetDialog.this.sendIntentToDownloadService(false);
            } else {
                new Builder(mContext).setTitle("Warning").setMessage("是否通过移动数据下载？").setPositiveButton("总是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AddDocsetDialog.this.sendIntentToDownloadService(true);
                        Log.e(TAG, "onClick: " + "移动数据");
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        }
    }

    private class SpinnerListener implements OnItemSelectedListener {
        private SpinnerListener() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            AddDocsetDialog.this.mSelectedVersion = pos;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private class XmlParseTask extends AsyncTask<String, Void, List<String>> {
        private XmlParseTask() {
        }

        protected List<String> doInBackground(String... strings) {
            String docsetUlr = strings[0];
            AddDocsetDialog.this.mServers = XmlParser.parseServers(docsetUlr);
            return XmlParser.parseVersions(docsetUlr);
        }

        protected void onPostExecute(List<String> strings) {
            AddDocsetDialog.this.mVersions = strings;
            VersionsSpinnerAdapter adapter = new VersionsSpinnerAdapter(AddDocsetDialog.this.mContext, simple_spinner_item, AddDocsetDialog.this.mVersions, AddDocsetDialog.this.mDocset.getName());
            adapter.setDropDownViewResource(R.layout.verions_spinner_dropdown_item);
            AddDocsetDialog.this.mVersionsSpinner.setAdapter(adapter);
            if (AddDocsetDialog.this.mVersions.isEmpty()) {
                AddDocsetDialog.this.hideSpinner();
                AddDocsetDialog.this.mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                return;
            }
            AddDocsetDialog.this.showSpinner(adapter.getFirstEnabledPosition());
            AddDocsetDialog.this.mDialog.getButton(-1).setEnabled(AddDocsetDialog.this.mVersionsSpinner.isEnabled());
        }
    }

    public static AddDocsetDialog newInstance(Docset docset) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("docset", docset);
        AddDocsetDialog dialog = new AddDocsetDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        EventBus.getDefault().register(this);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDbHelper = new DocsetsDatabaseHelper(this.mContext);
        if (savedInstanceState != null) {
            this.mDocset = savedInstanceState.getParcelable("docset");
            this.mVersions = savedInstanceState.getStringArrayList(KEY_VERSIONS);

            this.mSelectedVersion = savedInstanceState.getInt(KEY_SELECTED_VERSION);
            this.mServers = savedInstanceState.getStringArrayList("servers");
            return;
        }
        this.mDocset = getArguments().getParcelable("docset");
        this.mVersions = new ArrayList<>(0);
        this.mSelectedVersion = 0;
        this.mServers = new ArrayList<>(0);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("docset", this.mDocset);
        outState.putStringArrayList(KEY_VERSIONS, (ArrayList<String>) this.mVersions);
        outState.putInt(KEY_SELECTED_VERSION, this.mSelectedVersion);
        outState.putStringArrayList("servers", (ArrayList<String>) this.mServers);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(this.mDocset.getName()).setMessage(R.string.dialog_add_message).setPositiveButton(R.string.dialog_yes, new PositiveButtonListener()).setNegativeButton(R.string.dialog_no, new NegativeButtonListener());
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_docset, null, false);
        ButterKnife.bind(this, customView);
        this.mVersionsSpinner.setOnItemSelectedListener(new SpinnerListener());
        hideSpinner();
        builder.setView(customView);
        this.mDialog = builder.create();
        this.mDialog.setOnShowListener(new DialogOnShowListener());
        return this.mDialog;
    }

    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    private void sendIntentToDownloadService(final boolean mobileEnabled) {
        PermissionListener permissionlistener = new PermissionListener() {
            public void onPermissionGranted() {
                Intent i = new Intent(AddDocsetDialog.this.mContext, DownloadService.class);
                i.putExtra("docset", AddDocsetDialog.this.mDocset);
                i.putExtra(DownloadService.ARG_VERSION, AddDocsetDialog.this.mVersions.get(AddDocsetDialog.this.mSelectedVersion));
                i.putExtra(DownloadService.ARG_LATEST_VERSION, AddDocsetDialog.this.mSelectedVersion == 0);
                i.putStringArrayListExtra("servers", (ArrayList<String>) AddDocsetDialog.this.mServers);
                i.putExtra(DownloadService.ARG_MOBILE_ENABLED, mobileEnabled);
                AddDocsetDialog.this.mContext.startService(i);
            }

            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                if (AddDocsetDialog.this.getActivity() != null) {
                    Toast.makeText(AddDocsetDialog.this.getActivity(), "No new docets for you until you give the app proper permissions\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (getActivity() != null) {
            TedPermission.with(getActivity()).setPermissionListener(permissionlistener).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]").setPermissions("android.permission.WRITE_EXTERNAL_STORAGE").check();
        }
    }

    private void showSpinner(int selectedPosition) {
        if (selectedPosition != -1) {
            this.mVersionsSpinner.setEnabled(true);
            this.mVersionsSpinner.setSelection(selectedPosition);
        } else {
            this.mVersionsSpinner.setEnabled(false);
        }
        this.mVersionsSpinner.setVisibility(View.VISIBLE);
        this.mFetchingVersionsLayout.setVisibility(View.INVISIBLE);
    }

    private void hideSpinner() {
        this.mVersionsSpinner.setVisibility(View.INVISIBLE);
        this.mFetchingVersionsLayout.setVisibility(View.VISIBLE);
    }

    public void onEvent(ConnectivityChangeReceiver.Signal signal) {
        if (signal.isConnected()) {
            this.mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(this.mVersionsSpinner.isEnabled());
        } else {
            this.mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
