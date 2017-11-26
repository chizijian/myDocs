package com.lovelyhq.lovelydocs.fragments;

import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.activities.DocsetActivity;
import com.lovelyhq.lovelydocs.adapters.MainListAdapter;
import com.lovelyhq.lovelydocs.adapters.MainListAdapter.StartUpdateEvent;
import com.lovelyhq.lovelydocs.dialogs.DeleteDocsetDialog;
import com.lovelyhq.lovelydocs.events.DownloadCompletedEvent;
import com.lovelyhq.lovelydocs.events.DownloadFailedEvent;
import com.lovelyhq.lovelydocs.events.DownloadStartedEvent;
import com.lovelyhq.lovelydocs.events.StartActivityEvent;
import com.lovelyhq.lovelydocs.events.StartActivityEvent.Activity;
import com.lovelyhq.lovelydocs.events.StartDialogEvent;
import com.lovelyhq.lovelydocs.helpers.DocsetsDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.DocsetsSearchHelper;
import com.lovelyhq.lovelydocs.helpers.NetworkHelper;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.helpers.XmlParser;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.services.DeleteService;
import com.lovelyhq.lovelydocs.services.DeleteService.Signal;
import com.lovelyhq.lovelydocs.services.DownloadService;

import cn.bmob.v3.BmobUser;
import de.greenrobot.event.EventBus;
import docs_app.LoginActivity;
import docs_app.bean.User;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private static final String DOCSET_VERSIONS = "docset_versions";
    private static final String DOCSET_VERSIONS_FILTERED = "docset_versions_filtered";
    private static final String SEARCH_OPENED = "search_opened";
    private static final String SEARCH_QUERY = "search_query";
    private Context mContext;
    private DocsetsDatabaseHelper mDatabaseHelper;
    private List<DocsetVersion> mDocsetVersions;
    private List<DocsetVersion> mDocsetVersionsFiltered;
    @BindView(R.id.btnDocsets)
    ImageButton mDocsetsBtn;

    @BindView(R.id.lvDocsets)
    ListView mDocsetsLv;

    @BindView(R.id.empty)
    LinearLayout mEmptyView;

    @BindView(R.id.btnLogin)
    ImageButton mBtnLogin;
    EditText mSearchEt;
    private boolean mSearchOpened;
    private String mSearchQuery;

    @BindView(R.id.btnSettings)

    ImageButton mSettingsBtn;
    private class ButtonListener implements OnClickListener {
        private Activity activity;

        ButtonListener(Activity activity) {
            this.activity = activity;
        }

        public void onClick(View view) {
            EventBus.getDefault().post(new StartActivityEvent(this.activity));
        }
    }

    private class CheckForUpdatesTask extends AsyncTask<Void, Void, Void> {
        private CheckForUpdatesTask() {
        }

        protected Void doInBackground(Void... params) {
            for (DocsetVersion docsetVersion : MainFragment.this.mDocsetVersions) {
                if (docsetVersion != null) {
                    if (docsetVersion.isLatest() && docsetVersion.getDocset() != null && docsetVersion.getDocset().getUrl() != null && docsetVersion.getDocset().getUrl().length() > 0 && XmlParser.parseVersions(docsetVersion.getDocset().getUrl()).size() > 0) {
                        String latestVersion = XmlParser.parseVersions(docsetVersion.getDocset().getUrl()).get(0);
                        if (!(latestVersion == null || latestVersion.equals(docsetVersion.getVersion()))) {
                            docsetVersion.setUpdateExists(true);
                        }
                    }
                    if (!docsetVersion.hasTarix()) {
                        docsetVersion.setUpdateExists(true);
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainFragment.this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(MainFragment.this.mDocsetVersions, MainFragment.this.mSearchQuery);
            MainFragment.this.getListAdapter().update(MainFragment.this.mDocsetVersionsFiltered);
            if (new PreferencesHelper(MainFragment.this.mContext).isUpdatesEnabled()) {
                int pos = 0;
                for (DocsetVersion dv : MainFragment.this.mDocsetVersionsFiltered) {
                    if (dv.isUpdateExists()) {
                        MainFragment.this.updateDocsetVersion(pos);
                    }
                    pos++;
                }
            }
        }
    }

    private class ListItemClickListener implements OnItemClickListener {
        private ListItemClickListener() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if ((MainFragment.this.mDocsetVersionsFiltered.get(position)).hasTarix()) {
                Intent intent = new Intent(MainFragment.this.getActivity(), DocsetActivity.class);
                intent.putExtra( "docset_version",  MainFragment.this.mDocsetVersionsFiltered.get(position));
                MainFragment.this.startActivity(intent);
                return;
            }
            Toast.makeText(MainFragment.this.mContext, MainFragment.this.mContext.getString(R.string.tarix_upgrade), Toast.LENGTH_LONG).show();
        }
    }

    private class ListItemLongClickListener implements OnItemLongClickListener {
        private ListItemLongClickListener() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
            DeleteDocsetDialog.newInstance( MainFragment.this.mDocsetVersionsFiltered.get(pos)).show(MainFragment.this.getFragmentManager(), StartDialogEvent.TAG);
            return true;
        }
    }

    private class SearchThread extends Thread {
        private String searchQuery;

        SearchThread(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public void run() {
            MainFragment.this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(MainFragment.this.mDocsetVersions, this.searchQuery);
            MainFragment.this.refreshList();
        }
    }

    private class SearchWatcher implements TextWatcher {
        private SearchWatcher() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            MainFragment.this.mSearchQuery = editable.toString();
            new SearchThread(MainFragment.this.mSearchQuery).run();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        this.mDatabaseHelper = new DocsetsDatabaseHelper(getActivity());
        if (savedInstanceState == null) {
            this.mDocsetVersions = this.mDatabaseHelper.getListOfActiveVersions();
            this.mDocsetVersionsFiltered = this.mDocsetVersions;
            this.mSearchOpened = false;
            this.mSearchQuery = "";
            return;
        }
        this.mDocsetVersions = savedInstanceState.getParcelableArrayList(DOCSET_VERSIONS);
        this.mDocsetVersionsFiltered = savedInstanceState.getParcelableArrayList(DOCSET_VERSIONS_FILTERED);
        this.mSearchOpened = savedInstanceState.getBoolean(SEARCH_OPENED);
        this.mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DOCSET_VERSIONS, (ArrayList<? extends Parcelable>) this.mDocsetVersions);
        outState.putParcelableArrayList(DOCSET_VERSIONS_FILTERED, (ArrayList<? extends Parcelable>) this.mDocsetVersionsFiltered);
        outState.putBoolean(SEARCH_OPENED, this.mSearchOpened);
        outState.putString(SEARCH_QUERY, this.mSearchQuery);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        if(BmobUser.getCurrentUser(User.class)!=null){
            Glide.with(mContext).load(BmobUser.getCurrentUser(User.class).getIcon().getUrl()).into(mBtnLogin);;
        }

        this.mDocsetsLv.setAdapter(new MainListAdapter(getActivity()));
        this.mDocsetsLv.setEmptyView(this.mEmptyView);
        this.mDocsetsLv.setOnItemClickListener(new ListItemClickListener());
        this.mDocsetsLv.setOnItemLongClickListener(new ListItemLongClickListener());
        this.mDocsetsBtn.setOnClickListener(new ButtonListener(Activity.DOCSETS));
        this.mSettingsBtn.setOnClickListener(new ButtonListener(Activity.SETTINGS));
        this.mBtnLogin.setOnClickListener(new ButtonListener(Activity.LOGIN));
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mSearchOpened) {
            openSearchBar(this.mSearchQuery);
        }
    }

    private void openSearchBar(String searchQuery) {
        this.mSearchQuery = searchQuery;
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);
        this.mSearchEt =actionBar.getCustomView().findViewById(R.id.etSearch);
        this.mSearchEt.addTextChangedListener(new SearchWatcher());
        this.mSearchEt.setText(this.mSearchQuery);
        this.mSearchEt.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(this.mSearchEt, 0);
        this.mSearchOpened = true;
    }

    private void closeSearchBar() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.mSearchEt.getWindowToken(), 0);
        getActionBar().setDisplayShowCustomEnabled(false);
        this.mSearchOpened = false;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        if (this.mDocsetVersions == null || this.mDocsetVersions.isEmpty()) {
            mSearchItem.setVisible(false);
        } else {
            mSearchItem.setVisible(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_search) {
            return super.onOptionsItemSelected(item);
        }
        if (this.mSearchOpened) {
            closeSearchBar();
        } else {
            openSearchBar(this.mSearchQuery);
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.mDatabaseHelper.close();
    }

    private MainListAdapter getListAdapter() {
        return (MainListAdapter) this.mDocsetsLv.getAdapter();
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private void refreshList() {
        getActivity().invalidateOptionsMenu();
        getListAdapter().update(this.mDocsetVersionsFiltered);
        if (NetworkHelper.isOnline(getActivity())) {
            new CheckForUpdatesTask().execute();
        }
    }

    public void onEvent(DownloadStartedEvent event) {
        this.mDocsetVersions = this.mDatabaseHelper.getListOfActiveVersions();
        this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(this.mDocsetVersions, this.mSearchQuery);
        refreshList();
    }

    public void onEvent(DownloadCompletedEvent event) {
        this.mDocsetVersions = this.mDatabaseHelper.getListOfActiveVersions();
        this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(this.mDocsetVersions, this.mSearchQuery);
        refreshList();
    }

    public void onEvent(DownloadFailedEvent event) {
        this.mDocsetVersions = this.mDatabaseHelper.getListOfActiveVersions();
        this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(this.mDocsetVersions, this.mSearchQuery);
        refreshList();
    }

    public void onEvent(Signal event) {
        if (event.isSuccess()) {
            this.mDocsetVersions = this.mDatabaseHelper.getListOfActiveVersions();
            this.mDocsetVersionsFiltered = DocsetsSearchHelper.performVersionsSearch(this.mDocsetVersions, this.mSearchQuery);
            refreshList();
        }
    }

    public void onEvent(StartUpdateEvent event) {
        updateDocsetVersion(event.getPosition());
    }

    private void updateDocsetVersion(final int position) {
        if (new PreferencesHelper(getActivity()).isMobileEnabled()) {
            sendIntentToDownloadService(true, position);
        } else if (NetworkHelper.isWifiEnabled(getActivity())) {
            sendIntentToDownloadService(false, position);
        } else {
            new Builder(getActivity()).setTitle("Warning").setMessage("未发现wifi,是否通过移动数据下载").setPositiveButton("总是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainFragment.this.sendIntentToDownloadService(true, position);
                }
            }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private void sendIntentToDownloadService(final boolean mobileEnabled, final int position) {
        new Thread(new Runnable() {
            public void run() {
                DocsetVersion docsetVersionToUpdate =  MainFragment.this.mDocsetVersionsFiltered.get(position);
                Intent deleteIntent = new Intent(MainFragment.this.mContext, DeleteService.class);
                deleteIntent.putExtra("docset_version", docsetVersionToUpdate);
                MainFragment.this.getActivity().startService(deleteIntent);
                List<String> servers = XmlParser.parseServers(docsetVersionToUpdate.getDocset().getUrl());
                List<String> versions = XmlParser.parseVersions(docsetVersionToUpdate.getDocset().getUrl());
                Intent i = new Intent(MainFragment.this.mContext, DownloadService.class);
                i.putExtra("docset", docsetVersionToUpdate.getDocset());
                i.putExtra(DownloadService.ARG_VERSION,  versions.get(0));
                i.putExtra(DownloadService.ARG_LATEST_VERSION, true);
                i.putStringArrayListExtra("servers", (ArrayList<String>) servers);
                i.putExtra(DownloadService.ARG_MOBILE_ENABLED, mobileEnabled);
                MainFragment.this.mContext.startService(i);
            }
        }).start();
    }
}
