package com.lj.library.http.okhttp;

import okhttp3.OkHttpClient;

/**
 * OkHttp的管理器
 * Created by jie.liu on 16/3/11.
 */
public enum OkHttpManager {

    INSTANCE;

    private static OkHttpClient sOkHttpClient;

    private static final long HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    static {
        // Interceptor有两种,详情见https://github.com/square/okhttp/wiki/Interceptors
        // 开启缓存响应数据功能  http://www.devtf.cn/?p=1264
//        final File baseDir = new File();
        sOkHttpClient = new OkHttpClient.Builder()
//                .cache(new Cache(baseDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
                .addInterceptor(CommonHeadersInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance()).build();
    }

    public OkHttpClient getClient() {
        return sOkHttpClient;
    }
}
