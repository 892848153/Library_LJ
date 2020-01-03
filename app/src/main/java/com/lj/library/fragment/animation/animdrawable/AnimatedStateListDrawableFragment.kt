package com.lj.library.fragment.animation.animdrawable

import android.os.Bundle
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

/**
 * android 5.0加入AnimatedStateListDrawable。
 * androidx.appcompat:appcompat-resources:xxxx兼容包中有AnimatedStateListDrawableCompat共你使用兼容低版本
 */
class AnimatedStateListDrawableFragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.animated_state_list_drawable_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
    }
}