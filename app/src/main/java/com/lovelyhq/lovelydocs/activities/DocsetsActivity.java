package com.lovelyhq.lovelydocs.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.fragments.DocsetsFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DocsetsActivity extends Activity {

    private Unbinder unbind;

    public static DocsetsActivity docsetsActivity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docsetsActivity=this;
        overridePendingTransition(R.animator.bottom_slide_in, R.animator.top_slide_out);
        setContentView(R.layout.activity_docsets);
        unbind=ButterKnife.bind(this);
        setActionBar();
        if (savedInstanceState == null) {
            placeFragment(new DocsetsFragment(), R.id.placeholder1);
        }
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.animator.top_slide_in, R.animator.bottom_slide_out);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        unbind.unbind();
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Docsets");
        }
    }

    private void placeFragment(Fragment fragment, int container) {
        getFragmentManager().beginTransaction().replace(container, fragment).commit();
    }
}
