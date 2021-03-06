package com.lj.library.fragment.animation.objanim;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ObjectAnimator的demo.<p/>
 *
 *
 * 当对于属性值，只设置一个的时候，会认为当前对象该属性的值为开始（getPropName反射获取,
 * 然后设置的值为终点。如果设置两个，则一个为开始、一个为结束~~~	<BR/>
 *
 *动画更新的过程中，会不断调用setPropName更新元素的属性，所有使用ObjectAnimator更新某个属性
 * ，必须得有getter（设置一个属性值的时候）和setter方法~<BR/>
 *
 *2、如果你操作对象的该属性方法里面，比如上例的setRotationX如果内部没有调用view的重绘，
 * 则你需要自己按照下面方式手动调用。
 *
 * Created by liujie_gyh on 15/9/28.
 */
public class ObjectAnimatorFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.image_view)
    ImageView mImageView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.object_animator_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float cVal = (Float) animation.getAnimatedValue();
            mImageView.setAlpha(cVal);
            mImageView.setScaleX(cVal);
            mImageView.setScaleY(cVal);
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @OnClick(R.id.image_view)
    public void onClick(View v) {
        final ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.2f).setDuration(500);
        anim.addUpdateListener(mAnimatorUpdateListener);
        anim.start();
    }

}
