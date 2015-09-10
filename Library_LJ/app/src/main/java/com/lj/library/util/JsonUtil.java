package com.lj.library.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;

/**
 * Json解析，生成Json字符串类.
 * 
 * @time 2014年7月31日 下午5:56:01
 * @author jie.liu
 */
public class JsonUtil {

	private static Gson sGson = new Gson();

	public static <T> T fromJson(String str, Class<T> t) {
		return sGson.fromJson(str, t);
	}

	/**
	 * @param str
	 * @param type
	 *            sample: new TypeToken<List<T>>() { }.getType()
	 * @return
	 */
	public static <T> List<T> fromJson(String str, Type type) {
		return sGson.fromJson(str, type);
	}

	public static String toJson(Object src) {
		return sGson.toJson(src);
	}
}
