package com.lj.library.util;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	public static <T> List<T> fromJson(String str) {
		return sGson.fromJson(str, new TypeToken<List<T>>() {
		}.getType());
	}

	public static String toJson(Object src) {
		return sGson.toJson(src);
	}
}
