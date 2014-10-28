package com.lj.library.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO流关闭工具.
 * 
 * @time 2014年10月28日 下午3:45:16
 * @author jie.liu
 */
public class IOStreamCloser {

	public static void closeOutputStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
