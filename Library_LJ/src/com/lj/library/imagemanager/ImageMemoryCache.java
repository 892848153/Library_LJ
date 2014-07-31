package com.lj.library.imagemanager;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.lj.library.util.BitmapUtils;

public class ImageMemoryCache {
	/**
	 * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
	 */
	private static final int SOFT_CACHE_SIZE = 15; // 软引用缓存容量
	private static LruCache<String, Bitmap> mLruCache; // 硬引用缓存
	private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache; // 软引用缓存

	public ImageMemoryCache(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8; // 硬引用缓存容量，为系统可用内存的1/8
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null)
					return value.getRowBytes() * value.getHeight();
				else
					return 0;
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null)
					// 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
					mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				SOFT_CACHE_SIZE, 0.75f, true) {
			private static final long serialVersionUID = 6040103833179403725L;

			@Override
			protected boolean removeEldestEntry(
					Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 从缓存中获取图片
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap;
		// 先从硬引用缓存中获取
		synchronized (mLruCache) {
			bitmap = mLruCache.get(url);
			if (bitmap != null) {
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				// 其实当调用get方法的时候，LruCache在此方法中会把元素移到最前面
				mLruCache.remove(url);
				mLruCache.put(url, bitmap);
				return bitmap;
			}
		}
		// 如果硬引用缓存中找不到，到软引用缓存中找
		synchronized (mSoftCache) {
			SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
			if (bitmapReference != null) {
				bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 将图片移回硬缓存
					mLruCache.put(url, bitmap);
					mSoftCache.remove(url);
					return bitmap;
				} else {
					mSoftCache.remove(url);
				}
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存
	 */
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (mLruCache) {
				mLruCache.put(url, bitmap);
			}
		}
	}

	/**
	 * 
	 * @todo 把图片从硬缓存和软引用缓存中删除
	 * @time 2014年5月20日 下午4:15:07
	 * @author jie.liu
	 * @param url
	 */
	public void removeBitmpFromCache(String url) {
		synchronized (mLruCache) {
			mLruCache.remove(url);
		}

		synchronized (mSoftCache) {
			mSoftCache.remove(url);
		}
	}

	/**
	 * 
	 * @todo 清空内存缓存
	 * @time 2014-6-4 下午4:50:19
	 * @author liuzenglong163@gmail.com
	 */
	public void clearCache() {
		mSoftCache.clear();
		mLruCache.evictAll();
	}

	/**
	 * 
	 * 回收缓存中的图片资源.
	 * 
	 * @time 2014年7月16日 下午6:25:19
	 * @author liuzenglong163@gmail.com
	 */
	public void recycleCache() {
		for (Iterator<String> it = mSoftCache.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			SoftReference<Bitmap> bitmapSoftRef = mSoftCache.get(key);
			Bitmap bitmap = bitmapSoftRef.get();
			if (bitmap != null) {
				BitmapUtils.recycleBitmap(bitmap);
			}
		}
	}
}
