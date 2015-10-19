package com.lj.library.fragment.animation.objanim;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

/**
 * LayoutTransition Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class LayoutTransitionFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private GridLayout mGridLayout;
    private int mVal;
    private LayoutTransition mTransition;

    private CheckBox mAppear, mChangeAppear, mDisAppear, mChangeDisAppear;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.layout_transition_fragment, null);
        initViews(view);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initViews(View rootView) {
        ViewGroup viewGroup = (ViewGroup) rootView.findViewById(R.id.id_container);

        mAppear = (CheckBox) rootView.findViewById(R.id.id_appear);
        mChangeAppear = (CheckBox) rootView.findViewById(R.id.id_change_appear);
        mDisAppear = (CheckBox) rootView.findViewById(R.id.id_disappear);
        mChangeDisAppear = (CheckBox) rootView.findViewById(R.id.id_change_disappear);

        mAppear.setOnCheckedChangeListener(this);
        mChangeAppear.setOnCheckedChangeListener(this);
        mDisAppear.setOnCheckedChangeListener(this);
        mChangeDisAppear.setOnCheckedChangeListener(this);
        rootView.findViewById(R.id.add_btn).setOnClickListener(this);

        // 创建一个GridLayout
        mGridLayout = new GridLayout(mActivity);
        // 设置每列5个按钮
        mGridLayout.setColumnCount(5);
        // 添加到布局中
        viewGroup.addView(mGridLayout);
        // 默认动画全部开启
        mTransition = new LayoutTransition();
        mTransition.setAnimator(LayoutTransition.APPEARING, (mAppear
                .isChecked() ? ObjectAnimator.ofFloat(null, "scaleX", 0, 1)
                : null));
        mGridLayout.setLayoutTransition(mTransition);
    }

    @Override
    public void onClick(View v) {
        final Button button = new Button(mActivity);
        button.setText((++mVal) + "");
        mGridLayout.addView(button, Math.min(1, mGridLayout.getChildCount()));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridLayout.removeView(button);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.i(this, "onCheckedChanged");
        mTransition = new LayoutTransition();
        mTransition.setAnimator(LayoutTransition.APPEARING, mAppear.isChecked() ? ObjectAnimator.ofFloat(this, "scaleX", 0, 1) : null);
        mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, mChangeAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.CHANGE_APPEARING) : null);
        mTransition.setAnimator(LayoutTransition.DISAPPEARING, mDisAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.DISAPPEARING) : null);
        mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, mChangeDisAppear.isChecked() ? mTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING) : null);
        mGridLayout.setLayoutTransition(mTransition);
    }
}
