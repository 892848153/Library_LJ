package com.lj.library.imagemanager;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;

import com.lj.library.util.BitmapUtils;
import com.lj.library.util.LogUtil;

public class ImageMemoryCache {
	/**
	 * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
	 */
	private static final int SOFT_CACHE_SIZE = 15; // 软引用缓存容量
	private static LruCache<String, Bitmap> sLruCache; // 硬引用缓存
	/**
	 * LinkedHashMap实现方式:内部保存一个双向链表， header指针指向一个空元素，每次put时都将元素插入在header指针前面
	 * 然后再根据removeEldestEntry()的返回值决定是否删除header指针后面的那个元素，即删除最旧的元素.
	 * get时，将取到的元素插在header指针的前面，再在原来的位置的指针删除
	 **/
	private static LinkedHashMap<String, SoftReference<Bitmap>> sSoftCache; // 软引用缓存

	/**
	 * 保存着将在onDestroy()中回收的对象，调用{@link #recycleCacheByFlag(String)} 或者
	 * {@link #recycleOnFinish()}方法来回收资源
	 **/
	private static Map<String, Map<String, Bitmap>> sCachWillRecycled;

	public ImageMemoryCache(Context context) {
		if (sLruCache == null || sSoftCache == null
				|| sCachWillRecycled == null) {
			initCachMap(context);
		}
	}

	private void initCachMap(Context context) {
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 8; // 硬引用缓存容量，为系统可用内存的1/8
		sLruCache = new LruCache<String, Bitmap>(cacheSize) {

			/**
			 * get(key)时，当不存在对应key的value时，会调用此方法产生一个新的value对象,默认返回null.
			 * 如果在调用此方法期间，有其他线程往缓存中加入一样key的value，则新产生的value将被抛弃，并会调用
			 * entryRemoved(false, key, createdValue, mapValue);一般不用重载此方法
			 */
			@Override
			protected Bitmap create(String key) {
				return super.create(key);
			}

			/**
			 * 计算每个对象的大小，用于计算所有缓存的大小
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null)
					return value.getRowBytes() * value.getHeight();
				else
					return 0;
			}

			/**
			 * 四种情况下会调用此函数<br/>
			 * 1、get(key)时，当不存在对应key的value时，会调用create(key)产生一个新的value对象,默认返回null
			 * 如果在调用create(key)期间，有其他线程往缓存中加入一样key的value，则新产生的value将被抛弃，并会调用
			 * entryRemoved(false, key, createdValue, mapValue);<br/>
			 * 2、put(key, value)时，如果先前key有对应的value，则覆盖之，然后调用entryRemoved(false,
			 * key, previous, value);previous为先前的value.
			 * 3、remove(key)时，如果存在对应key的value让其删除，则调用entryRemoved(false, key,
			 * previous, null);
			 * 4、trimToSize(maxSize)时，没删除一个对象，都会调用entryRemoved(false, key,
			 * previous, null);
			 */
			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null)
					// 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
					sSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		// 第二个参数表示当实际存储容量>=initialCapacity*initialCapacity，重新增加分配容量
		// accessOrder决定在取数据时是否对数据重新排列，即将取到的元素插在header指针的前面，再把原来位置的指针删除
		sSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				SOFT_CACHE_SIZE, 0.75f, true) {
			private static final long serialVersionUID = 6040103833179403725L;

			// put的时候，根据这个返回的结果决定是否删除header指针后面的元素
			@Override
			protected boolean removeEldestEntry(
					Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE) {
					return true;
				}
				return false;
			}
		};
		sCachWillRecycled = new HashMap<String, Map<String, Bitmap>>();
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

		// 如果软引用缓存中找不到，从可回收队列中查找
		synchronized (sCachWillRecycled) {
			for (Entry<String, Map<String, Bitmap>> entry : sCachWillRecycled
					.entrySet()) {
				Map<String, Bitmap> cache = entry.getValue();
				if (cache != null) {
					bitmap = cache.get(url);
					if (bitmap != null) {
						return bitmap;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存.
	 * 
	 * @param url
	 * @param bitmap
	 */
	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sLruCache) {
				sLruCache.put(url, bitmap);
			}
		}
	}

	/**
	 * 缓存图片到可回收缓存队列.
	 * 
	 * @param cacheFlag
	 *            可回收缓存队列标志
	 * @param url
	 * @param bitmap
	 */
	public void addBitmapToRecycleCache(String cacheFlag, String url,
			Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sCachWillRecycled) {
				boolean keyExists = sCachWillRecycled.containsKey(cacheFlag);
				if (keyExists) {
					Map<String, Bitmap> cache = sCachWillRecycled
							.get(cacheFlag);
					if (cache != null) {
						cache.put(url, bitmap);
					}
				} else {
					Map<String, Bitmap> cache = new HashMap<String, Bitmap>();
					cache.put(url, bitmap);
					sCachWillRecycled.put(cacheFlag, cache);
				}
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
	 * 清空内存缓存的引用，但不主动回收图片资源.
	 */
	public void clearCache() {
		sSoftCache.clear();
		sLruCache.evictAll();
	}

	/**
	 * 回收缓存中的不常用图片资源.
	 */
	public void recycleLessCommonCache() {
		Set<String> keySet = sSoftCache.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String key = it.next();
			SoftReference<Bitmap> bitmapSoftRef = sSoftCache.get(key);
			Bitmap bitmap = bitmapSoftRef.get();
			if (bitmap != null) {
				BitmapUtils.recycleBitmap(bitmap);
			}
		}
	}

	/**
	 * 回收用
	 * {@link ImageMemoryCache#addBitmapToRecycleCache(String, String, Bitmap)}
	 * 方法加入缓存的资源.
	 * 
	 */
	public void recycleOnFinish() {
		Set<String> keySet = sCachWillRecycled.keySet();
		LogUtil.d(this, "回收所有可回收资源队列，队列大小:" + keySet.size());
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String key = it.next();
			recycleCacheByFlag(key);
		}
		sCachWillRecycled.clear();
	}

	/**
	 * 回收特定缓存队列.
	 * 
	 * @param cacheFlag
	 *            可回收缓存队列标志
	 */
	public void recycleCacheByFlag(String cacheFlag) {
		Map<String, Bitmap> cache = sCachWillRecycled.get(cacheFlag);
		if (cache != null) {
			for (Entry<String, Bitmap> item : cache.entrySet()) {
				Bitmap bitmap = item.getValue();
				if (bitmap != null) {
					BitmapUtils.recycleBitmap(bitmap);
				}
			}
		} else {
			LogUtil.w(this, this.getClass().getSimpleName() + "-----找不到可回收缓存队列");
		}
		sCachWillRecycled.remove(cacheFlag);
	}
}
