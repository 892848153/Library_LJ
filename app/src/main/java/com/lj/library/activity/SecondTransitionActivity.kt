package com.lj.library.activity

import android.os.Bundle
import androidx.core.view.ViewCompat
import com.lj.library.R
import com.lj.library.activity.base.BaseActivity2
import com.lj.library.databinding.SecondTransitionActivityBinding
import kotlinx.android.synthetic.main.second_transition_activity.*

/**
 * Created by liujie on 2020-01-10.
 */
class SecondTransitionActivity: BaseActivity2<SecondTransitionActivityBinding>() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.second_transition_activity
    }

    override fun initComp(savedInstanceState: Bundle?) {
        tv_detail.text = "这是${intent.getStringExtra(PARAM_TV)}的详细数据"
        iv_detail.setImageResource(intent.getIntExtra(PARAM_IMG, R.drawable.jugg))

        ViewCompat.setTransitionName(tv_detail, TRANSITION_NAME_TV)
        ViewCompat.setTransitionName(iv_detail, TRANSITION_NAME_IMG)

        btn_finish_after_transition.setOnClickListener {
            supportFinishAfterTransition()
        }

        showContentLayout()
    }

    companion object {
        const val PARAM_IMG = "parameter_img"
        const val PARAM_TV = "parameter_tv"

        const val TRANSITION_NAME_IMG = "img_detail"
        const val TRANSITION_NAME_TV = "tv_detail"
    }
}