package com.lj.library.http.okhttp;

import com.lj.library.util.SDCardUtil;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * OkHttp的管理器
 * Created by jie.liu on 16/3/11.
 */
public enum OkHttpManager {

    INSTANCE;

    private static OkHttpClient sOkHttpClient;

    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final String OKHTTP_CACHE_DIR = "okhttpCache";

    static {
        // Interceptor有两种,详情见https://github.com/square/okhttp/wiki/Interceptors
        // 开启缓存响应数据功能  http://www.devtf.cn/?p=1264
        final File baseDir = new File(SDCardUtil.getSDPath(), OKHTTP_CACHE_DIR);
        sOkHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(baseDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
                .addInterceptor(CommonHeadersInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance()).build();
    }

    public OkHttpClient getClient() {
        return sOkHttpClient;
    }
}
