package com.lj.library.fragment.animation.objanim;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * LayoutTransition Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class LayoutTransitionFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.id_container)
    ViewGroup mViewGroup;
    private GridLayout mGridLayout;
    private int mVal;
    private LayoutTransition mTransition;
    @BindView(R.id.id_appear)
    CheckBox mAppear;
    @BindView(R.id.id_change_appear)
    CheckBox mChangeAppear;
    @BindView(R.id.id_disappear)
    CheckBox mDisAppear;
    @BindView(R.id.id_change_disappear)
    CheckBox mChangeDisAppear;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.layout_transition_fragment;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void initComp(Bundle savedInstanceState) {
        mAppear.setOnCheckedChangeListener(this);
        mChangeAppear.setOnCheckedChangeListener(this);
        mDisAppear.setOnCheckedChangeListener(this);
        mChangeDisAppear.setOnCheckedChangeListener(this);

        // 创建一个GridLayout
        mGridLayout = new GridLayout(mContext);
        // 设置每列5个按钮
        mGridLayout.setColumnCount(5);
        // 添加到布局中
        mViewGroup.addView(mGridLayout);
        // 默认动画全部开启
        mTransition = new LayoutTransition();
        mTransition.setAnimator(LayoutTransition.APPEARING, (mAppear
                .isChecked() ? ObjectAnimator.ofFloat(null, "scaleX", 0, 1) : null));
        mGridLayout.setLayoutTransition(mTransition);
    }

    @OnClick(R.id.add_btn)
    public void onClick(View v) {
        final Button button = new Button(mContext);
        button.setText(String.valueOf(++mVal));
        mGridLayout.addView(button, Math.min(1, mGridLayout.getChildCount()));
        button.setOnClickListener(mGridItemClickListener);
    }

    private View.OnClickListener mGridItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mGridLayout.removeView(v);
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.i(this, "onCheckedChanged");
        mTransition.setAnimator(LayoutTransition.APPEARING, mAppear.isChecked() ? ObjectAnimator.ofFloat(this, "scaleX", 0, 1) : null);
        mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mChangeAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.CHANGE_APPEARING) : null);
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, mDisAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.DISAPPEARING) : null);
        mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mChangeDisAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING) : null);
        mGridLayout.setLayoutTransition(mTransition);
    }

}
