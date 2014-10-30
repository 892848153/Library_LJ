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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.lj.library.http.NetworkChecker;
import com.lj.library.imagemanager.ImageCacheManager.OnBitmapFromHttpListener;

public class ImageGetFromHttp {
	private static final String LOG_TAG = ImageGetFromHttp.class
			.getSimpleName();

	private final Context mContext;

	private final boolean mMemoryCach, mDiskCach;

	private int mTargetWidth;

	private int mTargetHeight;

	private boolean mRecycleOnFinish;

	private final WeakReference<Context> mContextWeakRef;

	private final Map<String, List<WeakReference<ImageView>>> mItems;

	private OnBitmapFromHttpListener mListener;

	public ImageGetFromHttp(Context context, boolean memoryCach,
			boolean diskCach) {
		mContext = context;
		mMemoryCach = memoryCach;
		mDiskCach = diskCach;
		mContextWeakRef = new WeakReference<Context>(context);
		mItems = new HashMap<String, List<WeakReference<ImageView>>>();
	}

	public void setOnGetFromHttpListener(OnBitmapFromHttpListener listener) {
		mListener = listener;
	}

	/**
	 * 下载网络图片，并填充到控件上.
	 * 
	 * @param url
	 * @param imageView
	 *            不想显示可以传入null
	 * @return
	 * @see #downloadBitmap(String, ImageView, int, int, boolean)
	 */
	public Bitmap downloadBitmap(String url, ImageView imageView) {
		return downloadBitmap(url, imageView, 0, 0);
	}

	/**
	 * 下载图片并进行压缩处理.
	 * 
	 * @param url
	 * @param imageView
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 * @see #downloadBitmap(String, ImageView, int, int, boolean)
	 */
	public Bitmap downloadBitmap(String url, ImageView imageView,
			int targetWidth, int targetHeight) {
		return downloadBitmap(url, imageView, targetWidth, targetHeight, false);
	}

	/**
	 * 下载图片并进行压缩处理.
	 * 
	 * @param url
	 * @param imageView
	 * @param targetWidth
	 *            图片将要压缩的目标宽度
	 * @param targetHeight
	 *            图片将要压缩的目标高度
	 * @param recycleOnFinish
	 *            是否将其加入内存缓存中的特殊队列中，为true时，调用
	 *            {@link ImageCacheManager#recycleOnDestroy()}将会回收队列中对象的资源
	 * @return
	 */
	public Bitmap downloadBitmap(String url, ImageView imageView,
			int targetWidth, int targetHeight, boolean recycleOnFinish) {
		if (!NetworkChecker.isNetworkAvailable(mContext)) {
			if (mListener != null) {
				mListener.onGetBitmapNetworkNotFound(url);
			}
			return null;
		}

		mTargetWidth = targetWidth;
		mTargetHeight = targetHeight;
		mRecycleOnFinish = recycleOnFinish;

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
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				Bitmap result = bitmap;
				if (mTargetWidth != 0 && mTargetHeight != 0) {
					result = ImageCompressor.compressImage(bitmap,
							mTargetWidth, mTargetHeight);
				}
				fillViewWithBitmap(result);
				cacheBitmapIfNeed(result);
			}
		}

		private void fillViewWithBitmap(Bitmap result) {
			List<WeakReference<ImageView>> values = mItems.get(mUrl);
			for (WeakReference<ImageView> ivWeakRef : values) {
				ImageView imageView = ivWeakRef.get();
				if (imageView != null) {
					Log.i("ImageCacheManager", "网络上下载图片，填充图片到imageView");
					imageView.setImageBitmap(result);
				}
			}
		}

		private void cacheBitmapIfNeed(Bitmap result) {
			Context context = mContextWeakRef.get();
			if (context != null && result != null) {
				if (mMemoryCach) {
					Log.i("ImageCacheManager", "缓存网络上下载的图片到内存");
					ImageMemoryCache memoryCache = new ImageMemoryCache(context);
					if (mRecycleOnFinish) {
						memoryCache.addBitmapToCache(mUrl, result,
								mRecycleOnFinish);
						mRecycleOnFinish = false;
					} else {
						memoryCache.addBitmapToCache(mUrl, result);
					}
				}

				if (mDiskCach) {
					Log.i("ImageCacheManager", "缓存网络上下载的图片到本地文件");
					ImageFileCache fileCache = new ImageFileCache();
					fileCache.saveBitmap(result, mUrl);
				}
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
