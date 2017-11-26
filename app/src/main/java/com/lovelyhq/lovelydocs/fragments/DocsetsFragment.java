package com.lovelyhq.lovelydocs.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.adapters.DocsetsListAdapter;
import com.lovelyhq.lovelydocs.events.DownloadCompletedEvent;
import com.lovelyhq.lovelydocs.events.DownloadFailedEvent;
import com.lovelyhq.lovelydocs.events.DownloadStartedEvent;
import com.lovelyhq.lovelydocs.events.StartDialogEvent;
import com.lovelyhq.lovelydocs.helpers.DocsetsDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.DocsetsSearchHelper;
import com.lovelyhq.lovelydocs.helpers.NetworkHelper;
import com.lovelyhq.lovelydocs.models.Docset;
import com.lovelyhq.lovelydocs.receivers.ConnectivityChangeReceiver.Signal;

import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import java.util.ArrayList;
import java.util.List;

public class DocsetsFragment extends Fragment {
    public static final String KEY_DOCSETS = "docsets";
    private static final String KEY_DOCSETS_FILTERED = "docsets_filtered";
    private static final String KEY_SEARCH_OPENED = "search_opened";
    private static final String KEY_SEARCH_QUERY = "search_query";
    private DocsetsDatabaseHelper mDatabaseHelper;
    private List<Docset> mDocsets;
    private List<Docset> mDocsetsFiltered;
    @BindView(R.id.lvDocsets)
    ListView mDocsetsLv;
    @BindView(R.id.tvNoInternet)
    TextView mNoInternetTv;
    EditText mSearchEt;
    private boolean mSearchOpened;
    private String mSearchQuery;

    Unbinder unbinder;

    private class SearchThread extends Thread {
        private String searchQuery;

        public SearchThread(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public void run() {
            DocsetsFragment.this.mDocsetsFiltered = DocsetsSearchHelper.performSearch(DocsetsFragment.this.mDocsets, this.searchQuery);
            DocsetsFragment.this.updateList(DocsetsFragment.this.mDocsetsFiltered);
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
            DocsetsFragment.this.mSearchQuery = editable.toString();
            new SearchThread(DocsetsFragment.this.mSearchQuery).run();
        }
    }

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setHasOptionsMenu(true);
        this.mDatabaseHelper = new DocsetsDatabaseHelper(getActivity());
        EventBus.getDefault().register(this);
        if (savedState != null) {
            this.mDocsets = savedState.getParcelableArrayList(KEY_DOCSETS);
            this.mDocsetsFiltered = savedState.getParcelableArrayList(KEY_DOCSETS_FILTERED);
            this.mSearchOpened = savedState.getBoolean(KEY_SEARCH_OPENED);
            this.mSearchQuery = savedState.getString(KEY_SEARCH_QUERY);
            return;
        }
        this.mDocsets = this.mDatabaseHelper.getListOfAllDocsets();
        this.mDocsetsFiltered = this.mDocsets;
        this.mSearchOpened = false;
        this.mSearchQuery = "";
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_DOCSETS, (ArrayList) this.mDocsets);
        outState.putParcelableArrayList(KEY_DOCSETS_FILTERED, (ArrayList) this.mDocsetsFiltered);
        outState.putBoolean(KEY_SEARCH_OPENED, this.mSearchOpened);
        outState.putString(KEY_SEARCH_QUERY, this.mSearchQuery);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_docsets, container, false);
        unbinder=ButterKnife.bind(this, view);
        this.mDocsetsLv.setAdapter(new DocsetsListAdapter(this.mDocsetsFiltered, getActivity()));
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.mSearchOpened) {
            openSearchBar(this.mSearchQuery);
        }
    }

    public void onResume() {
        super.onResume();
        handleNoInternetWarning(NetworkHelper.isOnline(getActivity()));
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
        unbinder.unbind();
    }

    private void openSearchBar(String searchQuery) {
        this.mSearchQuery = searchQuery;
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);
        this.mSearchEt = actionBar.getCustomView().findViewById(R.id.etSearch);
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

    private void handleNoInternetWarning(boolean isConnected) {
        if (isConnected) {
            this.mNoInternetTv.setVisibility(View.INVISIBLE);
        } else {
            this.mNoInternetTv.setVisibility(View.VISIBLE);
        }
        getListAdapter().setAllItemsEnabled(isConnected);
    }

    private void updateList(List<Docset> docsets) {
        ((DocsetsListAdapter) this.mDocsetsLv.getAdapter()).update(docsets);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    private DocsetsListAdapter getListAdapter() {
        return (DocsetsListAdapter) this.mDocsetsLv.getAdapter();
    }

    public void onEvent(StartDialogEvent event) {
        event.getFragment().show(getFragmentManager(), StartDialogEvent.TAG);
    }

    public void onEvent(Signal signal) {
        handleNoInternetWarning(signal.isConnected());
    }

    public void onEvent(DownloadStartedEvent event) {
        refreshList();
    }

    public void onEvent(DownloadCompletedEvent event) {
        refreshList();
    }

    public void onEvent(DownloadFailedEvent event) {
        refreshList();
    }

    private void refreshList() {
        this.mDocsets = this.mDatabaseHelper.getListOfAllDocsets();
        new SearchThread(this.mSearchQuery).run();
    }
}
