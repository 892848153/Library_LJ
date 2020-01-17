package com.lj.library.fragment

import android.os.Bundle
import com.lj.library.R
import kotlinx.android.synthetic.main.content_fragment.*

/**
 * Created by liujie on 2020-01-03.
 */
class ContentFragment(val content: String? = null): BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.content_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        content?.let {
            tv.text = it
        }
    }

}