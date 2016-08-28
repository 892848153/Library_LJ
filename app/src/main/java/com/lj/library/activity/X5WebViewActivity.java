package com.lj.library.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.lj.library.R;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 腾讯X5内核WebView.<p/>
 *
 * <a href="http://x5.tencent.com/doc?id=1004">官方文档</a><br/>
 *
 * Created by liujie_gyh on 16/8/27.
 */

public class X5WebViewActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QbSdk.allowThirdPartyAppDownload(true);
        setContentView(R.layout.x5_webview_activity);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings setting = mWebView.getSettings();
        setting.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setting.setAllowContentAccess(true);
        }
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
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB) {
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

            //=========HTML5定位==========================================================
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
                callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
                super.onGeolocationPermissionsShowPrompt(origin, callback);
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        mWebView.loadUrl("http://www.baidu.com");
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
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

            ((ViewGroup)mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroy();
    }
}
