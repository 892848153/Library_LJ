package com.lj.library.fragment.tabhost;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

/**
 * FragmentTabHost的内容页.
 * Created by jie.liu on 15/11/4.
 */
public class FragmentA extends BaseFragment {

    private static final String TAG = FragmentA.class.getSimpleName();

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        LogUtil.i(this, "initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        return R.layout.tab_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        TextView tv = (TextView) mRootView.findViewById(R.id.tv);
        tv.setText(TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(this, "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(this, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.i(this, "onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i(this, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(this, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(this, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(this, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(this, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(this, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(this, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(this, "onDetach");
    }

}
