package com.lovelyhq.lovelydocs.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.events.StartActivityEvent;
import com.lovelyhq.lovelydocs.fragments.MainFragment;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;
import de.greenrobot.event.EventBus;
import docs_app.LoginActivity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class MainActivity extends Activity {

    PreferencesHelper mHelper;//偏好

    @BindView(R.id.preparingLayout)
    RelativeLayout mPreparingLayout;

    Unbinder unbinder;

    Thread prepareThread = new Thread(new Runnable() {
        public void run() {
            mHelper.clearLegacyPreferences();
            File oldDir = getCacheDir().getParentFile();
            if (oldDir.exists()) {
                try {
                    FileUtils.cleanDirectory(oldDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mHelper.setFirstRun(false);
            runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.setLayout();
                }
            });
        }
    });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        Bmob.initialize(this, "8afbcaae22fc8ee6739956da9cbc2d7a");
        BmobUpdateAgent.initAppVersion();

        // Bmob.initialize(this,"455eb6562cb6c6cf44d1f34f46baaaee");


        if (savedInstanceState == null) {
            setLayout();
        }
        EventBus.getDefault().register(this);
    }

    private void setLayout() {
        mHelper = new PreferencesHelper(this);
        if (mHelper.isFirstRun()) {
            mPreparingLayout.setVisibility(View.VISIBLE);
            prepareThread.start();
            return;
        }
        mPreparingLayout.setVisibility(View.INVISIBLE);
        placeFragment(new MainFragment(), R.id.placeholder1);
    }

    protected void onDestroy() {
        super.onDestroy();
        prepareThread.interrupt();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void placeFragment(Fragment fragment, int container) {
        getFragmentManager().beginTransaction().replace(container, fragment).commitAllowingStateLoss();
    }

    public void onEvent(StartActivityEvent event) {
        Intent intent;
        switch (event.getActivity()) {
            case DOCSET:
                intent = new Intent(this, DocsetActivity.class);
                break;
            case DOCSETS:
                intent = new Intent(this, DocsetsActivity.class);
                break;
            case SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case LOGIN:
                intent = new Intent(this, LoginActivity.class);
                break;
            default:
                intent = new Intent();
                break;
        }
        startActivity(intent);
    }
}
