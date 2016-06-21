package com.lj.library.fragment.animation;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 帧动画.
 * Created by liujie_gyh on 15/9/9.
 */
public class FrameAnimFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.image_view)
    ImageView mImageView;

    @Bind(R.id.xml_btn)
    Button mXmlBtn;
    @Bind(R.id.java_btn)
    Button mJavaBtn;

    private AnimationDrawable mAnimDraw;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_anim_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {

    }

    @OnClick({R.id.xml_btn, R.id.java_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xml_btn:
                stopAnimIfIsRuning();
                //noinspection ResourceType
                mImageView.setBackgroundResource(R.anim.anim_list);
                mAnimDraw = (AnimationDrawable) mImageView.getBackground();
                mAnimDraw.start();
                break;
            case R.id.java_btn:
                stopAnimIfIsRuning();
                mAnimDraw = new AnimationDrawable();
                for (int i = 1; i <= 9; i++) {
                    //根据资源名称和目录获取R.java中对应的资源ID
                    int id = getResources().getIdentifier("img000" + i, "drawable", mActivity.getPackageName());
                    //根据资源ID获取到Drawable对象
                    Drawable drawable = getResources().getDrawable(id);
                    //将此帧添加到AnimationDrawable中
                    mAnimDraw.addFrame(drawable, 300);
                }
                mAnimDraw.setOneShot(false); //设置为loop
                mImageView.setBackgroundDrawable(mAnimDraw);  //将动画设置为ImageView背景
                mAnimDraw.start();   //开始动画
                break;
            default:
                LogUtil.e(this, "未处理的点击事件");
                break;
        }

    }

    private void stopAnimIfIsRuning() {
        if (mAnimDraw != null && mAnimDraw.isRunning()) {
            mAnimDraw.stop();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mAnimDraw != null && mAnimDraw.isRunning()) {
            mAnimDraw.stop();
            mAnimDraw = null;
        }
    }
}
