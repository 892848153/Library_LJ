package com.lj.library.http.apache;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lj.library.http.common.NetworkChecker;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 上传文件到服务器.
 * 
 * @time 2014年10月10日 下午2:15:39
 * @author jie.liu
 */
public class HttpUploader {

//	private WeakReference<OnUploadListener> mOnUploadListenerWRef;

	private OnUploadListener mOnUploadListener;

	private final long mTimeConsumed = 0L;

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
		Map<String, String> files = new HashMap<String, String>();
		files.put(fileKey, filePath);
		uploadFiles(activity, url, files, params);
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
		Map<String, String> files = new HashMap<String, String>();
		files.put(fileKey, file.getAbsolutePath());
		uploadFiles(activity, url, files, params);
	}

	/**
	 * 
	 * @param activity
	 * @param url
	 * @param files
	 *            文件键值对，key是文件的key， value是文件的路径.
	 * @param params
	 */
	public void uploadFiles(Activity activity, String url,
			Map<String, String> files, Map<String, String> params) {
		if (!NetworkChecker.isNetworkAvailable(activity)) {
			if (mOnUploadListener != null) {
				mOnUploadListener.onNetworkNotFound(url);
			}
			return;
		}

		new UploadAsynTask(url, files, params).execute();
	}

	private class UploadAsynTask extends AsyncTask<Void, Long, String> {

		private final String mUrl;
		private final Map<String, String> mFiles;
		private final Map<String, String> mParams;
		private final HttpResponseWrapper mResponseWrapper;

		public UploadAsynTask(String url, Map<String, String> files,
				Map<String, String> params) {
			mUrl = url;
			mFiles = files;
			mParams = params;
			mResponseWrapper = new HttpResponseWrapper();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient client = HttpAssistance.getDefaultHttpClient();
			try {
				HttpPost request = new HttpPost(mUrl);
				MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
						.create();
				entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
				if (params != null) {
					for (Entry<String, String> param : mParams.entrySet()) {
						entityBuilder.addTextBody(param.getKey(),
								param.getValue());
					}
				}

				if (mFiles != null) {
					for (Entry<String, String> fileEntry : mFiles.entrySet()) {
						File file = new File(fileEntry.getValue());
						if (file.exists()) {
							entityBuilder.addBinaryBody(fileEntry.getKey(),
									file);
						}
					}
				}

				request.setEntity(entityBuilder.build());
				result = HttpAssistance.executeRequest(client, request,
						mResponseWrapper);
			} catch (Exception e) {
				e.printStackTrace();
				if (mOnUploadListener != null) {
					mOnUploadListener.onUploadError(mUrl, e);
				}
			} finally {
				HttpAssistance.shutdown(client);
			}
			return result;
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
				if (mResponseWrapper.response == HttpStatus.SC_OK
						&& !TextUtils.isEmpty(result)) {
					mOnUploadListener.onUploadSuccess(mUrl, mFiles, result);
				} else {
					mOnUploadListener.onUploadFail(mUrl, mFiles, mResponseWrapper.response);
				}
			}
		}
	}

	public void setOnUploadListener(OnUploadListener onUploadListener) {
		if (mOnUploadListener != onUploadListener) {
			mOnUploadListener = onUploadListener;
		}
	}

	/**
	 * 获取上传耗费的时间.
	 * 
	 * @return
	 */
	public long getTimeConsuming() {
		return mTimeConsumed;
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
		 * @param files
		 * @param result
		 */
		void onUploadSuccess(String url, Map<String, String> files,
							 String result);

		/**
		 * 上传失败，运行在主线程.
		 * 
		 * @param url
		 * @param files
		 * @param response
		 */
		void onUploadFail(String url, Map<String, String> files, int response);
	}
}
