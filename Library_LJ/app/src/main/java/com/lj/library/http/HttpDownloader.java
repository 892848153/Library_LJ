package com.lj.library.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lj.library.util.IOStreamCloser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 从服务器下载文件.
 * 
 * @time 2014年9月3日 下午2:37:47
 * @author jie.liu
 */
public class HttpDownloader {

	private static final int RESPONSE_DOWNLOAD_SUCCESS = 0x01;

	private static final int RESPONSE_DOWNLOAD_FAIL = 0x02;

//	private WeakReference<OnDownloadListener> mOnDwldListenerWRef;

	private OnDownloadListener mOnDownloadListener;

	private final Map<String, Boolean> mGoOnDownload = new HashMap<String, Boolean>();

	public HttpDownloader() {
	}

	public HttpDownloader(OnDownloadListener onDownloadListener) {
		mOnDownloadListener = onDownloadListener;
	}

	public void getFileLength(Activity context, String url) {
		downloadFile(context, url, null);
	}

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
			if (mOnDownloadListener != null) {
				mOnDownloadListener.onNetworkNotFound(url);
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
			if (!TextUtils.isEmpty(mTargetDir)) {
				File targetDir = new File(mTargetDir);
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
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

				long contentLenth = entity.getContentLength();
				if (mOnDownloadListener != null) {
					mOnDownloadListener.onDownloadFileLength(mUrl, contentLenth);
				}

				boolean goOnDownload = checkHasSameNameFile(request);
				mGoOnDownload.put(mUrl, goOnDownload);
				if (!goOnDownload) {
					return RESPONSE_DOWNLOAD_FAIL;
				}

				is = entity.getContent();
				if (!TextUtils.isEmpty(mTargetFilePath)) {
					os = new FileOutputStream(mTargetFilePath);
				}
				downloadFile(is, os, entity);
			} catch (Exception e) {
				if (mOnDownloadListener != null) {
					mOnDownloadListener.onDownloadError(mUrl, e);
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
			if (!TextUtils.isEmpty(mTargetDir)) {
				if (!mTargetDir.endsWith(File.separator)) {
					mTargetDir += File.separator;
				}
				mTargetFilePath = mTargetDir + filename;
			}

			if (!TextUtils.isEmpty(mTargetFilePath)) {
				File file = new File(mTargetFilePath);
				if (file.exists()) {
					if (mOnDownloadListener != null) {
						mOnDownloadListener.onDownloadFileExist(mUrl, filename);
					}

					if (goOnDownload) {
						if (file.exists()) {
							file.delete();
						}
					}
				} else {
					goOnDownload = true;
				}
			}

			return goOnDownload;
		}

		private String buildTargetFilename(HttpPost request) {
			// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
			if (TextUtils.isEmpty(mTargetDir)) {
				return null;
			}

			// url中带有文件名
			// FilenameGenerator generator = new MD5FilenameGenerator();
			// String filename = generator.generateFilename(mUrl);

			String filename = null;
			Header header = request.getFirstHeader("Content-Disposition");
			if (header != null) {
				filename = header.getValue();
			}
			if (filename == null || filename.length() < 1) {
				filename = UUID.randomUUID() + "";
			}

			return filename;
		}

		private void downloadFile(InputStream is, OutputStream os,
				HttpEntity entity) throws IOException {
			if (is == null || os == null || entity == null) {
				if (mOnDownloadListener != null) {
					mOnDownloadListener.onDownloadFail(mUrl, mTargetFilePath);
				}
				return;
			}

			// 1K的数据缓冲
			long contentLength = entity.getContentLength();
			byte[] bs = new byte[1024];
			int len = -1, downloadedLength = 0;
			while ((len = is.read(bs)) != -1) {
				boolean goOnDownload = mGoOnDownload.get(mUrl);
				if (goOnDownload) {
					os.write(bs, 0, len);
					downloadedLength += len;
					publishProgress((long) downloadedLength, contentLength);
				} else {
					if (mOnDownloadListener != null) {
						mOnDownloadListener.onDownloadingStoped(mUrl, mTargetFilePath);
					}
					break;
				}
			}
			os.flush();
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			if (mOnDownloadListener != null) {
				long downloadedLength = values[0];
				long totalLength = values[1];
				mOnDownloadListener.onDownloadProgress(mUrl, mTargetFilePath, downloadedLength, totalLength);
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (mOnDownloadListener != null
					&& !TextUtils.isEmpty(mTargetFilePath)
					&& !TextUtils.isEmpty(mUrl)
					&& mGoOnDownload.get(mUrl)) {
				if (result.intValue() == RESPONSE_DOWNLOAD_SUCCESS) {
					mOnDownloadListener.onDownloadSuccess(mUrl, mTargetFilePath);
				} else {
					mOnDownloadListener.onDownloadFail(mUrl, mTargetFilePath);
				}
			}
		}
	}

	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		if (onDownloadListener != mOnDownloadListener) {
			mOnDownloadListener = onDownloadListener;
		}
	}

	/**
	 * 停止下载.
	 * 
	 * @param url
	 */
	public void stopDownload(String url) {
		mGoOnDownload.put(url, false);
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

		/**
		 * 需要下载的文件的大小, 运行在子线程.
		 * 
		 * @param url
		 * @param totalLength
		 *            文件大小，单位byte
		 */
		void onDownloadFileLength(String url, long totalLength);

		/**
		 * 停止下载文件， 运行在子线程.
		 * 
		 * @param url
		 * @param targetFilePath
		 */
		void onDownloadingStoped(String url, String targetFilePath);
	}

	public interface FilenameGenerator {

		/**
		 * 文件名生成器，url当中带着文件名.
		 * 
		 * @param url
		 * @return url或者目标文件夹为空，则返回null
		 */
		String generateFilename(String url);
	}

	public static class MD5FilenameGenerator implements FilenameGenerator {

		@Override
		public String generateFilename(String url) {
			if (TextUtils.isEmpty(url)) {
				return null;
			}

			String filename = null;
			if (!TextUtils.isEmpty(url)) {
				int index = url.lastIndexOf("/");
				if (index != -1 && url.length() > index + 1) {
					filename = md5(url.substring(index + 1));
				}
			}

			return filename;
		}

		public final String md5(String content) {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
					'9', 'A', 'B', 'C', 'D', 'E', 'F' };
			try {
				byte[] btInput = content.getBytes();
				// 获取信息摘要对象
				MessageDigest mdInst = MessageDigest.getInstance("MD5");
				// 使用指定的字节串更新摘要
				mdInst.update(btInput);
				// 将指定的"字节串"进行信息摘要,获取到固定长度的字节数组
				byte[] md = mdInst.digest();
				// 把密文转换成十六进制的字符串形式
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					// 前四个bit转成一个16进制的字符
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					// 后四个bit转成一个16进制的字符
					str[k++] = hexDigits[byte0 & 0xf];
				}
				// 返回16进制的字符串
				return new String(str);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
