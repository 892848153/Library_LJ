package com.lj.library.http.apache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.lj.library.constants.ExecutorHolder;
import com.lj.library.http.common.NetworkChecker;
import com.lj.library.util.LogUtil;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HttpHelper {

    private OnHttpCallback mOnHttpCallback;

    /**
     * 响应成功
     */
    private static final String CHARSET = HTTP.UTF_8;
    public static final int HTTP_GET = 0x00;
    public static final int HTTP_POST = 0x01;

    public HttpHelper() {
    }

    public HttpHelper(OnHttpCallback callback) {
        mOnHttpCallback = callback;
    }

    /**
     * @param context
     * @param path    请求的URL
     * @param params  参数
     */
    public void get(Context context, final String path,
                    final List<BasicNameValuePair> params) {
        doRequest(context, path, params, HTTP_GET);
    }

    /**
     * @param context
     * @param path    请求的URL
     * @param params  参数
     */
    public void post(Context context, final String path,
                     final List<BasicNameValuePair> params) {
        doRequest(context, path, params, HTTP_POST);
    }

    /**
     * 发起网络请求.
     *
     * @param context
     * @param path
     * @param params
     * @param requestFlag 网络请求方式:{@link #HTTP_GET}, {@link #HTTP_POST},默认是
     *                    {@link #HTTP_GET}
     */
    public void doRequest(Context context, final String path,
                          final List<BasicNameValuePair> params, int requestFlag) {
        if (!NetworkChecker.isNetworkAvailable(context)) {
            if (mOnHttpCallback != null) {
                mOnHttpCallback.onHttpNetworkNotFound(path);
            }
            return;
        }

        sendRequest(context, path, params, requestFlag);
    }

    /**
     * android3.0以前，{@link AsyncTask}是最少5个线程并发执行的，<br/>
     * 从3.0开始，改成串行执行了 . 不过网络是Wifi或者3G模式的话，并发模式比较好. <br/>
     * 此方法会根据网络状况自动决定采用并发还是串行执行.
     *
     * @param path
     * @param params
     * @param requestFlag
     */
    @SuppressLint("InlinedApi")
    private void sendRequest(Context context, String path,
                             List<BasicNameValuePair> params, int requestFlag) {
        NetworkAsynTask task = new NetworkAsynTask(path, params, requestFlag);
        if (Build.VERSION.SDK_INT < 11) {
            task.execute();
        } else {
            NetworkChecker.NetworkType type = NetworkChecker.getNetworkType(context);
            switch (type) {
                case NETWORK_TYPE_3G:
                case NETWORK_TYPE_WIFI:
                    task.executeOnExecutor(ExecutorHolder.THREAD_POOL_EXECUTOR,
                            new Void[]{});
                    break;
                default:
                    task.execute();
                    break;
            }
        }
    }

    private class NetworkAsynTask extends AsyncTask<Void, Void, String> {

        private final String mPath;

        private final List<BasicNameValuePair> mParams;

        private final int mRequestFlag;

        private final HttpResponseWrapper mResponseWrapper;

        public NetworkAsynTask(String path, List<BasicNameValuePair> params,
                               int requestFlag) {
            mPath = path;
            mParams = params;
            mRequestFlag = requestFlag;
            mResponseWrapper = new HttpResponseWrapper();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient client = HttpAssistance.getDefaultHttpClient();
            String result = null;
            try {
                result = performRequestHttp(client);
            } catch (Exception e) {
                e.printStackTrace();
                if (mOnHttpCallback != null) {
                    mOnHttpCallback.onHttpError(mPath, e);
                }
            } finally {
                HttpAssistance.shutdown(client);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mOnHttpCallback != null) {
                mOnHttpCallback.onHttpReturn(mPath, mResponseWrapper.response, result);
                if (mResponseWrapper.response == HttpStatus.SC_OK) {
                    if (TextUtils.isEmpty(result)) {
                        mOnHttpCallback.onHttpNothingReturn(mPath);
                    } else {
                        mOnHttpCallback.onHttpSuccess(mPath, result);
                    }
                } else {
                    mOnHttpCallback.onHttpError(mPath, mResponseWrapper.response);
                }
            }
        }

        private String performRequestHttp(HttpClient client)
                throws IOException {
            if (TextUtils.isEmpty(mPath)) {
                throw new NullPointerException("url is empty");
            }

            String result = null;
            if (HTTP_GET == mRequestFlag) {
                String url = buildGetUrl();
                LogUtil.e(this, "HTTP URL------->" + url);
                HttpGet request = new HttpGet(url);
                result = HttpAssistance.executeRequest(client, request,
                        mResponseWrapper);
            } else {
                HttpPost request = new HttpPost(mPath);
                buildPostParams(request);
                LogUtil.e(this,
                        "HTTP URL------->" + mPath + mParams == null ? ""
                                : mParams.toString());
                result = HttpAssistance.executeRequest(client, request,
                        mResponseWrapper);
            }
            return result;
        }

        private String buildGetUrl() {
            if (TextUtils.isEmpty(mPath)) {
                throw new NullPointerException("url is empty");
            }

            String pathWithParams = mPath;
            if (mParams != null && mParams.size() > 0) {
                pathWithParams = mPath + "?"
                        + URLEncodedUtils.format(mParams, HTTP.UTF_8);
            }
            return pathWithParams;
        }

        private void buildPostParams(HttpPost request)
                throws UnsupportedEncodingException {
            if (mParams != null && mParams.size() > 0) {
                request.setEntity(new UrlEncodedFormEntity(mParams, CHARSET));
            }
        }
    }

    public void setOnHttpCallback(OnHttpCallback onHttpResponse) {
        if (mOnHttpCallback == null || onHttpResponse != mOnHttpCallback) {
            mOnHttpCallback = onHttpResponse;
        }
    }

    public interface OnHttpCallback {
        /***************** 使用方法：在BaseHttpActivity中实现此接口，可在子类中选择需要复写的方法 ****************/

        /**
         * 未检测到网络，运行在主线程.
         *
         * @param path 请求的url
         */
        void onHttpNetworkNotFound(String path);

        /**
         * 网络请求返回调用此接口, 运行在主线程.
         *
         * @param path     请求的url
         * @param response 返回码
         * @param result   返回的结果
         */
        void onHttpReturn(String path, int response, String result);

        /**
         * 网络请求抛出异常会调用此接口,运行在子线程.
         *
         * @param path      请求的URL
         * @param exception 捕获的异常
         */
        void onHttpError(String path, Exception exception);

        /**
         * 网络请求出错会调用此接口, 运行在子线程.
         *
         * @param path     请求的url
         * @param response 返回码
         */
        void onHttpError(String path, int response);

        /**
         * 网络请求成功，但没有返回结果会调用此接口,运行在主线程.
         *
         * @param path
         */
        void onHttpNothingReturn(String path);

        /**
         * 网络请求成功并返回结果会调用此接口,运行在主线程.
         *
         * @param path   请求的url
         * @param result 返回的结果
         */
        void onHttpSuccess(String path, String result);
    }
}
