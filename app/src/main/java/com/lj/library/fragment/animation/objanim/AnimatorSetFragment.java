package com.lj.library.fragment.animation.objanim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * AnimatorSet Demo.
 * Created by liujie_gyh on 15/9/28.
 */
public class AnimatorSetFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.image_view)
    ImageView mImageView;

    private AnimatorSet mTogetherRunAnimSet;

    private AnimatorSet mPlayWithAfterAnimSet;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.animator_set_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @OnClick({R.id.together_btn, R.id.play_with_after_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.together_btn:
                togetherRun();
                break;
            case R.id.play_with_after_btn:
                playWithAfter();
                break;
            default:
                throw new UnknownError("点击了未处理的控件");
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void togetherRun() {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mImageView, "scaleX", 1.0f, 2f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mImageView, "scaleY", 1.0f, 2f);

        if (mTogetherRunAnimSet == null) {
            mTogetherRunAnimSet = new AnimatorSet();
            mTogetherRunAnimSet.setDuration(2000);
            mTogetherRunAnimSet.setInterpolator(new LinearInterpolator());
            //两个动画同时执行
            mTogetherRunAnimSet.playTogether(anim1, anim2);
//		mTogetherRunAnimSet.playSequentially(items)
        }
        mTogetherRunAnimSet.start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void playWithAfter() {
        /**
         * anim1，anim2,anim3同时执行
         * anim4接着执行
         */
        if (mPlayWithAfterAnimSet == null) {
            mPlayWithAfterAnimSet = new AnimatorSet();
            float cx = mImageView.getX();

            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mImageView, "scaleX", 1.0f, 2f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(mImageView, "scaleY", 1.0f, 2f);
            ObjectAnimator anim3 = ObjectAnimator.ofFloat(mImageView, "x", cx, 0f);
            ObjectAnimator anim4 = ObjectAnimator.ofFloat(mImageView, "x", cx);

            mPlayWithAfterAnimSet.play(anim1).with(anim2);
            mPlayWithAfterAnimSet.play(anim2).with(anim3);
            mPlayWithAfterAnimSet.play(anim4).after(anim3);
            mPlayWithAfterAnimSet.setDuration(1000);
        }
        mPlayWithAfterAnimSet.start();
    }
}
