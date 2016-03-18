package com.lj.library.http.okhttp;

import okhttp3.OkHttpClient;

/**
 * OkHttp的管理器
 * Created by jie.liu on 16/3/11.
 */
public enum OkHttpManager {

    INSTANCE;

    private static OkHttpClient sOkHttpClient;

    static {
        // Interceptor有两种,详情见https://github.com/square/okhttp/wiki/Interceptors
        sOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(CommonHeadersInterceptor.getInstance())
                .addInterceptor(LoggingInterceptor.getInstance()).build();
    }

    public OkHttpClient getClient() {
        return sOkHttpClient;
    }
}
