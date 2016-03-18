package com.lj.library.http.okhttp;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by liujie_gyh on 16/3/12.
 */
public class SimpleCallback implements Callback {
    @Override
    public void onFailure(Call call, IOException e) {
        Logger.e(e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
