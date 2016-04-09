package com.lj.library.http.okhttp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp 的拦截器，为每个请求增加一些头部.
 * Created by jie.liu on 16/3/11.
 */
public class CommonHeadersInterceptor implements Interceptor {

    private static class InstanceHolder {
        private static final CommonHeadersInterceptor INSTANCE = new CommonHeadersInterceptor();
    }

    private CommonHeadersInterceptor() {
    }

    public static final CommonHeadersInterceptor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Connection", "keep-alive")  // 默认值 详情见 {@link HttpEngine#networkRequest()}
                .addHeader("Accept-Encoding", "gzip")   // 默认值 详情见 {@link HttpEngine#networkRequest()}
                .build();
        return chain.proceed(request);
    }
}
