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
	private static LruCache<String, Bitmap> sLruCache; // 硬引用缓存
	private static LinkedHashMap<String, SoftReference<Bitmap>> sSoftCache; // 软引用缓存

	public ImageMemoryCache(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8; // 硬引用缓存容量，为系统可用内存的1/8
		sLruCache = new LruCache<String, Bitmap>(cacheSize) {
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
					sSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		sSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
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
	 * 从缓存中获取图片.
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap;
		// 先从硬引用缓存中获取
		synchronized (sLruCache) {
			bitmap = sLruCache.get(url);
			if (bitmap != null) {
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				// 其实当调用get方法的时候，LruCache在此方法中会把元素移到最前面
				sLruCache.remove(url);
				sLruCache.put(url, bitmap);
				return bitmap;
			}
		}
		// 如果硬引用缓存中找不到，到软引用缓存中找
		synchronized (sSoftCache) {
			SoftReference<Bitmap> bitmapReference = sSoftCache.get(url);
			if (bitmapReference != null) {
				bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 将图片移回硬缓存
					sLruCache.put(url, bitmap);
					sSoftCache.remove(url);
					return bitmap;
				} else {
					sSoftCache.remove(url);
				}
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存.
	 */
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sLruCache) {
				sLruCache.put(url, bitmap);
			}
		}
	}

	/**
	 * 
	 * 把图片从硬缓存和软引用缓存中删除
	 * 
	 * @param url
	 */
	public void removeBitmpFromCache(String url) {
		synchronized (sLruCache) {
			sLruCache.remove(url);
		}

		synchronized (sSoftCache) {
			sSoftCache.remove(url);
		}
	}

	/**
	 * 
	 * 清空内存缓存.
	 */
	public void clearCache() {
		sSoftCache.clear();
		sLruCache.evictAll();
	}

	/**
	 * 
	 * 回收缓存中的图片资源.
	 */
	public void recycleCache() {
		for (Iterator<String> it = sSoftCache.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			SoftReference<Bitmap> bitmapSoftRef = sSoftCache.get(key);
			Bitmap bitmap = bitmapSoftRef.get();
			if (bitmap != null) {
				BitmapUtils.recycleBitmap(bitmap);
			}
		}
	}
}
