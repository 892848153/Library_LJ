package com.lj.library.http;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.lj.library.constants.ExecutorHolder;
import com.lj.library.http.NetworkChecker.NetworkType;

public class HttpHelper {

	/** 响应成功 */
	public static final int RESPONSE_SUCCESS = 200;
	private static final String CHARSET = "UTF-8";
	private static final int DEFAULT_CONNECTION_TIMEOUT = (20 * 1000); // milliseconds
	private static final int DEFAULT_SOCKET_TIMEOUT = (20 * 1000); // milliseconds
	private Context mContext;
	private OnHttpCallback mCallback;

	public void post(Context context, final String path,
			final Map<String, String> params) {
		mContext = context;
		if (!NetworkChecker.isNetworkAvailable(context)) {
			if (mCallback != null) {
				mCallback.onHttpNetworkNotFound(path);
			}
			return;
		}

		sendRequest(path, params);
	}

	/**
	 * android3.0以前，{@link AsyncTask}是最少5个线程并发执行的，<br/>
	 * 从3.0开始，改成串行执行了 . 不过网络是Wifi或者3G模式的话，并发模式比较好. <br/>
	 * 此方法会更具网络状况自动决定采用并发还是串行执行.
	 * 
	 * @param path
	 * @param params
	 */
	private void sendRequest(String path, Map<String, String> params) {
		NetworkAsynTask task = new NetworkAsynTask(path, params);
		if (Build.VERSION.SDK_INT < 11) {
			task.execute();
		} else {
			NetworkType type = NetworkChecker.getNetworkType(mContext);
			switch (type) {
			case NETWORK_TYPE_3G:
			case NETWORK_TYPE_WIFI:
				task.executeOnExecutor(
						ExecutorHolder.THREAD_POOL_EXECUTOR,
						new Void[] {});
				break;
			default:
				task.execute();
				break;
			}
		}
	}

	private class NetworkAsynTask extends AsyncTask<Void, Void, String> {

		private final String mPath;

		private final Map<String, String> mParams;

		private int mResponse;

		public NetworkAsynTask(String path, Map<String, String> params) {
			mPath = path;
			mParams = params;
		}

		@Override
		protected String doInBackground(Void... params) {
			StringBuffer sb = new StringBuffer();
			HttpURLConnection conn = null;
			DataOutputStream outStream = null;
			try {
				URL url = new URL(mPath);
				byte[] data = parseParams(mParams).getBytes(CHARSET);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
				conn.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", CHARSET);
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				outStream = new DataOutputStream(conn.getOutputStream());
				outStream.write(data);
				outStream.flush();
				outStream.close();

				mResponse = conn.getResponseCode();
				if (mResponse == RESPONSE_SUCCESS) {
					InputStream in = conn.getInputStream();
					InputStreamReader reader = new InputStreamReader(in,
							CHARSET);
					char[] buff = new char[1024];
					int len;
					while ((len = reader.read(buff)) > 0) {
						sb.append(buff, 0, len);
					}
					reader.close();
					in.close();
				}
			} catch (Exception e) {
				if (mCallback != null) {
					mCallback.onHttpError(mPath, e);
				}
				e.printStackTrace();
			} finally {
				if (null != conn) {
					conn.disconnect();
				}
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			if (mCallback != null) {
				mCallback.onHttpReturn(mPath, mResponse, result);
				if (mResponse == RESPONSE_SUCCESS) {
					if (TextUtils.isEmpty(result)) {
						mCallback.onHttpNothingReturn(mPath);
					} else {
						mCallback.onHttpSuccess(mPath, result);
					}
				} else {
					mCallback.onHttpError(mPath, mResponse);
				}
			}
			mCallback = null;
		}
	}

	private String parseParams(Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		if (params == null)
			params = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			builder.append(entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue(), CHARSET) + "&");
		}
		if (builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);
		System.out.println(builder.toString());
		return builder.toString();
	}

	public void setOnHttpCallback(OnHttpCallback onHttpResponse) {
		if (onHttpResponse != mCallback) {
			mCallback = onHttpResponse;
		}
	}

	public interface OnHttpCallback {
		/***************** 使用方法：在BaseHttpActivity中实现此接口，可在子类中选择需要复写的方法 ****************/

		/**
		 * 
		 * 未检测到网络，运行在主线程.
		 * 
		 * @param path
		 *            请求的url
		 */
		void onHttpNetworkNotFound(String path);

		/**
		 * 
		 * 网络请求返回调用此接口, 运行在主线程.
		 * 
		 * @param path
		 *            请求的url
		 * @param response
		 *            返回码
		 * @param result
		 *            返回的结果
		 */
		void onHttpReturn(String path, int response, String result);

		/**
		 * 
		 * 网络请求抛出异常会调用此接口,运行在子线程.
		 * 
		 * @param path
		 *            请求的URL
		 * @param exception
		 *            捕获的异常
		 */
		void onHttpError(String path, Exception exception);

		/**
		 * 
		 * 网络请求出错会调用此接口, 运行在子线程.
		 * 
		 * @param path
		 *            请求的url
		 * @param response
		 *            返回码
		 */
		void onHttpError(String path, int response);

		/**
		 * 网络请求成功，但没有返回结果会调用此接口,运行在主线程.
		 * 
		 * @param path
		 */
		void onHttpNothingReturn(String path);

		/**
		 * 
		 * 网络请求成功并返回结果会调用此接口,运行在主线程.
		 * 
		 * @param path
		 *            请求的url
		 * @param result
		 *            返回的结果
		 */
		void onHttpSuccess(String path, String result);
	}
}
