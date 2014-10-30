package com.lj.library.imagemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lj.library.util.LogUtil;

/**
 * 图片缓存类.
 * 
 * @time 2014年5月16日 上午11:20:58
 * @author jie.liu
 */

public class ImageCacheManager {

	private final Context mContext;
	private final boolean mCachInMemory;
	private final boolean mCachInDisk;

	private final ImageMemoryCache mMemoryCache;
	private final ImageFileCache mFileCache;
	private final ImageGetFromHttp mHttpCache;

	private OnBitmapFromHttpListener mListener;

	/**
	 * 默认开启内存缓存与硬盘缓存.
	 * 
	 * @param context
	 */
	public ImageCacheManager(Context context) {
		this(context, true, true);
	}

	public ImageCacheManager(Context context, boolean cachInMemory,
			boolean cachInDisk) {
		mContext = context;
		mCachInMemory = cachInMemory;
		mCachInDisk = cachInDisk;
		mMemoryCache = new ImageMemoryCache(context);
		mFileCache = new ImageFileCache();
		mHttpCache = new ImageGetFromHttp(context, mCachInMemory, mCachInDisk);
	}

	public void setOnGetFromHttpListener(OnBitmapFromHttpListener listener) {
		mListener = listener;
	}

	/**
	 * 根据Url获取对应的图片，本地有缓存图片则返回图像对象.本地没有缓存则从服务器上下载，要获取服务器端的图片对象，请设置监听
	 * {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @return
	 * 
	 * @see #getBitmap(String, ImageView, int, int, boolean)
	 */
	public Bitmap getBitmap(String url) {
		return getBitmap(url, null);
	}

	/**
	 * 根据Url获取对应的图片，本地有缓存图片则返回图像对象.本地没有缓存则从服务器上下载，要获取服务器端的图片对象，请设置监听
	 * {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @return
	 * 
	 * @see #getBitmap(String, ImageView, int, int, boolean)
	 */
	public Bitmap getBitmap(String url, ImageView imageView) {
		return getBitmap(url, imageView, 0, 0);
	}

	/**
	 * 根据Url获取对应的图片，本地有缓存图片则返回图像对象，否则从服务器下载图片.
	 * <p/>
	 * 要获取服务器端的图片，请设置监听 {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @param imageView
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 * 
	 * @see #getBitmap(String, ImageView, int, int, boolean)
	 */
	public Bitmap getBitmap(String url, ImageView imageView, int targetWidth,
			int targetHeight) {
		return getBitmap(url, imageView, targetWidth, targetHeight, false);
	}

	/**
	 * 
	 * 根据Url获取对应的图片，本地有缓存图片则返回图像对象，否则从服务器下载图片.
	 * <p/>
	 * 要获取服务器端的图片，请设置监听 {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @param imageView
	 *            用来填充图片的控件， 不需要填充可传入null
	 * @param targetWidth
	 *            图片将要压缩的目标宽度
	 * @param targetHeight
	 *            图片将要压缩的目标高度
	 * @param recycleOnFinish
	 *            是否将其加入内存缓存中的特殊队列中，为true时，调用
	 *            {@link ImageCacheManager#recycleOnFinish()}将会回收队列中对象的资源
	 * @return 返回本地缓存图片的Bitmap对象，如果图片没有缓存在本地，<br/>
	 *         则返回null并开始下载图片,下载完毕后根据参数将图片在本地做缓存.<br/>
	 */
	public Bitmap getBitmap(String url, ImageView imageView, int targetWidth,
			int targetHeight, boolean recycleOnFinish) {
		if (TextUtils.isEmpty(url)) {
			throw new NullPointerException("url == null");
		}

		// 从内存缓存中获取图片
		Bitmap result = mMemoryCache.getBitmapFromCache(url);
		if (result == null) {
			// 文件缓存中获取
			result = mFileCache.getImage(url);
			if (result == null) {
				// 从网络获取
				mHttpCache.setOnGetFromHttpListener(mListener);
				if (targetWidth == 0 || targetHeight == 0) {
					result = mHttpCache.downloadBitmap(url, imageView);
				} else {
					result = mHttpCache.downloadBitmap(url, imageView,
							targetWidth, targetHeight);
				}
			} else {
				// 添加到内存缓存
				if (mCachInMemory) {
					mMemoryCache.addBitmapToCache(url, result, recycleOnFinish);
				}
			}
		}

		if (imageView != null && result != null) {
			imageView.setImageBitmap(result);
		}
		return result;
	}

	/**
	 * 清除特定的一张图片的内存缓存和文件缓存.
	 * 
	 * @param url
	 */
	public void removeBitmap(String url) {
		removeBitmapFromMem(url);
		removeBitmapFromDisk(url);
	}

	/**
	 * 清除特定的一张图片在内存中的缓存.
	 * 
	 * @param url
	 */
	public void removeBitmapFromMem(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return;
		}

		mMemoryCache.removeBitmpFromCache(url);
	}

	/**
	 * 清除特定的一张图片在硬盘上的缓存.
	 * 
	 * @param url
	 */
	public void removeBitmapFromDisk(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return;
		}

		mFileCache.removeFileFromCache(url);
	}

	/**
	 * 回收缓存中的不常用图片资源，慎用， 因为不常用的图片不代表没在用，如果这张图片还在显示，则程序会出问题.
	 */
	public void recycleMemCache() {
		mMemoryCache.recycleCache();
	}

	/**
	 * 回收标记
	 * {@link ImageCacheManager#getBitmap(String, ImageView, int, int, boolean)}
	 * 第五个参数为true的资源.
	 */
	public void recycleOnFinish() {
		mMemoryCache.recycleOnFinish();
	}

	/**
	 * 清空图片内存缓存和文件缓存.
	 */
	public void clearCache() {
		clearMemCache();
		clearFileCache();
	}

	/**
	 * 
	 * 清空图片内存缓存.
	 * 
	 * @see ImageCacheManager#clearCache()
	 */
	public void clearMemCache() {
		mMemoryCache.clearCache();
	}

	/**
	 * 
	 * 清空图片文件缓存.
	 * 
	 * @see ImageFileCache#clearCache()
	 */
	public void clearFileCache() {
		mFileCache.clearCache();
	}

	public interface OnBitmapFromHttpListener {

		/**
		 * 
		 * 下载图片时，未发现网络，运行在主线程.
		 * 
		 * @param url
		 *            需要下载的图片的url
		 */
		void onGetBitmapNetworkNotFound(String url);

		/**
		 * 
		 * 开始下载图片,运行在主线程.
		 * 
		 * @param url
		 *            需要下载的图片的url
		 */
		void onGetBitmapBegin(String url);

		/**
		 * 
		 * 图片下载完毕，运行在主线程.
		 * 
		 * @param url
		 *            需要下载的图片的url
		 * @param bitmap
		 *            下载的图片的对象
		 */
		void onGetBitmapOver(String url, Bitmap bitmap);

		/**
		 * 
		 * 下载图片出现异常,运行在子线程.
		 * 
		 * @param url
		 *            需要下载的图片的url
		 * @param e
		 *            下载图片抛出的异常
		 */
		void onGetBitmapError(String url, Exception e);

	}
}
