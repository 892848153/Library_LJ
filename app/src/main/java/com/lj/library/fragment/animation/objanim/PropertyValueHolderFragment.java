package com.lj.library.fragment.animation.objanim;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * PropertyValueHolder Demo.
 * Created by liujie_gyh on 15/10/17.
 */
public class PropertyValueHolderFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.image_view)
    ImageView mImageView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.property_value_holder_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @OnClick(R.id.start_anim_btn)
    public void onClick(View v) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(mImageView, pvhX, pvhY, pvhZ).setDuration(1000).start();
    }
}
