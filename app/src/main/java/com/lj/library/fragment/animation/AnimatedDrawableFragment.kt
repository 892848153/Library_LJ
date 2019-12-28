package com.lj.library.fragment.animation

import android.os.Bundle
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

/**
 * 5.0开始 加入AnimatedStateListDrawable
 */
class AnimatedDrawableFragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.animated_drawable_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
    }
}