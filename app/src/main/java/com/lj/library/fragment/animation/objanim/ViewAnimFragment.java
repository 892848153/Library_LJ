package com.lj.library.fragment.animation.objanim;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * View.animate() Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class ViewAnimFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.image_view)
    ImageView mBlueBall;
    private float mScreenHeight;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.view_anim_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.view_anim_btn)
    public void onClick(View v) {
        // need API12
        mBlueBall.animate()//
                .alpha(0)//
                .y(mScreenHeight / 2)
                .setDuration(1000)
                // need API 12
                .withStartAction( mStartAction)
                // need API 16
                .withEndAction(mEndAction).start();
    }

    private Runnable mStartAction = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(this, "START");
        }
    };

    private Runnable mEndAction = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(this, "START");
            mActivity.runOnUiThread(mUpdateBlueBall);
        }
    };

    private Runnable mUpdateBlueBall = new Runnable() {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void run() {
            mBlueBall.setY(0);
            mBlueBall.setAlpha(1.0f);
        }
    };

}
