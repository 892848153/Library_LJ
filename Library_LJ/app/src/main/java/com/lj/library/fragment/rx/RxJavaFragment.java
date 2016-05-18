package com.lj.library.fragment.rx;

import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 16/4/29.
 */
public class RxJavaFragment extends BaseFragment {

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        executTest();
        return null;
    }

    private void executTest() {
        simpleDemo();
    }

    private void simpleDemo() {

    }
}
