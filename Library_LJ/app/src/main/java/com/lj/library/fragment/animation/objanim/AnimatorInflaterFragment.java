package com.lj.library.fragment.animation.objanim;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

/**
 * AnimatorInflater Demo
 * Created by liujie_gyh on 15/10/17.
 */
public class AnimatorInflaterFragment extends BaseFragment implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.animator_inflater_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        mImageView = (ImageView) rootView.findViewById(R.id.image_view);
        rootView.findViewById(R.id.scale_x_btn).setOnClickListener(this);
        rootView.findViewById(R.id.scale_xy_btn).setOnClickListener(this);
    }

    @Override
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
