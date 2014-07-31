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

	public static <T> T from(String str, Class<T> t) {
		Gson gson = new Gson();
		return gson.fromJson(str, t);
	}

	public static <T> List<T> from(String str) {
		Gson gson = new Gson();
		return gson.fromJson(str, new TypeToken<List<T>>() {
		}.getType());
	}

	public static String to(Object src) {
		Gson gson = new Gson();
		return gson.toJson(src);
	}
}
