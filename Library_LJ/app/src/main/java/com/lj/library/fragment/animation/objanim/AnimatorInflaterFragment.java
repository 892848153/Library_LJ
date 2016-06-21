package com.lj.library.fragment.animation.objanim;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * AnimatorInflater Demo
 * Created by liujie_gyh on 15/10/17.
 */
public class AnimatorInflaterFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.image_view)
    ImageView mImageView;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.animator_inflater_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @OnClick({R.id.scale_x_btn, R.id.scale_xy_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scale_x_btn:
                scaleX();
                break;
            case R.id.scale_xy_btn:
                scaleXandScaleY();
                break;
            default:
                throw new UnknownError("点击了未处理的控件");
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void scaleX() {
        Animator animator = AnimatorInflater.loadAnimator(mActivity, R.animator.scalex);
        mImageView.setPivotY(mImageView.getHeight() / 2);
        mImageView.setPivotX(mImageView.getWidth() / 2);
        animator.setTarget(mImageView);
        animator.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void scaleXandScaleY() {
        Animator animator = AnimatorInflater.loadAnimator(mActivity, R.animator.scale);
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        animator.setTarget(mImageView);
        animator.start();
    }
}
