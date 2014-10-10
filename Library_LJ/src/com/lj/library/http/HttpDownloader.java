package com.lj.library.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import android.app.Activity;
import android.os.AsyncTask;

/**
 * 从服务器下载文件.
 * 
 * @time 2014年9月3日 下午2:37:47
 * @author jie.liu
 */
public class HttpDownloader {

	private OnDownloadListener mOnDwlodListener;

	/**
	 * 
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

	private class DownloadAsynTask extends AsyncTask<Void, Void, Integer> {

		private final String mUrl;
		private String mTargetDir;
		private String mTargetFileUrl;

		public DownloadAsynTask(String url, String targetDir) {
			mUrl = url;
			mTargetDir = targetDir;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			URLConnection con = null;
			InputStream is = null;
			OutputStream os = null;
			try {
				URL url = new URL(mUrl);
				// 打开连接
				con = url.openConnection();
				// 输入流
				is = con.getInputStream();
				File targetDir = new File(mTargetDir);
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}

				// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
				String filename = con.getHeaderField("Content-Disposition");
				if (filename == null || filename.length() < 1) {
					filename = UUID.randomUUID() + "";
				}

				if (!mTargetDir.endsWith(File.separator)) {
					mTargetDir += File.separator;
				}

				mTargetFileUrl = mTargetDir + filename;
				File file = new File(mTargetFileUrl);
				// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
				if (file.exists()) {
					file.delete();
				}

				// 1K的数据缓冲
				byte[] bs = new byte[1024];
				// 读取到的数据长度
				int len;
				// 输出的文件流
				os = new FileOutputStream(mTargetFileUrl);
				// 开始读取
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				// 完毕，关闭所有链接
				os.flush();
				return 0;
			} catch (MalformedURLException e) {
				if (mOnDwlodListener != null) {
					mOnDwlodListener.onDownloadError(mUrl, e);
				}
				e.printStackTrace();
			} catch (IOException e) {
				if (mOnDwlodListener != null) {
					mOnDwlodListener.onDownloadError(mUrl, e);
				}
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (mOnDwlodListener != null) {
				if (result.intValue() == 0) {
					mOnDwlodListener.onDownloadSuccess(mUrl, mTargetFileUrl);
				} else {
					mOnDwlodListener.onDownloadFail(mUrl, mTargetFileUrl);
				}
			}
		}
	}

	public void setOnDownloadResponse(OnDownloadListener onDownloadListener) {
		mOnDwlodListener = onDownloadListener;
	}

	public interface OnDownloadListener {

		/**
		 * 
		 * 下载异常调用，运行在子线程.
		 * 
		 * @time 2014年6月10日 上午10:42:39
		 * @param url
		 * @param e
		 */
		void onDownloadError(String url, Exception e);

		/**
		 * 
		 * 未检测到网络调用，运行在主线程.
		 * 
		 * @time 2014年6月10日 上午10:43:08
		 * @param url
		 */
		void onNetworkNotFound(String url);

		/**
		 * 
		 * 下载成功调用，运行在主线程.
		 * 
		 * @time 2014年6月10日 上午10:43:33
		 * @param url
		 *            下载文件在服务器上的地址
		 * @param targetUrl
		 *            下载文件保存的地址
		 */
		void onDownloadSuccess(String url, String targetUrl);

		/**
		 * 下载失败，运行在主线程.
		 * 
		 * @param url
		 * @param targetUrl
		 */
		void onDownloadFail(String url, String targetUrl);
	}
}
