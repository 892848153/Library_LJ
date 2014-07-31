package com.lj.library.imagemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lj.library.util.LogUtil;

/**
 * 图片缓存类
 * 
 * @time 2014年5月16日 上午11:20:58
 * @author jie.liu
 */

public class ImageCacheManager {
	private final Context mContext;

	private final ImageMemoryCache mMemoryCache;
	private final ImageFileCache mFileCache;
	private final ImageGetFromHttp mHttpCache;

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
	 * {@link OnBitmapFromHttpListener}
	 * 
	 * @time 2014年7月16日 下午9:14:29
	 * @author liuzenglong163@gmail.com
	 * @param url
	 * @return
	 */
	public Bitmap getBitmap(String url) {
		return getBitmap(url, null);
	}

	/**
	 * 
	 * 根据Url获取对应的图片，本地有混存图片则返回图像对象，要获取服务器端的图片，请设置监听
	 * {@link OnBitmapFromHttpListener}
	 * 
	 * @time 2014年5月16日 下午2:06:13
	 * @author jie.liu
	 * @param url
	 * @param imageView
	 *            用来填充图片的控件， 不需要填充可传入null
	 * @return 返回本地的图片的Bitmap对象，如果图片不在本地，则返回null，并开始下载图片将图片加入本地缓存
	 */
	public Bitmap getBitmap(String url, ImageView imageView) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return null;
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

	public void delBitmapFromMem(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return;
		}

		mMemoryCache.removeBitmpFromCache(url);
	}

	public void delBitmap(String url) {
		if (TextUtils.isEmpty(url)) {
			LogUtil.d(mContext, "图片Url地址为空");
			return;
		}

		mMemoryCache.removeBitmpFromCache(url);
		mFileCache.removeFileFromCache(url);
	}

	/**
	 * 
	 * 清空图片缓存
	 * 
	 * @time 2014-6-4 下午4:52:58
	 * @author liuzenglong163@gmail.com
	 */
	public void clearCache() {
		clearMemCache();
		clearFileCache();
	}

	/**
	 * 
	 * 清空图片内存缓存
	 * 
	 * @time 2014-6-4 下午4:51:17
	 * @author liuzenglong163@gmail.com
	 * @see ImageCacheManager#clearCache()
	 */
	public void clearMemCache() {
		new ImageMemoryCache(mContext).clearCache();
	}

	public void recycleMemCache() {
		new ImageMemoryCache(mContext).recycleCache();
	}

	/**
	 * 
	 * 清空图片文件缓存
	 * 
	 * @time 2014-6-4 下午4:52:07
	 * @author liuzenglong163@gmail.com
	 * @see ImageFileCache#clearCache()
	 */
	public void clearFileCache() {
		new ImageFileCache().clearCache();
	}

	public interface OnBitmapFromHttpListener {

		/**
		 * 
		 * 下载图片时，未发现网络
		 * 
		 * @time 2014年6月17日 下午6:10:35
		 * @author liuzenglong163@gmail.com
		 * @param url
		 */
		void onGetBitmapNetworkNotFound(String url);

		/**
		 * 
		 * @todo 开始下载图片
		 * @time 2014年5月22日 下午2:12:49
		 * @author jie.liu
		 * @param url
		 */
		void onGetBitmapBegin(String url);

		/**
		 * 
		 * @todo 图片下载完毕
		 * @time 2014年5月22日 下午2:13:02
		 * @author jie.liu
		 * @param url
		 * @param bitmap
		 */
		void onGetBitmapOver(String url, Bitmap bitmap);

		/**
		 * 
		 * 下载图片出现异常
		 * 
		 * @time 2014年6月17日 下午6:08:15
		 * @author liuzenglong163@gmail.com
		 * @param url
		 * @param e
		 */
		void onGetBitmapError(String url, Exception e);

	}

}
