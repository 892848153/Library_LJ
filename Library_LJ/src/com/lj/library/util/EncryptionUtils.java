package com.lj.library.util;

import java.security.MessageDigest;

import android.util.Base64;

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
	public static final String getMD5(String s) {
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

	/**
	 * Base64编码，android自带{@link Base64}这个类.
	 * 
	 * <pre>
	 * 一、编码规则
	 *       Base64编码的思想是是采用64个基本的ASCII码字符对数据进行重新编码。它将需要编码的数据拆分成字节
	 * 
	 * 数组。以3个字节为一组。按顺序排列24 位数据，再把这24位数据分成4组，即每组6位。再在每组的的最高位前
	 * 
	 * 补两个0凑足一个字节。这样就把一个3字节为一组的数据重新编码成了4个字节。当所要编码的数据的字节数不是
	 * 
	 * 3的整倍数，也就是说在分组时最后一组不够3个字节。这时在最后一组填充1到2个0字节。并在最后编码完成后在
	 * 
	 * 结尾添加1到2个 “=”。
	 * 
	 * 例：将对ABC进行BASE64编码：
	 * 
	 * 
	 * 1、首先取ABC对应的ASCII码值。A（65）B（66）C（67）；
	 * 2、再取二进制值A（01000001）B（01000010）C（01000011）；
	 *  3、然后把这三个字节的二进制码接起来（010000010100001001000011）；
	 * 4、 再以6位为单位分成4个数据块,并在最高位填充两个0后形成4个字节的编码后的值，（00010000）（00010100
	 * 
	 * ）（00001001）（00000011），其中蓝色部分为真实数据；
	 *  5、再把这四个字节数据转化成10进制数得（16）（20）（9）（3）；
	 *  6、最后根据BASE64给出的64个基本字符表，查出对应的ASCII码字符（Q）（U）（J）（D），这里的值实际就是
	 * 
	 * 数据在字符表中的索引。
	 * 
	 * 注：BASE64字符表：ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/
	 * 
	 * 二、解码规则
	 *       解码过程就是把4个字节再还原成3个字节再根据不同的数据形式把字节数组重新整理成数据。
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static String getBASE64(byte[] b) {
		return Base64.encodeToString(b, 0);
	}

	public static byte[] fromBASE64(String s) {
		return Base64.decode(s, 0);
	}
}
