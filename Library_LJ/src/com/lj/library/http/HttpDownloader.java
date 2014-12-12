package com.lj.library.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.os.AsyncTask;

import com.lj.library.util.IOStreamCloser;

/**
 * 从服务器下载文件.
 * 
 * @time 2014年9月3日 下午2:37:47
 * @author jie.liu
 */
public class HttpDownloader {

	private static final int RESPONSE_DOWNLOAD_SUCCESS = 0x01;

	private static final int RESPONSE_DOWNLOAD_FAIL = 0x02;

	private OnDownloadListener mOnDwlodListener;

	/**
	 * 下载文件.
	 * 
	 * @param context
	 * @param url
	 * @param targetDir
	 *            下载的文件将保存在这个文件夹下，文件夹不存在则会自动创建
	 */
	public void downloadFile(Activity context, String url, String targetDir) {
		if (!NetworkChecker.isNetworkAvailable(context)) {
			if (mOnDwlodListener != null) {
				mOnDwlodListener.onNetworkNotFound(url);
			}
			return;
		}

		new DownloadAsynTask(url, targetDir).execute();
	}

	private class DownloadAsynTask extends AsyncTask<Void, Long, Integer> {

		private final String mUrl;
		private String mTargetDir;
		private String mTargetFilePath;
		private final HttpResponseWrapper mResponseWrapper;

		public DownloadAsynTask(String url, String targetDir) {
			mUrl = url;
			mTargetDir = targetDir;
			mResponseWrapper = new HttpResponseWrapper();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			File targetDir = new File(mTargetDir);
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}

			HttpClient client = HttpAssistance.getDefaultHttpClient();
			InputStream is = null;
			OutputStream os = null;
			try {
				HttpPost request = new HttpPost(mUrl);
				HttpEntity entity = HttpAssistance.executeDownload(client,
						request, mResponseWrapper);
				if (entity == null) {
					return RESPONSE_DOWNLOAD_FAIL;
				}

				boolean goOnDownload = checkHasSameNameFile(request);
				if (!goOnDownload) {
					return RESPONSE_DOWNLOAD_FAIL;
				}

				is = entity.getContent();
				os = new FileOutputStream(mTargetFilePath);
				downloadFile(is, os, entity);
			} catch (Exception e) {
				if (mOnDwlodListener != null) {
					mOnDwlodListener.onDownloadError(mUrl, e);
				}
				e.printStackTrace();
			} finally {
				IOStreamCloser.closeOutputStream(os);
				IOStreamCloser.closeInputStream(is);
				HttpAssistance.shutdown(client);
			}

			return RESPONSE_DOWNLOAD_SUCCESS;
		}

		private boolean checkHasSameNameFile(HttpPost request) {
			boolean goOnDownload = false;
			String filename = buildTargetFilename(request);
			File file = new File(mTargetFilePath);
			if (file.exists()) {
				if (mOnDwlodListener != null) {
					goOnDownload = mOnDwlodListener.onDownloadFileExist(mUrl,
							filename);
				}

				if (goOnDownload) {
					if (file.exists()) {
						file.delete();
					}
				}
			}

			return goOnDownload;
		}

		private String buildTargetFilename(HttpPost request) {
			// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
			String filename = null;
			Header header = request.getFirstHeader("Content-Disposition");
			if (header != null) {
				filename = header.getValue();
			}
			if (filename == null || filename.length() < 1) {
				filename = UUID.randomUUID() + "";
			}

			if (!mTargetDir.endsWith(File.separator)) {
				mTargetDir += File.separator;
			}

			mTargetFilePath = mTargetDir + filename;
			return filename;
		}

		private void downloadFile(InputStream is, OutputStream os,
				HttpEntity entity) throws IOException {
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			int len = -1, downloadedLength = 0;
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				downloadedLength += len;
				publishProgress((long) downloadedLength,
						entity.getContentLength());
			}
			os.flush();
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			if (mOnDwlodListener != null) {
				long downloadedLength = values[0];
				long totalLength = values[1];
				mOnDwlodListener.onDownloadProgress(mUrl, mTargetFilePath,
						downloadedLength, totalLength);
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (mOnDwlodListener != null) {
				if (result.intValue() == RESPONSE_DOWNLOAD_SUCCESS) {
					mOnDwlodListener.onDownloadSuccess(mUrl, mTargetFilePath);
				} else {
					mOnDwlodListener.onDownloadFail(mUrl, mTargetFilePath);
				}
			}
		}
	}

	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		mOnDwlodListener = onDownloadListener;
	}

	public interface OnDownloadListener {

		/**
		 * 未检测到网络调用，运行在主线程.
		 * 
		 * @time 2014年6月10日 上午10:43:08
		 * @param url
		 */
		void onNetworkNotFound(String url);

		/**
		 * 需要下载的文件已经存在,运行在子线程.
		 * 
		 * @param url
		 * @param filePath
		 * @return 是否继续下载。 返回true时，会删除存在的文件，继续下载。
		 */
		boolean onDownloadFileExist(String url, String filePath);

		/**
		 * 下载异常调用，运行在子线程.
		 * 
		 * @time 2014年6月10日 上午10:42:39
		 * @param url
		 * @param e
		 */
		void onDownloadError(String url, Exception e);

		/**
		 * 下载成功调用，运行在主线程.
		 * 
		 * @time 2014年6月10日 上午10:43:33
		 * @param url
		 *            下载文件在服务器上的地址
		 * @param targetFilePath
		 *            下载文件保存的地址
		 */
		void onDownloadSuccess(String url, String targetFilePath);

		/**
		 * 下载失败，运行在主线程.
		 * 
		 * @param url
		 * @param targetFilePath
		 */
		void onDownloadFail(String url, String targetFilePath);

		/**
		 * 下载进度，运行在主线程.
		 * 
		 * @param url
		 * @param targetFilePath
		 * @param curLength
		 *            当前已下载的文件大小
		 * @param totalLength
		 *            文件全部大小
		 */
		void onDownloadProgress(String url, String targetFilePath,
				long curLength, long totalLength);
	}
}
