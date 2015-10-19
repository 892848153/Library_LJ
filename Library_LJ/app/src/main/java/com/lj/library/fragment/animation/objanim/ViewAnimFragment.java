package com.lj.library.fragment.animation.objanim;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

/**
 * View.animate() Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class ViewAnimFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mBlueBall;
    private float mScreenHeight;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.view_anim_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mBlueBall = (ImageView) rootView.findViewById(R.id.image_view);
        rootView.findViewById(R.id.view_anim_btn).setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
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
