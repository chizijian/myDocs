package com.lovelyhq.lovelydocs.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.helpers.TarixDatabaseHelper;
import com.lovelyhq.lovelydocs.helpers.TarixExtractHelper;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import com.lovelyhq.lovelydocs.models.TarixItem;
import java.io.ByteArrayInputStream;

import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.io.FilenameUtils;

public class DocsetDetailsFragment extends Fragment {
    private static final String KEY_DOCSET_VERSION = "docset_version";
    private static final String KEY_HTML_PATH = "html_path";
    private static final String KEY_TGZ_PATH = "tgz_path";
    private long duration;
    private long endTime;
    @BindView(R.id.wvDetails)
    WebView mDetailsWv;


    @BindView(R.id.btnGoBack)
    CircleButton mGoBackBtn;

    private String mHtmlPath;


    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private TarixDatabaseHelper mTarixDatabaseHelper;

    private String mTgzPath;
    private long startTime;


    public static DocsetDetailsFragment newInstance(String tgzPath, String htmlPath, DocsetVersion docsetVersion) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_HTML_PATH, htmlPath);
        bundle.putString(KEY_TGZ_PATH, tgzPath);
        bundle.putParcelable(KEY_DOCSET_VERSION, docsetVersion);
        DocsetDetailsFragment fragment = new DocsetDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHtmlPath = getArguments().getString(KEY_HTML_PATH);
        this.mTgzPath = getArguments().getString(KEY_TGZ_PATH);
        DocsetVersion mDocsetVersion = (DocsetVersion) getArguments().getParcelable(KEY_DOCSET_VERSION);
        this.mTarixDatabaseHelper = new TarixDatabaseHelper(getActivity(), mDocsetVersion);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docset_details, container, false);
        ButterKnife.bind(this, view);
        this.mGoBackBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DocsetDetailsFragment.this.mDetailsWv.canGoBack()) {
                    DocsetDetailsFragment.this.mDetailsWv.goBack();
                }
            }
        });
        this.mDetailsWv.setLayerType(1, null);
        this.mDetailsWv.getSettings().setJavaScriptEnabled(true);
        this.mDetailsWv.getSettings().setDisplayZoomControls(true);
        this.mDetailsWv.getSettings().setBuiltInZoomControls(true);
        this.mDetailsWv.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                DocsetDetailsFragment.this.mProgressBar.setVisibility(View.VISIBLE);
                DocsetDetailsFragment.this.startTime = System.nanoTime();
            }

            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.startsWith("file")) {
                    url = url.replace("%20", " ");
                    if (url.contains("%3")) {
                        url = url.split("%3")[0];
                    }
                    String url2 = DocsetDetailsFragment.this.getRealURL(url);
                    String type = DocsetDetailsFragment.this.getStreamType(url2);
                    if (type != null) {
                        TarixItem tItem = DocsetDetailsFragment.this.mTarixDatabaseHelper.getEntry(url2, true);
                        String blockNum = tItem.getBlockNum();
                        String blockLength = tItem.getBlockLength();
                        String offset = tItem.getOffset();
                        if (!(blockNum == null || blockLength == null || offset == null)) {
                            return new WebResourceResponse(type, CharsetNames.UTF_8, new ByteArrayInputStream(TarixExtractHelper.unpackFileUsingTarix(DocsetDetailsFragment.this.mTgzPath, Integer.parseInt(blockNum), Integer.parseInt(blockLength), Integer.parseInt(offset))));
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DocsetDetailsFragment.this.endTime = System.nanoTime();
                DocsetDetailsFragment.this.duration = TimeUnit.MILLISECONDS.convert(DocsetDetailsFragment.this.endTime - DocsetDetailsFragment.this.startTime, TimeUnit.NANOSECONDS);
                String url2 = url.replace("file://" + FilenameUtils.getFullPath(DocsetDetailsFragment.this.mTgzPath).replace("%20", " "), "");
                DocsetDetailsFragment.this.mProgressBar.setVisibility(View.INVISIBLE);
                if (view.canGoBack()) {
                    DocsetDetailsFragment.this.mGoBackBtn.setVisibility(View.VISIBLE);
                } else {
                    DocsetDetailsFragment.this.mGoBackBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        this.mDetailsWv.loadUrl("file:///" + FilenameUtils.getPath(this.mTgzPath).replace("%20", " ") + this.mHtmlPath.replace("%20", " "));
        return view;
    }

    private String getRealURL(String url) {
        String url2 = url.replace("file://" + FilenameUtils.getFullPath(this.mTgzPath).replace("%20", " "), "");
        if (url2.contains("#")) {
            url2 = url2.substring(0, url2.indexOf("#"));
        }
        if (url2.contains("?")) {
            return url2.substring(0, url2.indexOf("?"));
        }
        return url2;
    }

    private String getStreamType(String url) {
        if (url.lastIndexOf(".") == -1) {
            return null;
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(url.substring(url.lastIndexOf(".") + 1));
    }
}
