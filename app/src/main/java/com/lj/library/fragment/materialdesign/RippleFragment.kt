package com.lj.library.fragment.materialdesign

import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.ripple_fragment.*

/**
 * Created by liujie on 2020-01-17.
 */
class RippleFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {
        (iv_3333.background as RippleDrawable).apply {
            setHotspot(iv_3333.pivotX, iv_3333.pivotY)
        }
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.ripple_fragment
    }
}