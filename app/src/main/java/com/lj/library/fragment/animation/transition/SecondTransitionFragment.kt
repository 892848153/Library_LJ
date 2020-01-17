package com.lj.library.fragment.animation.transition

import android.os.Bundle
import androidx.core.view.ViewCompat
import com.lj.library.R
import com.lj.library.activity.SecondTransitionActivity
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.second_transition_activity.*

/**
 * Created by liujie on 2020-01-16.
 */
class SecondTransitionFragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.second_transition_activity
    }

    override fun initComp(savedInstanceState: Bundle?) {
        tv_detail.text = "这是${arguments?.getString(SecondTransitionActivity.PARAM_TV)}的详细数据"
        iv_detail.setImageResource(arguments?.getInt(SecondTransitionActivity.PARAM_IMG, R.drawable.jugg)?:R.drawable.jugg)

        arguments?.getString(SecondTransitionActivity.TRANSITION_NAME_IMG)?.let {
            ViewCompat.setTransitionName(iv_detail, it)
        }
        arguments?.getString(SecondTransitionActivity.TRANSITION_NAME_TV)?.let {
            ViewCompat.setTransitionName(tv_detail, it)
        }

        btn_finish_after_transition.setOnClickListener {
            mContext.onBackPressed()
        }
    }

}