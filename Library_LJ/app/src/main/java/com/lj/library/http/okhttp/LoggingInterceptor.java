package com.lj.library.http.okhttp;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp打印日志的拦截器
 * Created by jie.liu on 16/3/11.
 */
public class LoggingInterceptor implements Interceptor {

    private static class InstanceHolder {
        private static final LoggingInterceptor INSTANCE = new LoggingInterceptor();
    }

    private LoggingInterceptor() {
    }

    public static final LoggingInterceptor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Logger.i(String.format("Sending request %s on %s%n%s%n%s",
                request.url(), chain.connection(), request.headers(),
                request.body() == null ? "" : request.body().toString()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Logger.i(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        Logger.json(response.body() == null ? "" : response.body().string());

        return response;
    }
}