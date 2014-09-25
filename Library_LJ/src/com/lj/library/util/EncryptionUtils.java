package com.lj.library.util;

import java.security.MessageDigest;

/**
 * 一些加密，解密算法.
 * 
 * @time 2014年9月25日 下午3:20:21
 * @author jie.liu
 */
public class EncryptionUtils {

	/**
	 * MD5信息摘要算法.
	 * 
	 * @param s
	 * @return 返回32位16进制的字符串.
	 */
	public static final String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节串更新摘要
			mdInst.update(btInput);
			// 将指定的"字节串"进行信息摘要,获取到"128bit"的结果,即16byte
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				// 前四个bit转成一个16进制的字符
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				// 后四个bit转成一个16进制的字符
				str[k++] = hexDigits[byte0 & 0xf];
			}
			// 返回32个16进制的字符串
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
