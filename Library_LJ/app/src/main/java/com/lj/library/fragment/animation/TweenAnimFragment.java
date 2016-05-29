package com.lj.library.fragment.animation;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 15/9/9.
 */
public class TweenAnimFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.tween_anim_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        mImageView = (ImageView) rootView.findViewById(R.id.image_view);
        rootView.findViewById(R.id.alpha_anim_btn).setOnClickListener(this);
        rootView.findViewById(R.id.rotate_anim_btn).setOnClickListener(this);
        rootView.findViewById(R.id.scale_anim_btn).setOnClickListener(this);
        rootView.findViewById(R.id.translate_anim_btn).setOnClickListener(this);
        rootView.findViewById(R.id.anim_set_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alpha_anim_btn:
                alphaImpl();
                break;
            case R.id.rotate_anim_btn:
                rotateImpl();
                break;
            case R.id.scale_anim_btn:
                scaleImpl();
                break;
            case R.id.translate_anim_btn:
                translateImpl();
                break;
            case R.id.anim_set_btn:
                setAll();
                break;
            default:
                try {
                    throw new Exception("发生了未知的点击事件");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 透明动画.
     */
    private void alphaImpl() {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.alpha_demo);
        mImageView.startAnimation(animation);

//        AlphaAnimation alphaAnimation = createAlphaAnimation();
//        mImageView.startAnimation(alphaAnimation);
    }

    // 旋转动画
    private void rotateImpl() {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.rotate_demo);
        mImageView.startAnimation(animation);

//        RotateAnimation rotateAnimation = createRotateAnimation();
//        mImageView.startAnimation(rotateAnimation);
    }

    // 缩放动画
    private void scaleImpl() {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_demo);
        mImageView.startAnimation(animation);

//        ScaleAnimation scaleAnimation = createScaleAnimation();
//        mImageView.startAnimation(scaleAnimation);
    }

    // 移动效果
    private void translateImpl() {
        // XML文件
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.translate_demo);
        animation.setRepeatCount(Animation.INFINITE);//循环显示
        mImageView.startAnimation(animation);

        /*
         * 第一种 mImageView.setAnimation(animation); animation.start();
         */
        // 第二种

        // Java代码
//        TranslateAnimation translateAnimation = createTranslateAnimation();
//        mImageView.startAnimation(translateAnimation);
    }

    // 综合实现set_demo.xml中的动画
    private void setAll() {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.set_demo);
        mImageView.startAnimation(animation);

//        AnimationSet animationSet = new AnimationSet(false);
//
//        Animation alphaAnimation = createAlphaAnimation();
//        Animation rotateAnimation = createRotateAnimation();
//        Animation scaleAnimation = createScaleAnimation();
//        Animation translateAnimation = createTranslateAnimation();
//
//        animationSet.addAnimation(alphaAnimation);
//        animationSet.addAnimation(rotateAnimation);
//        animationSet.addAnimation(scaleAnimation);
//        animationSet.addAnimation(translateAnimation);
//
//        mImageView.startAnimation(animationSet);
    }

    private AlphaAnimation createAlphaAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        setAnimationDuration(alphaAnimation);
        return alphaAnimation;
    }

    private RotateAnimation createRotateAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f);
        setAnimationDuration(rotateAnimation);
        return rotateAnimation;
    }

    private ScaleAnimation createScaleAnimation() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1.5f, 0.2f, 1.5f);
        setAnimationDuration(scaleAnimation);
        return scaleAnimation;
    }

    @NonNull
    private TranslateAnimation createTranslateAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 200, 0, 0);
        setAnimationDuration(translateAnimation);
        return translateAnimation;
    }

    private void setAnimationDuration(Animation animation) {
        animation.setDuration(2000);
    }
}
