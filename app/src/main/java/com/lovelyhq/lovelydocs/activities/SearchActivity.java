package com.lovelyhq.lovelydocs.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.fragments.SearchResultsFragment;
import com.lovelyhq.lovelydocs.models.DocsetVersion;


public class SearchActivity extends Activity {
    public static final String DOCSET_VERSION = "docset_version";
    private DocsetVersion mDocsetVersion;

    Unbinder unbinder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.right_slide_in, R.animator.left_slide_out);
        setContentView(R.layout.activity_search);
        unbinder=ButterKnife.bind(this);
        if (savedInstanceState != null) {
            this.mDocsetVersion = savedInstanceState.getParcelable(DOCSET_VERSION);
        } else {
            this.mDocsetVersion = getIntent().getParcelableExtra(DOCSET_VERSION);
        }
        placeFragment(SearchResultsFragment.newInstance(this.mDocsetVersion), R.id.placeholder1, false);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DOCSET_VERSION, this.mDocsetVersion);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() !=R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        onBackPressed();
        return true;
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.animator.left_slide_in, R.animator.right_slide_out);
    }

    public void placeFragment(Fragment fragment, int container, boolean addToBackStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
