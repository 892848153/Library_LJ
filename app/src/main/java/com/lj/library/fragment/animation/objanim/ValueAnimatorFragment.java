package com.lj.library.fragment.animation.objanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by liujie_gyh on 15/9/28.
 */
public class ValueAnimatorFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.image_view)
    ImageView mImageView;

    private float mScreenHeight;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.value_animation_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    @OnClick({R.id.ver_anim_btn, R.id.parabola_anim_btn, R.id.fade_out_anim_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ver_anim_btn:
                verticalRun();
                break;
            case R.id.parabola_anim_btn:
                parabolaRun();
                break;
            case R.id.fade_out_anim_btn:
                fadeOutRun();
                break;
            default:
                throw new UnknownError("点击了未处理的位置");
        }
    }

    /**
     * 垂直动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void verticalRun() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, mScreenHeight - mImageView.getHeight());
        animator.setTarget(mImageView);
        animator.setDuration(1000);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mImageView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    /**
     * 抛物线
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void parabolaRun() {
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(2000);
        animator.setObjectValues(new PointF(0f, 0f));
        animator.setEvaluator(new TypeEvaluator<PointF>() {
            @Override
            public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
                PointF point = new PointF();
                point.x = 200 * fraction * 3;
                point.y = 0.5f * 100 * (fraction * 3) * (fraction * 3);
                return point;
            }
        });

        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                mImageView.setX(point.x);
                mImageView.setY(point.y);
            }
        });
    }

    /**
     * 淡出且删除
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void fadeOutRun() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mImageView, "alpha", 0.5f);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) mImageView.getParent();
                if (parent != null)
                    parent.removeView(mImageView);
            }
        });

        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup parent = (ViewGroup) mImageView.getParent();
                if (parent != null)
                    parent.removeView(mImageView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        anim.start();
    }

}
