package com.lj.library.imagemanager;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.lj.library.imagemanager.ImageCacheManager.OnBitmapFromHttpListener;

public class ImageGetFromHttp {
	private static final String LOG_TAG = ImageGetFromHttp.class
			.getSimpleName();

	private final WeakReference<Context> mContextWeakRef;

	private final Map<String, List<WeakReference<ImageView>>> mItems;

	private OnBitmapFromHttpListener mListener;

	public ImageGetFromHttp(Context context) {
		mContextWeakRef = new WeakReference<Context>(context);
		mItems = new HashMap<String, List<WeakReference<ImageView>>>();
	}

	public void setOnGetFromHttpListener(OnBitmapFromHttpListener listener) {
		mListener = listener;
	}

	public Bitmap downloadBitmap(String url, ImageView imageView) {
		if (!new NetworkChecker().isNetworkAvailable()) {
			if (mListener != null) {
				mListener.onGetBitmapNetworkNotFound(url);
			}
			return null;
		}

		putItem(url, imageView);
		new NetworkAsynTask(url).execute();
		return null;
	}

	private void putItem(String url, ImageView imageView) {
		List<WeakReference<ImageView>> values = null;
		if (mItems.containsKey(url)) {
			values = mItems.get(url);
		} else {
			values = new ArrayList<WeakReference<ImageView>>();
		}
		values.add(new WeakReference<ImageView>(imageView));
		mItems.put(url, values);
	}

	private class NetworkChecker {
		public boolean isNetworkAvailable() {
			Context context = mContextWeakRef.get();
			if (context == null) {
				return false;
			}

			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null)
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
			}
			Toast.makeText(context, "未检测到可用的网络", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	private class NetworkAsynTask extends AsyncTask<String, Void, Bitmap> {

		private final String mUrl;

		public NetworkAsynTask(String url) {
			mUrl = url;
		}

		@Override
		protected void onPreExecute() {
			if (mListener != null) {
				mListener.onGetBitmapBegin(mUrl);
			}
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			final HttpClient client = new DefaultHttpClient();
			final HttpGet getRequest = new HttpGet(mUrl);

			try {
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					Log.w(LOG_TAG, "Error " + statusCode
							+ " while retrieving bitmap from " + mUrl);
					return null;
				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = entity.getContent();
						FilterInputStream fit = new FlushedInputStream(
								inputStream);
						return BitmapFactory.decodeStream(fit);
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (IOException e) {
				getRequest.abort();
				onGetBitmapError(mUrl, e);
				Log.w(LOG_TAG,
						"I/O error while retrieving bitmap from " + mUrl, e);
			} catch (IllegalStateException e) {
				getRequest.abort();
				onGetBitmapError(mUrl, e);
				Log.w(LOG_TAG, "Incorrect URL: " + mUrl);
			} catch (Exception e) {
				getRequest.abort();
				onGetBitmapError(mUrl, e);
				Log.w(LOG_TAG, "Error while retrieving bitmap from " + mUrl, e);
			} finally {
				client.getConnectionManager().shutdown();
			}
			return null;
		}

		private void onGetBitmapError(String url, Exception e) {
			if (mListener != null) {
				mListener.onGetBitmapError(url, e);
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			fillViewWithBitmap(result);
			cacheBitmap(result);
		}

		private void fillViewWithBitmap(Bitmap result) {
			List<WeakReference<ImageView>> values = mItems.get(mUrl);
			for (WeakReference<ImageView> ivWeakRef : values) {
				ImageView imageView = ivWeakRef.get();
				if (imageView != null && result != null) {
					Log.i("ImageCacheManager", "网络上下载图片，填充图片到imageView");
					imageView.setImageBitmap(result);
				}
			}
		}

		private void cacheBitmap(Bitmap result) {
			Context context = mContextWeakRef.get();
			if (context != null && result != null) {
				Log.i("ImageCacheManager", "缓存网络上下载的图片到内存");
				ImageMemoryCache memoryCache = new ImageMemoryCache(context);
				memoryCache.addBitmapToCache(mUrl, result);

				Log.i("ImageCacheManager", "缓存网络上下载的图片到本地文件");
				ImageFileCache fileCache = new ImageFileCache();
				fileCache.saveBitmap(result, mUrl);
			}

			if (mListener != null) {
				mListener.onGetBitmapOver(mUrl, result);
			}
		}
	}

	public byte[] getBytesFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024]; // 用数据装
		int len = -1;
		while ((len = is.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		// 关闭流一定要记得。
		return outstream.toByteArray();
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it
	 * reaches EOF.
	 */
	private class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
