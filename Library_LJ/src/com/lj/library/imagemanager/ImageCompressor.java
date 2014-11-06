package com.lj.library.imagemanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.view.View;

import com.lj.library.util.BitmapUtils;
import com.lj.library.util.IOStreamCloser;
import com.lj.library.util.LogUtil;

/**
 * 图片缩放类.
 * 
 * @time 2014年10月28日 上午11:12:24
 * @author jie.liu
 */
public class ImageCompressor {

	/**
	 * 放大或者缩小图片， 并对原图进行回收,采用{@link ThumbnailUtils}.
	 * <p/>
	 * 图片放大后，不紧占用的内存会变大，保存为文件时文件大小也会比原来的大. <br/>
	 * 图片缩小后，不紧占用的内存会变小，保存为文件时文件大小也会比原来的小.
	 * 
	 * @param srcBmp
	 * @param view
	 *            用来获取图片将要压缩成的大小
	 * @return 压缩后的图片
	 */
	public static Bitmap compressImage(Bitmap srcBmp, View view) {
		if (view == null) {
			throw new NullPointerException(
					"the view for getting the target width and height is null");
		}
		view.requestLayout();
		int width = view.getMeasuredWidth();
		int height = view.getMeasuredHeight();
		LogUtil.d(new Object(), "view's width:" + width + " height:" + height);
		if (width == 0 || height == 0) {
			throw new IllegalArgumentException("控件的宽高为0或者获取不到控件的宽高");
		}

		return compressImage(srcBmp, width, height);
	}

	/**
	 * 缩放图片， 并对原图进行回收,采用{@link ThumbnailUtils}.
	 * <p/>
	 * 图片放大后，不紧占用的内存会变大，保存为文件时文件大小也会比原来的大.<br/>
	 * 图片缩小后，不紧占用的内存会变小，保存为文件时文件大小也会比原来的小.
	 * 
	 * @param srcBmp
	 * @param targetWidth
	 *            必须大于0
	 * @param targetHeight
	 *            必须大于0
	 * @return
	 */
	public static Bitmap compressImage(Bitmap srcBmp, int targetWidth,
			int targetHeight) {
		if (targetWidth <= 0 || targetHeight <= 0) {
			throw new IllegalArgumentException(
					"targetWidth or targetHeight is less than or equal to zero");
		}

		Bitmap dest = ThumbnailUtils.extractThumbnail(srcBmp, targetWidth,
				targetHeight);
		return dest;
	}

	/**
	 * 放大或者缩小图片,采用矩阵的方式.
	 * <p/>
	 * 图片放大后，不紧占用的内存会变大，保存为文件时文件大小也会比原来的大.<br/>
	 * 图片缩小后，不紧占用的内存会变小，保存为文件时文件大小也会比原来的小.
	 * 
	 * @param srcPath
	 * @param targetWidth
	 *            必须大于0
	 * @param targetHeight
	 *            必须大于0
	 * @return
	 */
	public static Bitmap scaleImage(String srcPath, int targetWidth,
			int targetHeight) {
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
		return scaleImage(bitmap, targetWidth, targetHeight);
	}

	/**
	 * 放大或者缩小图片，采用矩阵的方式.
	 * <p/>
	 * 图片放大后，不紧占用的内存会变大，保存为文件时文件大小也会比原来的大.<br/>
	 * 图片缩小后，不紧占用的内存会变小，保存为文件时文件大小也会比原来的小.
	 * 
	 * @param srcBmp
	 * @param targetWidth
	 *            必须大于0
	 * @param targetHeight
	 *            必须大于0
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap srcBmp, int targetWidth,
			int targetHeight) {
		if (srcBmp == null) {
			throw new NullPointerException("the source bitmap is null");
		}
		if (targetWidth <= 0 || targetHeight <= 0) {
			throw new IllegalArgumentException(
					"targetWidth or targetHeight is less than or equal to zero");
		}

		int bitmapWidth = srcBmp.getWidth();
		int bitmapHeight = srcBmp.getHeight();

		// 缩放图片的尺寸
		float scaleWidth = (float) targetWidth / bitmapWidth;
		float scaleHeight = (float) targetHeight / bitmapHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 产生缩放后的Bitmap对象
		Bitmap resizeBitmap = Bitmap.createBitmap(srcBmp, 0, 0, bitmapWidth,
				bitmapHeight, matrix, false);
		BitmapUtils.recycleBitmap(srcBmp);

		return resizeBitmap;
	}

	/**
	 * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式.
	 * 
	 * @param srcPath
	 * @param targetWidth
	 *            必须大于0
	 * @param targetHeight
	 *            必须大于0
	 * @return
	 */
	public static Bitmap compressImageFromFile(String srcPath, int targetWidth,
			int targetHeight) {
		return compressImageFromFile(srcPath, targetWidth, targetHeight,
				Config.ARGB_8888);
	}

	/**
	 * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式.
	 * <p/>
	 * 特点: 通过设置采样率, 减少图片的像素, 达到对内存中Bitmap进行压缩
	 * 
	 * @param srcPath
	 *            目标文件的路径
	 * @param targetWidth
	 *            必须大于0
	 * @param targetHeight
	 *            必须大于0
	 * @param preferredConfig
	 *            不想设置可传入null， 默认{@link Config#ARGB_8888} <br/>
	 *            图片像素点的格式 取值有<br/>
	 *            {@link Config#ARGB_8888} 就是由4个8位组成即一个像素32位<br/>
	 *            {@link Config#ARGB_4444} 就是由4个4位组成即一个像素16位<br/>
	 *            {@link Config#RGB_565}
	 *            &nbsp;&nbsp;&nbsp;就是R为5位，G为6位，B为5位，一个像素16位<br/>
	 *            {@link Config#ALPHA_8} &nbsp;&nbsp;&nbsp;8位的位图，一个像素8位
	 * @return
	 */
	public static Bitmap compressImageFromFile(String srcPath, int targetWidth,
			int targetHeight, Config preferredConfig) {
		if (srcPath == null) {
			throw new NullPointerException("the source file's path is null");
		}
		if (targetWidth <= 0 || targetHeight <= 0) {
			throw new IllegalArgumentException(
					"targetWidth or targetHeight is less than or equal to zero");
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int originalWidth = newOpts.outWidth;
		int originalHeight = newOpts.outHeight;
		float width = targetWidth;
		float height = targetHeight;
		int scale = 1;
		if (originalWidth > originalHeight && originalWidth > height) {
			scale = (int) (originalWidth / height);
		} else if (originalWidth < originalHeight && originalHeight > width) {
			scale = (int) (originalHeight / width);
		}
		if (scale <= 0)
			scale = 1;
		newOpts.inSampleSize = scale;// 设置采样率
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
		if (preferredConfig != null) {
			newOpts.inPreferredConfig = preferredConfig;
		}

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

	/**
	 * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩，文件长度最大为100K.
	 * 
	 * @param bmp
	 * @param file
	 */
	public static void compressBmpToFile(Bitmap bmp, File file) {
		compressBmpToFile(bmp, file, 100);
	}

	/**
	 * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩,只对JPG格式有效.
	 * <p/>
	 * 特点是: File形式的图片大小确实被压缩了, 但是当你重新读取压缩后的file为 Bitmap时,<br/>
	 * 它占用的内存并没有改变,不能改变图片的像素点数.
	 * 
	 * @param bmp
	 *            将要保存的图片
	 * @param file
	 *            bitmap对象要保存到的目标文件
	 * @param maxFileLength
	 *            文件的最大长度，单位为"K"
	 */
	public static void compressBmpToFile(Bitmap bmp, File file,
			int maxFileLength) {
		if (bmp == null) {
			throw new NullPointerException("the bitmap is null");
		}
		if (file == null) {
			throw new NullPointerException("the target file is null");
		}
		if (maxFileLength < 0) {
			throw new IllegalArgumentException(
					"the max file length is less than zero");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > maxFileLength) {
			baos.reset();
			options -= 10;
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		IOStreamCloser.closeOutputStream(baos);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOStreamCloser.closeOutputStream(fos);
		}
	}

}
