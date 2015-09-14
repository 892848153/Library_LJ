package com.lj.library.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.lj.library.R;
import com.lj.library.activity.base.BaseHttpActivity;
import com.lj.library.fragment.MainFragment;
import com.lj.library.http.HttpDownloader.OnDownloadListener;
import com.lj.library.http.HttpHelper.OnHttpCallback;
import com.lj.library.http.HttpUploader.OnUploadListener;
import com.lj.library.util.LogUtil;

import java.util.Map;

public class MainActivity extends BaseHttpActivity implements OnHttpCallback,
        OnUploadListener, OnDownloadListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, new MainFragment());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }

//		Map<String, String> map = new HashMap<String, String>();
//		map.put("thread1", "10");
//		map.put("thread2", "20");
//		map.put("thread3", "30");
//		map.put("thread4", "40");
//		map.put("thread5", "50");
//
//		String path = "http://0";
//
//		DBTaskRunner dbRunner = new DBTaskRunner(getApplicationContext());
//		// dbRunner.insertDownloadProgress(0, "http://0", map);
//		dbRunner.queryDownloadProgress(0, "http://0");
//		dbRunner.queryDownloadProgress(1, "http://0");
//		dbRunner.queryDownloadProgress(2, "http://0");
//		dbRunner.queryDownloadProgress(3, "http://0");
//		dbRunner.queryDownloadProgress(4, "http://0");
//
//		dbRunner.updateDownloadProgress(0, path, map);
//		dbRunner.updateDownloadProgress(1, path, map);
//		dbRunner.updateDownloadProgress(2, path, map);
//		dbRunner.updateDownloadProgress(3, path, map);
//		dbRunner.updateDownloadProgress(4, path, map);

        // List<BasicNameValuePair> params = new
        // ArrayList<BasicNameValuePair>();
        // params.add(new BasicNameValuePair("date", "2011-11-11 11:11:11"));
        // HttpHelper httpHelper = new HttpHelper(this);
        // try {
        // Thread.sleep(2000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // for (int i = 0; i < 100; i++)
        // httpHelper
        // .post(this,
        // "http://61.144.248.2:8090/carManager/helpInfo!IconInstroduce.action",
        // params);

        // String uploadUrl =
        // "http://61.144.248.2:8090/carManager/cemployee!updateHeadPic.action";
        // HttpUploader uploader = new HttpUploader();
        // uploader.setOnUploadListener(this);
        // Map<String, String> params1 = new HashMap<String, String>();
        // params1.put("userid", "13242957698");
        // params1.put("usertype1", "1");
        // String filePath = Environment.getExternalStorageDirectory()
        // .getAbsolutePath() + File.separator + "test.png";
        // File file = new File(filePath);
        // if (file.exists()) {
        // uploader.uploadFile(this, uploadUrl, "pic", file, params1);
        // }

        // String filePath1 = Environment.getExternalStorageDirectory()
        // .getAbsolutePath() + File.separator + "JLCar";
        //
        // HttpDownloader downloader = new HttpDownloader();
        // downloader.setOnDownloadListener(this);
        // downloader
        // .downloadFile(
        // this,
        // "http://61.144.248.2:8090/carManager/html/ueditor/jsp/upload/info/2014/06/21/1meitu_21_1403335829281_720.jpg",
        // filePath1);
    }

    @Override
    public void onHttpNetworkNotFound(String path) {
        LogUtil.i(this, "onHttpNetworkNotFound  " + path);
    }

    @Override
    public void onHttpReturn(String path, int response, String result) {
        LogUtil.i(this, "onHttpReturn   " + path + "  response" + response
                + " result" + result);
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
    public void onUploadSuccess(String url, Map<String, String> files,
                                String result) {
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
        LogUtil.i(this, "onDownloadSuccess   " + url + "targetFilePath  "
                + targetFilePath);
    }

    @Override
    public void onDownloadFail(String url, String targetFilePath) {
        LogUtil.i(this, "onDownloadFail   " + url + "targetFilePath"
                + targetFilePath);
    }

    @Override
    public void onDownloadProgress(String url, String targetFilePath,
                                   long curLength, long totalLength) {
        LogUtil.i(this, "onDownloadProgress   " + url + "targetFilePath "
                + targetFilePath + "   curLength " + curLength
                + "  totalLength " + totalLength);
    }

    @Override
    public void onDownloadFileLength(String url, long totalLength) {
    }

    @Override
    public void onDownloadingStoped(String url, String targetFilePath) {
    }

}