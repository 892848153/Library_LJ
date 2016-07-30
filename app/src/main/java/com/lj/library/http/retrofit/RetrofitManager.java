package com.lj.library.http.retrofit;

import com.lj.library.http.okhttp.OkHttpManager;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jie.liu on 16/3/11.
 */
public enum RetrofitManager {

    INSTANCE;

    private static final String BASE_URL = "http://www.mangocity.com";

    private static Retrofit sRetrofit;

    private static Retrofit sRetrofitWithRxJava;

    static {
        sRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpManager.INSTANCE.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sRetrofitWithRxJava = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpManager.INSTANCE.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static <T> T create(Class<T> cls) {
        return sRetrofit.create(cls);
    }

    public static <T> T createWithRxJava(Class<T> cls) {
        return sRetrofitWithRxJava.create(cls);
    }
}
