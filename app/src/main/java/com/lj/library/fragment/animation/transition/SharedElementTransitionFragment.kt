package com.lj.library.fragment.animation.transition

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import com.lj.library.R
import com.lj.library.activity.MainActivity
import com.lj.library.activity.SecondTransitionActivity
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.shared_element_transition_fragment.*

/**
 * Created by liujie on 2020-01-11.
 */
class SharedElementTransitionFragment : BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.shared_element_transition_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        iv_1.setOnClickListener { jumpToActivity(iv_1, tv_1, R.drawable.img0003) }
        iv_2.setOnClickListener { jumpToActivity(iv_2, tv_1, R.drawable.img0004) }
        iv_3.setOnClickListener { jumpToFragment(iv_3, tv_2, R.drawable.img0005) }
        iv_4.setOnClickListener { jumpToFragment(iv_4, tv_2, R.drawable.img0009) }

//        enterTransition = Fade().apply { duration = 300 }
//        returnTransition = enterTransition
//        sharedElementEnterTransition = ChangeBounds().apply { duration = 2000 }
//        sharedElementReturnTransition = ChangeBounds().apply { duration = 2000 }
    }

    private fun jumpToActivity(iv: ImageView, tv: TextView, imgSrc: Int) {
        Intent(mContext, SecondTransitionActivity::class.java).apply {
            putExtra(SecondTransitionActivity.PARAM_IMG, imgSrc)
            putExtra(SecondTransitionActivity.PARAM_TV, tv.text)
        }.let {
            (mContext as MainActivity).startActivityWithSharedElementsFromFragment(this, it,
                    Pair(iv, SecondTransitionActivity.TRANSITION_NAME_IMG),
                    Pair(tv, SecondTransitionActivity.TRANSITION_NAME_TV))
        }
    }

    private fun jumpToFragment(iv: ImageView, tv: TextView, imgSrc: Int) {
        // 给自己的View设置transitionName
        ViewCompat.setTransitionName(iv, iv.id.toString())
        ViewCompat.setTransitionName(tv, tv.id.toString())

        SecondTransitionFragment().apply {
            // 需要手动设置transition, 不然Fragment是没有transition可以应用的
            enterTransition = Fade().apply { duration = 200 }
            returnTransition = enterTransition
            sharedElementEnterTransition = ChangeBounds().apply { duration = 2000 }

            arguments = Bundle().apply {
                putInt(SecondTransitionActivity.PARAM_IMG, imgSrc)
                putString(SecondTransitionActivity.PARAM_TV, tv.text.toString())

                // 把transitionName传递过去
                putString(SecondTransitionActivity.TRANSITION_NAME_IMG, ViewCompat.getTransitionName(iv))
                putString(SecondTransitionActivity.TRANSITION_NAME_TV, ViewCompat.getTransitionName(tv))
            }
        }.also {
            fragmentManager!!.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .addToBackStack(null)
                    // 设置transitionName。 addSharedElement()不会给View设置transitionName，所以需要手动使用ViewCompat.setTransitionName方法设置
                    .addSharedElement(iv, ViewCompat.getTransitionName(iv)?:SecondTransitionActivity.TRANSITION_NAME_IMG)
                    .addSharedElement(tv, ViewCompat.getTransitionName(tv)?:SecondTransitionActivity.TRANSITION_NAME_TV)
                    .commit()
        }
    }
}