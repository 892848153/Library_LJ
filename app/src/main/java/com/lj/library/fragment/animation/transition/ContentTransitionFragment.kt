package com.lj.library.fragment.animation.transition

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.transition.Explode
import androidx.transition.Fade
import androidx.transition.Slide
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

/**
 * 页面切换的Transition， Activity/Fragment切换。
 *
 * ContentTransition/SharedElementTransition分别分为四大类，1. enter   2.exit   3.reenter   4.return
 *
 * 假设，两个页面 A和B
 * A启动B： A发生exit transition，B发生enter transition
 * B返回A： B发生return transition，A发生reenter transition
 *
 * return Transition默认是enter Transition的反转动画
 * reenter Transition默认是exit Transition的反转动画
 *
 * 定义页面切换的Transition可以在xml中也可以在代码中
 * xml中: 在styles.xml文件中的app主题下增加 <item name="android:windowXXXXTransition">@transition/explode</item>
 * 代码中:
 *       with(window) {
 *          // 这行得在setContentView()之前调用，其他的没要求
 *           requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
 *
 *           // set an exit transition
 *           XXXXTransition = Explode()
 *      }
 *
 * 跳转Activity使用：startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElements).toBundle());
 *
 * 如果没有sharedElements就不传.
 *
 * Created by liujie on 2020-01-10.
 */
class ContentTransitionFragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.content_transition_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        reenterTransition = Slide().apply {
            slideEdge = Gravity.START
            duration = 500 }
    }

    @OnClick(R.id.btn_1)
    fun fadeIn(view: View) {
        SecondTransitionFragment().apply {
            enterTransition = Fade().apply { duration = 500 }
            returnTransition = Explode().apply { duration = 500 }
        }.let {
            startFragment(it)
        }
    }

    @OnClick(R.id.btn_2)
    fun explodeIn(view: View) {
        SecondTransitionFragment().apply {
            enterTransition = Explode().apply { duration = 500 }
        }.let {
            startFragment(it)
        }
    }

    @OnClick(R.id.btn_3)
    fun slideIn(view: View) {
        SecondTransitionFragment().apply {
            enterTransition = Slide().apply {
                slideEdge = Gravity.END
                duration = 500 }
        }.let {
            startFragment(it)
        }
    }

}