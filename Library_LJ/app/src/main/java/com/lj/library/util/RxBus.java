package com.lj.library.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by liujie_gyh on 16/5/23.
 */
public class RxBus {

    private ConcurrentHashMap<Object, List<Subject>> mSubjectMapper = new ConcurrentHashMap<>();

    public static final RxBus getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final RxBus INSTANCE = new RxBus();
    }

    private RxBus() {
    }

    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> cls) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            mSubjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject = new SerializedSubject<>(PublishSubject.<T>create());
        subjectList.add(subject);
        return subject;
    }

    public void unregister(@NonNull Object tag) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (subjectList != null && !subjectList.isEmpty()) {
            mSubjectMapper.remove(tag);
        }
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (subjectList != null) {
            subjectList.remove(observable);
            if (subjectList.isEmpty()) {
                mSubjectMapper.remove(tag);
            }
        }
    }

    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = mSubjectMapper.get(tag);
        if (subjectList != null && !subjectList.isEmpty()) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
    }

}
