package com.lovelyhq.lovelydocs.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.activities.SearchActivity;
import com.lovelyhq.lovelydocs.adapters.SearchListAdapter;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.helpers.ReceivedDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.SortHelper;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TypeItem;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsFragment extends Fragment {
    private static final String DOCSET_VERSION = "docset_version";
    private static final String SEARCH_QUERY = "search_query";
    private static final String SELECTED_POSITION = "selected_position";
    private static final String TYPE_ITEMS = "type_items";
    private SearchListAdapter mAdapter;
    private DocsetVersion mDocsetVersion;
    private List<TypeItem> mItems;
    @BindView(R.id.lvDocsetMenu)
    ListView mItemsLv;
    ProgressBar mLoadingBar;
    private EditText mSearchEt;
    private String mSearchQuery;
    private SearchTask mSearchTask;
    private int mSelectedPosition;

    public class SearchTask extends AsyncTask<String, Void, List<TypeItem>> {
        private Context context;
        private DocsetVersion docsetVersion;

        public SearchTask(Context context, DocsetVersion docsetVersion) {
            this.context = context;
            this.docsetVersion = docsetVersion;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            SearchResultsFragment.this.mLoadingBar.setVisibility(View.VISIBLE);
        }

        protected List<TypeItem> doInBackground(String... params) {
            return new SortHelper(new ReceivedDatabaseHelper(this.context, this.docsetVersion).performSearch(params[0]), SearchResultsFragment.this.mSearchQuery).sort();
        }

        protected void onPostExecute(List<TypeItem> result) {
            SearchResultsFragment.this.mLoadingBar.setVisibility(View.INVISIBLE);
            SearchResultsFragment.this.mAdapter.setData(result, SearchResultsFragment.this.mSearchQuery);
            SearchResultsFragment.this.mItems = result;
        }
    }

    private class SearchTextWatcher implements TextWatcher {
        private SearchTextWatcher() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            SearchResultsFragment.this.mSearchQuery = s.toString();
            SearchResultsFragment.this.mAdapter.setSelectedPosition(-1);
            if (SearchResultsFragment.this.mSearchTask != null) {
                SearchResultsFragment.this.mSearchTask.cancel(true);
            }
            if (SearchResultsFragment.this.mSearchQuery.length() > 0) {
                SearchResultsFragment.this.mSearchTask = new SearchTask(SearchResultsFragment.this.getActivity(), SearchResultsFragment.this.mDocsetVersion);
                SearchResultsFragment.this.mSearchTask.execute(new String[]{SearchResultsFragment.this.mSearchQuery});
                return;
            }
            SearchResultsFragment.this.mLoadingBar.setVisibility(View.INVISIBLE);
            SearchResultsFragment.this.mItems.clear();
            SearchResultsFragment.this.mAdapter.setData(SearchResultsFragment.this.mItems, SearchResultsFragment.this.mSearchQuery);
        }
    }

    public static SearchResultsFragment newInstance(DocsetVersion dv) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DOCSET_VERSION, dv);
        SearchResultsFragment f = new SearchResultsFragment();
        f.setArguments(bundle);
        return f;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_holder, menu);
    }

    private void setSearchBar(String searchQuery) {
        this.mSearchQuery = searchQuery;
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_bar);
        }
        EditText mSearchEt =  actionBar.getCustomView().findViewById(R.id.etSearch);
        mSearchEt.setHint("Find in docset..");
        mSearchEt.setText(this.mSearchQuery);
        mSearchEt.addTextChangedListener(new SearchTextWatcher());
        mSearchEt.requestFocus();
        this.mLoadingBar = (ProgressBar) actionBar.getCustomView().findViewById(R.id.loadingBar);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mSearchEt, 0);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            this.mItems = savedInstanceState.getParcelableArrayList(TYPE_ITEMS);
            this.mDocsetVersion =savedInstanceState.getParcelable("docset_version");
            this.mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
            this.mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
            return;
        }
        this.mItems = new ArrayList();
        this.mDocsetVersion = getArguments().getParcelable("docset_version");
        this.mSearchQuery = "";
        this.mSelectedPosition = -1;
    }

    public void onResume() {
        super.onResume();
        this.mAdapter.setSelectedPosition(this.mSelectedPosition);
    }

    public void onPause() {
        super.onPause();
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(1, 0);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TYPE_ITEMS, (ArrayList) this.mItems);
        outState.putParcelable(DOCSET_VERSION, this.mDocsetVersion);
        outState.putString(SEARCH_QUERY, this.mSearchQuery);
        outState.putInt(SELECTED_POSITION, this.mSelectedPosition);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docset_menu, container, false);
        ButterKnife.bind(this, view);
        this.mAdapter = new SearchListAdapter(getActivity());
        this.mItemsLv.setAdapter(this.mAdapter);
        this.mItemsLv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                boolean z;
                SearchActivity searchActivity = (SearchActivity) SearchResultsFragment.this.getActivity();
                Fragment newInstance = DocsetDetailsFragment.newInstance(SearchResultsFragment.this.mDocsetVersion.getPath() + "/" + SearchResultsFragment.this.mDocsetVersion.getTgzName(), SearchResultsFragment.this.mDocsetVersion.getExtractedDirectory() + "/Contents/Resources/Documents/" + ( SearchResultsFragment.this.mItems.get(position)).getPath(), SearchResultsFragment.this.mDocsetVersion);
                int i = new PreferencesHelper(SearchResultsFragment.this.getActivity()).isLargeScreen() ? R.id.placeholder2 : R.id.placeholder1;
                z = !new PreferencesHelper(SearchResultsFragment.this.getActivity()).isLargeScreen();
                searchActivity.placeFragment(newInstance, i, z);
                if (new PreferencesHelper(SearchResultsFragment.this.getActivity()).isLargeScreen()) {
                    SearchResultsFragment.this.mSelectedPosition = position;
                    SearchResultsFragment.this.mAdapter.setSelectedPosition(SearchResultsFragment.this.mSelectedPosition);
                }
            }
        });
        this.mAdapter.setData(this.mItems, this.mSearchQuery);
        setSearchBar(this.mSearchQuery);
        return view;
    }
}
