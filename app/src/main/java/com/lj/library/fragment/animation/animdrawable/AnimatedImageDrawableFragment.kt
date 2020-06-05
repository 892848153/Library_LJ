package com.lj.library.fragment.animation.animdrawable

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.animated_image_drawable_fragment.*

/**
 * android 9/Pie才加入的AnimatedImageDrawable,用于绘制和显示 GIF 和 WebP 动画图像
 * Created by liujie on 2020-06-05.
 */
class AnimatedImageDrawableFragment: BaseFragment() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun initComp(savedInstanceState: Bundle?) {
        // ImageDecoder是android 9/Pie才加入的，可提供现代化的图像解码方法。
        // 使用该类取代 BitmapFactory 和 BitmapFactory.Options API。
        val decodedAnimation = ImageDecoder.decodeDrawable(
                ImageDecoder.createSource(resources, R.drawable.splash))

        iv.setImageDrawable(decodedAnimation)

        // Prior to start(), the first frame is displayed.
        (decodedAnimation as? AnimatedImageDrawable)?.let {
            it.repeatCount = AnimatedImageDrawable.REPEAT_INFINITE
            it.isAutoMirrored = true // android:autoMirrored	Indicates if the drawable needs to be mirrored when its layout direction is RTL (right-to-left).
            it.start()
        }
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.animated_image_drawable_fragment
    }
}