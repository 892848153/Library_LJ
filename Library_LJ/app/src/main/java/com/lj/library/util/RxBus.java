package com.lj.library.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by liujie_gyh on 16/5/23.
 */
public class RxBus {

    private ConcurrentHashMap<Object, List<SubjectWrapper>> mSubjectMapper = new ConcurrentHashMap<>();

    public static final RxBus getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final RxBus INSTANCE = new RxBus();
    }

    private RxBus() {
    }

    private static final Action1<Throwable> DEFAULT_ON_ERROR = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Logger.e(throwable, "", "");
        }
    };

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> eventType, @NonNull Action1<T> onNext) {
        return register(tag, eventType, onNext, DEFAULT_ON_ERROR);
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> eventType, @NonNull Action1<T> onNext, @NonNull Action1<Throwable> onError) {
        Observable<T> observable = register(tag, eventType);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
        return observable;
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> eventType) {
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(tag);
        if (null == subjectWrapperList) {
            subjectWrapperList = new ArrayList<>();
            mSubjectMapper.put(tag, subjectWrapperList);
        }

        Subject<T, T> subject = new SerializedSubject<>(PublishSubject.<T>create());
        subjectWrapperList.add(new SubjectWrapper(eventType, subject));
        return subject;
    }

    public void unregister(@NonNull Object tag) {
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(tag);
        if (subjectWrapperList != null && !subjectWrapperList.isEmpty()) {
            mSubjectMapper.remove(tag);
        }
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(tag);
        if (subjectWrapperList != null && !subjectWrapperList.isEmpty()) {
            for (SubjectWrapper subjectWrapper : subjectWrapperList) {
                if (subjectWrapper.getSubject() == observable) {
                    subjectWrapperList.remove(subjectWrapper);
                }
            }

            if (subjectWrapperList.isEmpty()) {
                mSubjectMapper.remove(tag);
            }
        }
    }

    public <T> void post(@NonNull Object tag, @NonNull T content) {
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(tag);
        if (subjectWrapperList != null && !subjectWrapperList.isEmpty()) {
            dispatchPost(content, subjectWrapperList);
        }
    }

    private <T> void dispatchPost(@NonNull T content, List<SubjectWrapper> subjectWrapperList) {
        for (SubjectWrapper subjectWrapper : subjectWrapperList) {
            if (subjectWrapper.getEventType() == content.getClass()) {
                subjectWrapper.getSubject().onNext(content);
            }
        }
    }

    private class SubjectWrapper {

        private Class mEventType;

        private Subject mSubject;

        public SubjectWrapper(Class eventType, Subject subject) {
            mEventType = eventType;
            mSubject = subject;
        }

        public Class getEventType() {
            return mEventType;
        }

        public rx.subjects.Subject getSubject() {
            return mSubject;
        }
    }
}
