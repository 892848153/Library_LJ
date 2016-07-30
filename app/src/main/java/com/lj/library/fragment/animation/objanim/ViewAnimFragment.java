package com.lj.library.fragment.animation.objanim;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * View.animate() Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class ViewAnimFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.image_view)
    ImageView mBlueBall;
    private float mScreenHeight;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_anim_fragment, null);
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
                .y(mScreenHeight / 2).setDuration(1000)
                // need API 12
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(this, "START");

                    }
                    // need API 16
                }).withEndAction(new Runnable() {

            @Override
            public void run() {
                LogUtil.i(this, "START");
                mActivity.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void run() {
                        mBlueBall.setY(0);
                        mBlueBall.setAlpha(1.0f);
                    }
                });
            }
        }).start();
    }

}
