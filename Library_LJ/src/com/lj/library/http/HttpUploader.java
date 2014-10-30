package com.lj.library.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lj.library.util.LogUtil;

/**
 * 上传文件到服务器.
 * 
 * @time 2014年10月10日 下午2:15:39
 * @author jie.liu
 */
public class HttpUploader {

	public static final int RESPONSE_SUCCESS = 200;
	private static final String CHARSET = "UTF-8";
	private static final int DEFAULT_CONNECTION_TIMEOUT = (20 * 1000); // milliseconds
	private static final int DEFAULT_SOCKET_TIMEOUT = (20 * 1000); // milliseconds
	private static final String BOUNDARY = UUID.randomUUID().toString();
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String REAL_BOUNDARY = PREFIX + BOUNDARY + LINE_END;
	private static final String BODY_END = REAL_BOUNDARY + LINE_END;
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private OnUploadListener mOnUploadListener;
	private long mTimeConsuming = 0L;

	private final String DEFAULT_FILE_KEY = "file";

	public void uploadFile(Activity activity, String url, String filePath) {
		uploadFile(activity, url, DEFAULT_FILE_KEY, filePath);
	}

	public void uploadFile(Activity activity, String url, String fileKey,
			String filePath) {
		uploadFile(activity, url, DEFAULT_FILE_KEY, filePath,
				Collections.<String, String> emptyMap());
	}

	public void uploadFile(Activity activity, String url, String fileKey,
			String filePath, Map<String, String> params) {
		if (TextUtils.isEmpty(filePath)) {
			throw new NullPointerException();
		}

		File file = new File(filePath);
		if (file.exists()) {
			uploadFile(activity, url, DEFAULT_FILE_KEY, file, params);
		} else {
			LogUtil.e(this, "需要上传的文件:" + filePath + " 不存在.");
		}
	}

	public void uploadFile(Activity activity, String url, File file) {
		uploadFile(activity, url, DEFAULT_FILE_KEY, file);
	}

	public void uploadFile(Activity activity, String url, String fileKey,
			File file) {
		uploadFile(activity, url, DEFAULT_FILE_KEY, file,
				Collections.<String, String> emptyMap());
	}

	public void uploadFile(Activity activity, String url, String fileKey,
			File file, Map<String, String> params) {
		if (!file.exists()) {
			LogUtil.e(this, "需要上传的文件:" + file.getAbsolutePath() + " 不存在.");
			return;
		}

		if (!NetworkChecker.isNetworkAvailable(activity)) {
			if (mOnUploadListener != null) {
				mOnUploadListener.onNetworkNotFound(url);
			}
			return;
		}

		new UploadAsynTask(url, fileKey, file, params).execute();
	}

	private class UploadAsynTask extends AsyncTask<Void, Long, String> {

		private final String mUrl;
		private final String mFileKey;
		private final File mFile;
		private final Map<String, String> mParams;
		private int mResponse;

		public UploadAsynTask(String url, String fileKey, File file,
				Map<String, String> params) {
			mUrl = url;
			mFileKey = fileKey;
			mFile = file;
			mParams = params;
		}

		@Override
		protected String doInBackground(Void... params) {
			StringBuffer sb = null;
			DataOutputStream dos = null;
			InputStream fileIs = null;
			InputStream in = null;
			InputStreamReader reader = null;

			try {
				long requestTime = System.currentTimeMillis();
				long responseTime = 0L;
				URL url = new URL(mUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
				conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
				conn.setDoInput(true); // 允许输入流
				conn.setDoOutput(true); // 允许输出流
				conn.setUseCaches(false); // 不允许使用缓存
				conn.setRequestMethod("POST"); // 请求方式
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Charset", CHARSET); // 设置编码
				conn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				conn.setRequestProperty("Content-Type", CONTENT_TYPE
						+ ";boundary=" + BOUNDARY);
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				dos = new DataOutputStream(conn.getOutputStream());

				String param = "";

				/***
				 * 以下是用于上传参数
				 */
				if (mParams != null && mParams.size() > 0) {
					Iterator<String> it = mParams.keySet().iterator();
					while (it.hasNext()) {
						sb = null;
						sb = new StringBuffer();
						String key = it.next();
						String value = mParams.get(key);
						sb.append(REAL_BOUNDARY);
						sb.append("Content-Disposition: form-data; name=\"")
								.append(key).append("\"").append(LINE_END)
								.append(LINE_END);
						sb.append(value).append(LINE_END);
						param = sb.toString();
						dos.write(param.getBytes());
					}
				}

				sb = null;
				param = null;
				sb = new StringBuffer();
				// for () // 同时上传多个文件,待测
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append(REAL_BOUNDARY);
				sb.append("Content-Disposition:form-data; name=\"" + mFileKey
						+ "\"; filename=\"" + mFile.getName() + "\"" + LINE_END);
				sb.append("Content-Type:text/plain" + LINE_END); // 这里配置的Content-type很重要的
																	// ，用于服务器端辨别文件的类型的
				sb.append(LINE_END);
				param = sb.toString();
				sb = null;
				sb = new StringBuffer();

				LogUtil.i(HttpUploader.this, mFile.getName() + "=" + param);
				dos.write(param.getBytes());
				// 上传文件
				fileIs = new FileInputStream(mFile);
				byte[] bytes = new byte[1024];
				int len = 0;
				int curLen = 0;
				while ((len = fileIs.read(bytes)) != -1) {
					curLen += len;
					dos.write(bytes, 0, len);
					publishProgress((long) curLen, mFile.length());
				}

				dos.write(LINE_END.getBytes());
				// } 同时上传多个文件,待测
				byte[] end_data = BODY_END.getBytes();
				dos.write(end_data);
				dos.flush();
				// 计算上传耗费时间
				mResponse = conn.getResponseCode();
				responseTime = System.currentTimeMillis();
				mTimeConsuming = responseTime - requestTime;
				// 获取服务器返回结果
				LogUtil.e(this, "response code:" + mResponse);
				if (mResponse == RESPONSE_SUCCESS) {
					in = conn.getInputStream();
					reader = new InputStreamReader(in, CHARSET);
					char[] buff = new char[1024];
					while ((len = reader.read(buff)) > 0) {
						sb.append(buff, 0, len);
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				if (mOnUploadListener != null) {
					mOnUploadListener.onUploadError(mUrl, e);
				}
			} catch (IOException e) {
				e.printStackTrace();
				if (mOnUploadListener != null) {
					mOnUploadListener.onUploadError(mUrl, e);
				}
			} finally {
				try {
					if (fileIs != null) {
						fileIs.close();
					}
					if (dos != null) {
						dos.close();
					}

					if (in != null) {
						in.close();
					}

					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return sb.toString();
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			long curlen = values[0];
			long totalLen = values[1];
			if (mOnUploadListener != null) {
				mOnUploadListener.onUploadProgress(curlen, totalLen);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (mOnUploadListener != null) {
				if (mResponse == RESPONSE_SUCCESS && !TextUtils.isEmpty(result)) {
					mOnUploadListener.onUploadSuccess(mUrl, mFile);
				} else {
					mOnUploadListener.onUploadFail(mUrl, mFile);
				}
			}
		}
	}

	public void setOnUploadListener(OnUploadListener onUploadListener) {
		mOnUploadListener = onUploadListener;
	}

	/**
	 * 获取上传耗费的时间.
	 * 
	 * @return
	 */
	public long getTimeConsuming() {
		return mTimeConsuming;
	}

	public interface OnUploadListener {

		/**
		 * 上传异常，运行在子线程.
		 * 
		 * @param url
		 * @param e
		 */
		void onUploadError(String url, Exception e);

		/**
		 * 未发现网络，运行在主线程
		 * 
		 * @param url
		 */
		void onNetworkNotFound(String url);

		/**
		 * 上传进度，运行在主线程.
		 * 
		 * @param currentLength
		 *            当前已经上传了的文件大小.
		 * @param totalLength
		 *            全部需要上传的文件大小.
		 */
		void onUploadProgress(long currentLength, long totalLength);

		/**
		 * 上传成功， 运行在主线程.
		 * 
		 * @param url
		 * @param srcFile
		 */
		void onUploadSuccess(String url, File srcFile);

		/**
		 * 上传失败，运行在主线程.
		 * 
		 * @param url
		 * @param srcFile
		 */
		void onUploadFail(String url, File srcFile);
	}
}
