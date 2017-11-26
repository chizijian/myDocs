package com.lovelyhq.lovelydocs.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.activities.DocsetActivity;
import com.lovelyhq.lovelydocs.adapters.DocsetSubmenuListAdapter;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.helpers.ReceivedDatabaseHelper;
import com.lovelyhq.lovelydocs.models.CondensedType;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TypeItem;

import java.util.ArrayList;
import java.util.List;

public class DocsetSubmenuFragment extends Fragment {
    private static final String CONDENSED_TYPE = "condensed_type";
    private static final String DOCSET_VERSION = "docset_version";
    private static final String SELECTED_POSITION = "selected_position";
    private static final String TYPE_ITEMS = "type_items";
    private DocsetSubmenuListAdapter mAdapter;
    private CondensedType mCondensedType;
    private ReceivedDatabaseHelper mDatabaseHelper;
    private DocsetVersion mDocsetVersion;
    private List<TypeItem> mItems;
    @BindView(R.id.lvDocsetMenu)
    ListView mItemsLv;
    private int mSelectedPosition;

    private class DataLoader extends AsyncTask<Void, Void, Void> {
        private DataLoader() {
        }

        protected Void doInBackground(Void... params) {
            DocsetSubmenuFragment.this.mItems = DocsetSubmenuFragment.this.mDatabaseHelper.getItemsByType(DocsetSubmenuFragment.this.mCondensedType);
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DocsetSubmenuFragment.this.mAdapter.update(DocsetSubmenuFragment.this.mItems);
            DocsetSubmenuFragment.this.mAdapter.update(DocsetSubmenuFragment.this.mSelectedPosition);
        }
    }

    public static DocsetSubmenuFragment newInstance(DocsetVersion docsetVersion, CondensedType type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("docset_version", docsetVersion);
        bundle.putParcelable(CONDENSED_TYPE, type);
        DocsetSubmenuFragment fragment = new DocsetSubmenuFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mDocsetVersion = savedInstanceState.getParcelable(DOCSET_VERSION);
            this.mCondensedType = savedInstanceState.getParcelable(CONDENSED_TYPE);
            this.mItems = savedInstanceState.getParcelableArrayList(TYPE_ITEMS);
            this.mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
        } else {
            this.mDocsetVersion = getArguments().getParcelable(DOCSET_VERSION);
            this.mCondensedType = getArguments().getParcelable(CONDENSED_TYPE);
            this.mItems = new ArrayList<>();
            this.mSelectedPosition = -1;
        }
        this.mDatabaseHelper = new ReceivedDatabaseHelper(getActivity(), this.mDocsetVersion);
        this.mAdapter = new DocsetSubmenuListAdapter(getActivity(), this.mCondensedType.getIcon());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DOCSET_VERSION, this.mDocsetVersion);
        outState.putParcelable(CONDENSED_TYPE, this.mCondensedType);
        outState.putParcelableArrayList(TYPE_ITEMS, (ArrayList<? extends Parcelable>) this.mItems);
        outState.putInt(SELECTED_POSITION, this.mSelectedPosition);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docset_menu, container, false);
        ButterKnife.bind(this, view);
        this.mItemsLv.setAdapter(this.mAdapter);
        this.mItemsLv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                boolean z;
                DocsetActivity docsetActivity = (DocsetActivity) DocsetSubmenuFragment.this.getActivity();
                Fragment newInstance = DocsetDetailsFragment.newInstance(DocsetSubmenuFragment.this.mDocsetVersion.getPath() + "/" + DocsetSubmenuFragment.this.mDocsetVersion.getTgzName(), DocsetSubmenuFragment.this.mDocsetVersion.getExtractedDirectory() + "/Contents/Resources/Documents/" + (DocsetSubmenuFragment.this.mItems.get(position)).getPath(), DocsetSubmenuFragment.this.mDocsetVersion);
                int i = new PreferencesHelper(DocsetSubmenuFragment.this.getActivity()).isLargeScreen() ? R.id.placeholder2 : R.id.placeholder1;
                z = !new PreferencesHelper(DocsetSubmenuFragment.this.getActivity()).isLargeScreen();
                docsetActivity.placeFragment(newInstance, i, z);
                if (new PreferencesHelper(DocsetSubmenuFragment.this.getActivity()).isLargeScreen()) {
                    DocsetSubmenuFragment.this.mSelectedPosition = position;
                    DocsetSubmenuFragment.this.mAdapter.update(DocsetSubmenuFragment.this.mSelectedPosition);
                }
            }
        });
        return view;
    }

    public void onResume() {
        super.onResume();
        if (this.mItems.size() == 0) {
            new DataLoader().execute();
            return;
        }
        this.mAdapter.update(this.mItems);
        this.mAdapter.update(this.mSelectedPosition);
    }
}
