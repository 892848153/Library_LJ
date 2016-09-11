package com.lj.library.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.lj.library.R;
import com.lj.library.activity.base.BaseHttpActivity;
import com.lj.library.fragment.MainFragment;
import com.lj.library.http.apache.HttpDownloader.OnDownloadListener;
import com.lj.library.http.apache.HttpUploader.OnUploadListener;
import com.lj.library.util.LogUtil;

import java.util.Map;

public class MainActivity extends BaseHttpActivity implements OnUploadListener, OnDownloadListener, NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            mNavigationView = (NavigationView) findViewById(R.id.nav_view);
            mNavigationView.setNavigationItemSelectedListener(this);
            mNavigationView.setCheckedItem(R.id.nav_home);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, new MainFragment());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHttpNetworkNotFound(String path) {
        LogUtil.i(this, "onHttpNetworkNotFound  " + path);
    }

    @Override
    public void onHttpReturn(String path, int response, String result) {
        LogUtil.i(this, "onHttpReturn   " + path + "  response" + response + " result" + result);
    }

    @Override
    public void onHttpError(String path, Exception exception) {
        LogUtil.i(this, "onHttpError   " + path);
    }

    @Override
    public void onHttpError(String path, int response) {
        LogUtil.i(this, "onHttpError   " + path + " response" + response);
    }

    @Override
    public void onHttpNothingReturn(String path) {
        LogUtil.i(this, "onHttpNothingReturn   " + path);
    }

    @Override
    public void onHttpSuccess(String path, String result) {
        // LogUtil.i(this, "onHttpSuccess   " + path + "  result" + result);
    }

    @Override
    public void onUploadError(String url, Exception e) {
        LogUtil.i(this, "onUploadError   " + url);
    }

    @Override
    public void onNetworkNotFound(String url) {
        LogUtil.i(this, "onNetworkNotFound   " + url);
    }

    @Override
    public void onUploadProgress(long currentLength, long totalLength) {
        // LogUtil.i(this, "onHttpSuccess" + path + "  result" + result);
    }

    @Override
    public void onUploadSuccess(String url, Map<String, String> files, String result) {
        LogUtil.i(this, "onUploadSuccess   " + url + "  result" + result);
    }

    @Override
    public void onUploadFail(String url, Map<String, String> files, int response) {
        LogUtil.i(this, "onUploadFail   " + url + " response:" + response);
    }

    @Override
    public boolean onDownloadFileExist(String url, String filePath) {
        return true;
    }

    @Override
    public void onDownloadError(String url, Exception e) {
        LogUtil.i(this, "onDownloadError   " + url);
    }

    @Override
    public void onDownloadSuccess(String url, String targetFilePath) {
        LogUtil.i(this, "onDownloadSuccess   " + url + "targetFilePath  " + targetFilePath);
    }

    @Override
    public void onDownloadFail(String url, String targetFilePath) {
        LogUtil.i(this, "onDownloadFail   " + url + "targetFilePath" + targetFilePath);
    }

    @Override
    public void onDownloadProgress(String url, String targetFilePath, long curLength, long totalLength) {
        LogUtil.i(this, "onDownloadProgress   " + url + "targetFilePath "
                + targetFilePath + "   curLength " + curLength + "  totalLength " + totalLength);
    }

    @Override
    public void onDownloadFileLength(String url, long totalLength) {
    }

    @Override
    public void onDownloadingStoped(String url, String targetFilePath) {
    }


}
