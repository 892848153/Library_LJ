package com.lj.library.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.http.common.NetworkChecker;
import com.lj.library.util.LogUtil;
import com.lj.library.util.Logger;

/**
 * WebView事例.
 */
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout mContentLayout;

    private ProgressBar mProgressBar;

    private WebView mWebView;

    private View mErrorView;

    private String mUrl;

    private LoadingUrlManager mLoadingUrlManager;

    private final LoadingUrlManager INITIAL_LOADING_MANAGER = new InitialLoadingUrlManager();

    private final LoadingUrlManager RELOADING_MANAGER = new ReloadingUrlManager();

    public static final String KEY_URL = "keyUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.x5_webview_activity);

        mUrl = getIntent().getStringExtra(KEY_URL);
        initWebView();
        mLoadingUrlManager = INITIAL_LOADING_MANAGER;
        mLoadingUrlManager.loadUrl();
    }

    private void initWebView() {
        mContentLayout = (LinearLayout) findViewById(R.id.web_content_llyt);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings setting = mWebView.getSettings();
        setting.setAllowFileAccess(true);
        setting.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setting.setAllowFileAccessFromFileURLs(true);
            setting.setAllowUniversalAccessFromFileURLs(true);
        }

        // 设置是否自动加载图片,包括本地图片,网络图片等,默认true.
        setting.setLoadsImagesAutomatically(true);
        // 设置是否不加载网络图片,只有上面设置为true时,此属性才生效, 默认false
        setting.setBlockNetworkImage(false);
        // 设置是否不加载网络资源, 有网络权限时,默认false, 无网络权限,默认true
        setting.setBlockNetworkLoads(false);
        // 设置是否开启build-in zoom机制, 开启后会在webView上有个缩放按钮,也支持手势缩放,默认false
        setting.setBuiltInZoomControls(true);
        // 设置是否支持缩放按钮和手势缩放网页, 默认是true
        setting.setSupportZoom(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 设置在build-in zoom 机制下,是否显示缩放按钮,默认是true
            setting.setDisplayZoomControls(false);
        }
        // 设置是否开启overView模式, 开启这个模式的情况下, 当网页本身的宽比手机屏幕要宽时,
        // 就会缩小网页来适配手机屏幕. 当getUseWideViewPort为true时, 该值默认false
        setting.setLoadWithOverviewMode(true);
        // 设置是否支持<viewPort>标签, 当为false时,网页的宽为CSS指定的,跟设备无关。
        // 当为true时, 如果网页中有<viewport>标签并且有指定值,则网页的宽使用该值,默认提供一个默认的<viewport>
        setting.setUseWideViewPort(true);

        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启 database storage API功能
        setting.setDatabaseEnabled(true);
        // 开启 DOM storage API 功能
        setting.setDomStorageEnabled(true);
        // 开启Application Caches功能
        setting.setAppCacheEnabled(true);
        setting.setAppCachePath(getFilesDir().getAbsolutePath() + "app_cache");
        // 支持定位
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        setting.setGeolocationEnabled(true);
        setting.setGeolocationDatabasePath(dir);
//        setting.setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
        // 支持JS
        setting.setJavaScriptEnabled(true);
        // 支持插件
        setting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // 当webview调用requestFocus时为webview设置节点
        setting.setNeedInitialFocus(true);
        // 拦截保存表单数据
        setting.setSaveFormData(true);
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        setting.setSupportMultipleWindows(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtil.d(WebViewActivity.this, "ProgressChanged++  " + newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            //=========HTML5定位==========================================================
            @Override
            public void onGeolocationPermissionsShowPrompt(final String s, final GeolocationPermissions.Callback geolocationPermissionsCallback) {
                geolocationPermissionsCallback.invoke(s, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
            }

            //=========多窗口的问题==========================================================
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(mWebView);
                resultMsg.sendToTarget();
                return true;
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.e("onReceivedError   errorCode:" + errorCode + "  desc:" + description);
                mLoadingUrlManager = RELOADING_MANAGER;
                showErrorView(mLoadingUrlManager.getLoadingErrorPrompt());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.i("shouldOverrideUrlLoading:" + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.i("onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.i("onPageFinished");
            }

        });
    }

    private void initErrorView(@StringRes int errorTipId) {
        if (mErrorView == null) {
            mErrorView = View.inflate(this, R.layout.include_loading_error_layout, null);
            mErrorView.findViewById(R.id.retry_btn).setOnClickListener(this);
        }

        TextView tv = (TextView) mErrorView.findViewById(R.id.prompt_tv);
        tv.setText(errorTipId);
    }

    @Override
    public void onClick(final View v) {
        if (mLoadingUrlManager != null) {
            mLoadingUrlManager.loadUrlIfNetworkAvailable();
        }
    }

    private void showWebView() {
        Logger.i("----------------  show WebView");
        if (mContentLayout.getChildCount() > 1) {
            mContentLayout.removeViewAt(1);
        }

        mContentLayout.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void showErrorView(@StringRes int errorTipId) {
        Logger.i("----------------  showErrorView");
        if (mContentLayout.getChildCount() > 1) {
            mContentLayout.removeViewAt(1);
        }
        initErrorView(errorTipId);
        mContentLayout.addView(mErrorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 避免JS一直在后台耗电
        mWebView.getSettings().setJavaScriptEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    public void updateCookies(String url, String value) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) { // 2.3及以下
            CookieSyncManager.createInstance(getApplicationContext());
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, value);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            CookieSyncManager.getInstance().sync();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            showWebView();
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroy();
    }

    interface LoadingUrlManager {

        int getNoNetworkErrorPrompt();

        int getLoadingErrorPrompt();

        void loadUrl();

        void loadUrlIfNetworkAvailable();

    }

    private abstract class BaseLoadingUrlManager implements LoadingUrlManager {

        @Override
        public int getNoNetworkErrorPrompt() {
            return R.string.disconnected_from_network;
        }

        @Override
        public int getLoadingErrorPrompt() {
            return R.string.common_tip_load_url_error;
        }

        @Override
        public void loadUrlIfNetworkAvailable() {
            if (NetworkChecker.isNetworkAvailable(WebViewActivity.this)) {
                showWebView();
                loadUrl();
            } else {
                Logger.i("show no network error view");
                showErrorView(mLoadingUrlManager.getNoNetworkErrorPrompt());
            }
        }
    }

    private class InitialLoadingUrlManager extends BaseLoadingUrlManager {

        @Override
        public void loadUrl() {
            Logger.i("InitialLoadingUrlManager.loadUrl()");
            mWebView.loadUrl(TextUtils.isEmpty(mUrl) ? "http://www.baidu.com" : mUrl);
        }
    }

    private class ReloadingUrlManager extends BaseLoadingUrlManager {

        @Override
        public void loadUrl() {
            Logger.i("ReloadingUrlManager.loadUrl()");
            mWebView.reload();
        }
    }
}
