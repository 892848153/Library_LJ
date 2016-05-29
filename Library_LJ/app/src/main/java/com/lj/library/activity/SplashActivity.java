package com.lj.library.activity;

import android.app.Activity;
import android.os.Bundle;

import com.lj.library.util.ContextUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by liujie_gyh on 16/5/29.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.just(1).delay(1500, TimeUnit.MILLISECONDS).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                ContextUtil.pushToActivity(SplashActivity.this, MainActivity.class);
                finish();
            }
        });
    }
}
