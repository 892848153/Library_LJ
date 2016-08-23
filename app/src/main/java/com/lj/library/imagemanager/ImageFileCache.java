package com.lj.library.imagemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.lj.library.constants.Constants;
import com.lj.library.util.EncryptionUtils;
import com.lj.library.util.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 文件缓存.
 * 
 * @time 2014年10月30日 上午10:06:15
 * @author jie.liu
 */
public class ImageFileCache {
	private static final String CACHDIR = Constants.APP_ROOT_DIR
			+ "/ImageCache";
	private static final String WHOLESALE_CONV = ".cach";

	private static final int MB = 1024 * 1024;
	private static final int CACHE_SIZE = 1000;
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

	public ImageFileCache() {
		// 清理文件缓存
		removeCacheIfNecessary(getDirectory());
	}

	/**
	 * 从缓存中获取图片.
	 * 
	 * @param url
	 **/
	public Bitmap getImage(final String url) {
		final String path = getDirectory() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileTime(path);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 将图片存入文件缓存.
	 * 
	 * @param bm
	 *            需要保存的对象
	 * @param url
	 *            用来产生文件名
	 **/
	public void saveBitmap(Bitmap bm, String url) {
		if (bm == null) {
			return;
		}
		// 判断sdcard上的空间
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			// SD空间不足
			return;
		}

		String filename = convertUrlToFileName(url);
		String dir = getDirectory();
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException");
		}
	}

	/**
	 * 
	 * 清空文件缓存
	 */
	public void clearCache() {
		removeCacheIfNecessary(getDirectory());
	}

	/**
	 * 计算存储目录下的文件大小，
	 * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
	 * 那么删除40%最近没有被使用的文件
	 */
	private boolean removeCacheIfNecessary(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return true;
		}
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}

		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(WHOLESALE_CONV)) {
				dirSize += files[i].length();
			}
		}

		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			for (int i = 0; i < removeFactor; i++) {
				if (files[i].getName().contains(WHOLESALE_CONV)) {
					files[i].delete();
				}
			}
		}

		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}

		return true;
	}

	/**
	 * 修改文件的最后修改时间 .
	 * 
	 **/
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/**
	 * 计算sdcard上的剩余空间.
	 * 
	 **/
	@SuppressWarnings("deprecation")
	private int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/**
	 * 将url转成文件名,文件名中不要有乱七八糟的符号，不然在低端机中创建文件会抛出异常.
	 * 
	 **/
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		String original = strs[strs.length - 1];
		return EncryptionUtils.getMD5(original) + WHOLESALE_CONV;
	}

	/**
	 * 获得缓存目录 .
	 * 
	 **/
	private String getDirectory() {
		String dir = StorageUtils.getSDCardPath() + "/" + CACHDIR;
		return dir;
	}

	/**
	 * 根据文件的最后修改时间进行排序.
	 */
	private class FileLastModifSort implements Comparator<File> {
		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	/**
	 * 
	 * 删除图片在本地的外部缓存.
	 * 
	 * @param url
	 */
	public void removeFileFromCache(String url) {
		final String path = getDirectory() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
}
