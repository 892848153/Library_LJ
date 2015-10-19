package com.lj.library.fragment.animation.objanim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

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

    private ImageView mImageView;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.object_animator_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        mImageView = (ImageView) rootView.findViewById(R.id.image_view);
        mImageView.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        final ObjectAnimator anim = ObjectAnimator.ofFloat(mImageView, "zhy", 1.0f, 0.2f)
                .setDuration(500);
        anim.start();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                mImageView.setAlpha(cVal);
                mImageView.setScaleX(cVal);
                mImageView.setScaleY(cVal);
            }
        });


    }
}
