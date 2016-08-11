package com.lj.library.fragment.rx;

import android.os.Bundle;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;

import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liujie_gyh on 16/4/29.
 */
public class RxJavaFragment extends BaseFragment {

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.rx_java_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @OnClick(R.id.rx_java_btn)
    public void runRxJavaDemo() {
        simpleDemo();
        complexDemo();
    }

    private void simpleDemo() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world");
                subscriber.onCompleted();
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Logger.i("onCompleted", "");
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.i("onError", "");
            }

            @Override
            public void onNext(String s) {
                Logger.i("onNext:%s", s);
            }
        });
    }

    /**
     * 线程切换参考 <a href="http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/1012/3572.html">RxJava 详解</a>
     */
    private void complexDemo() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("OnSubscribe<String>.call", "");
                subscriber.onNext("hello");
                subscriber.onNext("world");
                subscriber.onCompleted();
            }
        }).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        Logger.i("map(new Func1<String, Integer>()", "");
                        return 123;
                    }
                }).observeOn(Schedulers.computation())
                .flatMap(new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer integer) {
                        Logger.i("flatMap(new Func1<Integer, Observable<Long>>", "");
                        return Observable.just(1L, 2L, 3L);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Logger.i("subscribe.Action1.call", "");
                    }
                });
    }

    /**
     * 几个Subject类的区别见<a href="http://www.tuicool.com/articles/E32amy2">RxJava开发精要2</a>
     */
    @OnClick(R.id.rx_bus_btn)
    public void runRxBusDemo() {
        Observable<String> observable = RxBus.getInstance().register(this, String.class);
        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Logger.i("onCompleted", "");
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.e(throwable, "", "");
            }

            @Override
            public void onNext(String o) {
                Logger.i("onNext:%s", o);
            }
        });
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Logger.i("Action1.call:%s", s);
            }
        });
        RxBus.getInstance().post(this, 1);
        RxBus.getInstance().post(this, "hello");


        RxBus.getInstance().register(this, String.class, new Action1<String>() {
            @Override
            public void call(String s) {
                Logger.i(" RxBus.getInstance().register(this, String.class, new Action1<String>()", "");
            }
        });
        RxBus.getInstance().post(this, "world");

    }

}
