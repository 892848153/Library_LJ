package com.lj.library.fragment.rx;

import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.fragment.BaseFragment;
import com.orhanobut.logger.Logger;

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
    protected View onCreateView(LayoutInflater inflater) {
        executeTest();
        return null;
    }

    private void executeTest() {
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
}
