package com.lj.library.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscription;
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

    /**
     * @param tag       可以是{@link Class}类型的对象，如果不是则自动将其转换成对应的{@link Class}类型对象
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> eventType) {
        Object opTag = optimizeTag(tag);
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(opTag);
        if (null == subjectWrapperList) {
            subjectWrapperList = new ArrayList<>();
            mSubjectMapper.put(opTag, subjectWrapperList);
        }

        for (SubjectWrapper subjectWrapper : subjectWrapperList) {
            if (subjectWrapper.getEventType() == eventType) {
                return subjectWrapper.getSubject();
            }
        }

        Subject<T, T> subject = new SerializedSubject<>(PublishSubject.<T>create());
        subjectWrapperList.add(new SubjectWrapper(eventType, subject));
        return subject;
    }

    /**
     * 移除tag下所有的观察者.
     *
     * @param tag
     */
    public void unregister(@NonNull Object tag) {
        unregister(tag, null);
    }

    /**
     * 移除tag下具体的观察者.<p/>
     *
     * 不需要调用{@link Subscription#unsubscribe()}也不会造成内存泄漏，
     * 先看持有关系: {@link Observable}-> {@link rx.Subscriber} ->{@link android.app.Activity}/{@link android.support.v4.app.Fragment}
     * 一旦我们的RxBus不再持有{@link Observable}，由于{@link Observable}的{@link rx.Observable.OnSubscribe}
     * 中不会有耗时的工作，所以{@link Observable}可以很快被回收
     *
     * @param tag
     * @param observable 为null则移除tag下所有的观察者
     */
    public void unregister(@NonNull Object tag, Observable observable) {
        Object opTag = optimizeTag(tag);
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(opTag);
        if (subjectWrapperList != null && !subjectWrapperList.isEmpty()) {
            if (observable == null) {
                mSubjectMapper.remove(opTag);
            } else {
                unregister(subjectWrapperList, observable);
            }

            if (subjectWrapperList.isEmpty()) {
                mSubjectMapper.remove(opTag);
            }
        }
    }

    private void unregister(List<SubjectWrapper> subjectWrapperList, Observable observable) {
        for (SubjectWrapper subjectWrapper : subjectWrapperList) {
            if (subjectWrapper.getSubject() == observable) {
                subjectWrapperList.remove(subjectWrapper);
            }
        }
    }

    /**
     * @param tag     是{@link Class}类型的对象，如果不是则自动将其转换成对应的{@link Class}类型对象
     * @param content
     * @param <T>
     */
    public <T> void post(@NonNull Object tag, @NonNull T content) {
        List<SubjectWrapper> subjectWrapperList = mSubjectMapper.get(optimizeTag(tag));
        if (subjectWrapperList != null && !subjectWrapperList.isEmpty()) {
            dispatchPost(content, subjectWrapperList);
        }
    }

    /**
     * 如果tag是{@link Class}类型对象则直接返回，如果不是则将tag转换成其对应的{@link Class}对象。
     * {@link Object#getClass()}和Object.class是想等的。
     *
     * @param tag
     * @return
     */
    private Object optimizeTag(@NonNull Object tag) {
        if (tag instanceof Class) {
            return tag;
        }
        return tag.getClass();
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
