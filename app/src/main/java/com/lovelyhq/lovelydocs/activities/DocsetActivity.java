package com.lovelyhq.lovelydocs.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.fragments.DocsetMenuFragment;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.helpers.TarixDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.TarixExtractHelper;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TarixItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

public class DocsetActivity extends Activity {
    public static final String KEY_DOCSET_VERSION = "docset_version";
    private DocsetVersion mDocsetVersion;
    AsyncTask<Void, Integer, Boolean> mExtractTarixTask;
    AsyncTask mExtractTgzTask;
    private boolean mIsLargeScreen;
    RelativeLayout mPreparingDocsLayout;
    private EditText mSearchEt;
    private boolean mSearchOpened;
    private String mSearchQuery;
    private TarixDatabaseHelper mTarixDatabaseHelper;

    private class ExtractorTarixTask extends AsyncTask<Void, Integer, Boolean> {
        File destinationFile;
        File tarixFile;

        ExtractorTarixTask(String tarixPath, String destinationPath) {
            this.tarixFile = new File(tarixPath);
            this.destinationFile = new File(destinationPath);
        }

        protected Boolean doInBackground(Void... params) {
            try {
                try {
                    ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP).extract(this.tarixFile, this.destinationFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            } catch (IOException e2) {
                e2.printStackTrace();
                return false;
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            this.destinationFile.delete();
        }

        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                this.tarixFile.delete();
                String docsetDirectoryPath = DocsetActivity.this.mDocsetVersion.getPath();
                String docsetTgzPath = docsetDirectoryPath + File.separator + DocsetActivity.this.mDocsetVersion.getTgzName();
                mExtractTgzTask = new ExtractorTgzTask(docsetTgzPath, docsetDirectoryPath).execute();
                return;
            }
            finish();
        }
    }

    private class ExtractorTgzTask extends AsyncTask {
        File destinationFile;
        File tgzFile;

        ExtractorTgzTask(String tgzfile, String destinationFile) {
            super();
            this.tgzFile = new File(tgzfile);
            this.destinationFile = new File(destinationFile);
        }

        Boolean doInBackground() throws IOException {
            List<TarixItem> ExtractionList = DocsetActivity.this.mTarixDatabaseHelper.getExtractionList();
            if(ExtractionList.size() > 0) {

                for(int i = 0; i< ExtractionList.size(); ++i) {
                    try {

                        String path = this.destinationFile.toString() + "/" + (ExtractionList.get(i)).getPath();
                        File file = new File(path);
                        path.split("/");
                        file.getParentFile().mkdirs();
                        if(!file.exists()) {
                            file.createNewFile();
                        }

                        TarixExtractHelper.writeToFile(this.tgzFile.toString(), Integer.parseInt((ExtractionList.get(i)).getBlockNum()), Integer.parseInt((ExtractionList.get(i)).getBlockLength()), Integer.parseInt((ExtractionList.get(i)).getOffset()), path);
                    }
                    catch(Exception e) {
                        FileUtils.forceDelete(new File(this.tgzFile.getAbsolutePath().replace(".tgz", ".tar")));
                        e.printStackTrace();
                    }
                    FileUtils.forceDelete(new File(this.tgzFile.getAbsolutePath().replace(".tgz", ".tar")));
                }

                return true;
            }
            return false;
        }


        protected void onCancelled() {
            super.onCancelled();
            this.destinationFile.delete();
        }

        protected void onPostExecute(Boolean arg2) {
            super.onPostExecute(arg2);
            if(arg2) {
                DocsetActivity.this.setLayout();
            }
            else {
                DocsetActivity.this.finish();
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                return doInBackground();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Object arg1) {
            this.onPostExecute(((Boolean)arg1));
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
            DocsetActivity.this.mSearchQuery = s.toString();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.animator.right_slide_in, R.animator.left_slide_out);
        setContentView(R.layout.activity_docset);
        this.mIsLargeScreen = findViewById(R.id.placeholder2) != null;
        new PreferencesHelper(this).setLargeScreen(this.mIsLargeScreen);
        this.mPreparingDocsLayout = findViewById(R.id.layoutPreparingDocs);
        if (savedInstanceState == null) {
            this.mDocsetVersion = this.getIntent().getParcelableExtra("docset_version");
            if(mDocsetVersion!=null)
                setLayout();
            else
                Log.e("DocsetActivity", "onCreate: "+null );
        } else {
            this.mDocsetVersion = savedInstanceState.getParcelable("docset_version");
            this.mPreparingDocsLayout.setVisibility(View.INVISIBLE);
        }
        this.mTarixDatabaseHelper = new TarixDatabaseHelper(this, this.mDocsetVersion);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(this.mDocsetVersion.getDocset().getName());
            if (this.mDocsetVersion.getDocset().getActionBarIcon() != 0) {
                actionBar.setIcon(this.mDocsetVersion.getDocset().getActionBarIcon());
            }
        }
    }

    protected void onPause() {
        super.onPause();
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(1, 0);
        overridePendingTransition(R.animator.left_slide_in, R.animator.right_slide_out);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_DOCSET_VERSION, this.mDocsetVersion);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(KEY_DOCSET_VERSION, this.mDocsetVersion);
            startActivity(intent);
            return true;
        } else if (item.getItemId() != 16908332 || this.mIsLargeScreen) {
            return super.onOptionsItemSelected(item);
        } else {
            onBackPressed();
            return true;
        }
    }

    private void openSearchBar(String searchQuery) {
        this.mSearchQuery = searchQuery;
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_bar);
        }
        this.mSearchEt = (EditText) (actionBar != null ? actionBar.getCustomView().findViewById(R.id.etSearch) : null);
        if (this.mSearchEt != null) {
            this.mSearchEt.setHint("Find in docset..");
            this.mSearchEt.setText(this.mSearchQuery);
            this.mSearchEt.addTextChangedListener(new SearchTextWatcher());
            this.mSearchEt.requestFocus();
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(this.mSearchEt, 0);
            this.mSearchOpened = true;
        }
    }

    private void closeSearchBar() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.mSearchEt.getWindowToken(), 0);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
        }
        this.mSearchOpened = false;
    }

    private void setLayout() {
        showPreparingDocsetsLayout();
        String docsetDirectoryPath = this.mDocsetVersion.getPath();
        String docsetTgzPath = docsetDirectoryPath + File.separator + this.mDocsetVersion.getTgzName();
        String docsetTarixDirectoryPath = this.mDocsetVersion.getPath();
        String docsetTarixPath = docsetDirectoryPath + File.separator + this.mDocsetVersion.getTarixName();
        if (isDocsetExtracted()) {
            placeFragment(DocsetMenuFragment.newInstance(this.mDocsetVersion), R.id.placeholder1, false);
            if (this.mSearchOpened) {
                openSearchBar(this.mSearchQuery);
                return;
            }
            return;
        }
        this.mExtractTarixTask = new ExtractorTarixTask(docsetTarixPath, docsetTarixDirectoryPath).execute();
    }

    private boolean isDocsetExtracted() {
        return new File(this.mDocsetVersion.getPath() + "/" + this.mDocsetVersion.getExtractedDirectory()).exists();
    }

    private void showPreparingDocsetsLayout() {
        this.mPreparingDocsLayout.setVisibility(View.VISIBLE);
    }

    public void hidePreparingDocsetsLayout() {
        this.mPreparingDocsLayout.setVisibility(View.INVISIBLE);
    }

    public void placeFragment(Fragment fragment, int container, boolean addToBackStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (!this.mIsLargeScreen) {
            ft.setCustomAnimations(R.animator.animator_right_slide_in, R.animator.animator_left_slide_out, R.animator.animator_left_slide_in, R.animator.animator_right_slide_out);
        }
        ft.replace(container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        if (this != null && !isFinishing()) {
            ft.commit();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mExtractTgzTask != null) {
            this.mExtractTgzTask.cancel(true);
        }
        if (this.mExtractTarixTask != null) {
            this.mExtractTarixTask.cancel(true);
        }
    }
}
