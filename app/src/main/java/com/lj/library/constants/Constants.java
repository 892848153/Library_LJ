package com.lj.library.constants;


public interface Constants {

	String PREFERENCE_FILE = "preference_file";

	String CACHE_ROOT_DIR_NAME = "AppRootDir";

	/**
	 * 本地安装的旧APK文件的MD5值，跟网络接口返回的对比,如果不一样，说明本地安装的是被二次打包的apk
	 */
	String WEIBO_OLD_MD5 = "7dc46bd75c1042f943942a37d646afaa";

	/**
	 * 新APK文件正确的MD5值, 跟合成的APK文件的MD5值对比,一样的话则合成正确
	 */
	String WEIBO_NEW_MD5 = "c2da8edf4b796c1a393be38730ac93fe";

}
