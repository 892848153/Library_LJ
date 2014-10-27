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

	private final ImageMemoryCache mMemoryCache;
	private final ImageFileCache mFileCache;
	private final ImageGetFromHttp mHttpCache;
	private boolean mCachInMemory;
	private boolean mCachInDisk;

	private OnBitmapFromHttpListener mListener;

	public ImageCacheManager(Context context) {
		mContext = context;
		mMemoryCache = new ImageMemoryCache(context);
		mFileCache = new ImageFileCache();
		mHttpCache = new ImageGetFromHttp(context);
	}

	public void setOnGetFromHttpListener(OnBitmapFromHttpListener listener) {
		mListener = listener;
	}

	/**
	 * 根据Url获取对应的图片，本地有混存图片则返回图像对象，要获取服务器端的图片，请设置监听
	 * {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		return getBitmap(url, null);
	}

	/**
	 * 
	 * 根据Url获取对应的图片，本地有缓存图片则返回图像对象.
	 * <p/>
	 * 要获取服务器端的图片，请设置监听 {@link OnBitmapFromHttpListener}.
	 * 
	 * @param url
	 * @param imageView
	 *            用来填充图片的控件， 不需要填充可传入null
	 * @return 返回本地缓存图片的Bitmap对象，如果图片没有缓存在本地，<br/>
	 *         则返回null并开始下载图片,下载完毕后自动将图片在本地做缓存.<br/>
	 */
	public Bitmap getBitmap(String url, ImageView imageView) {
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
				result = mHttpCache.downloadBitmap(url, imageView);
			} else {
				// 添加到内存缓存
				mMemoryCache.addBitmapToCache(url, result);
			}
		}

		if (imageView != null && result != null) {
			imageView.setImageBitmap(result);
		}
		return result;
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
	 * 清除特定的一张图片的缓存.
	 * 
	 * @param url
	 */
	public void removeBitmap(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return;
		}

		mMemoryCache.removeBitmpFromCache(url);
		mFileCache.removeFileFromCache(url);
	}

	public void recycleMemCache() {
		new ImageMemoryCache(mContext).recycleCache();
	}

	/**
	 * 清空图片缓存.
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
		new ImageMemoryCache(mContext).clearCache();
	}

	/**
	 * 
	 * 清空图片文件缓存.
	 * 
	 * @see ImageFileCache#clearCache()
	 */
	public void clearFileCache() {
		new ImageFileCache().clearCache();
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
