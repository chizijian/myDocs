package com.lovelyhq.lovelydocs.activities;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.fragments.SettingsFragment;

import butterknife.BindAnim;

public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.animator.bottom_slide_in, R.animator.top_slide_out);
        setContentView(R.layout.activity_settings);
        setActionBar();
        if (savedInstanceState == null) {
            placeFragment(new SettingsFragment(), R.id.placeholder1);
        }
    }

    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Settings");
        }
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.animator.top_slide_in, R.animator.bottom_slide_out);
    }

    private void placeFragment(Fragment fragment, int container) {
        getFragmentManager().beginTransaction().replace(container, fragment).commit();
    }
}
