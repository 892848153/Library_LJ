package com.lj.library.fragment.animation.revealhide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import butterknife.BindView
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.circular_reveal_fragment.*
import kotlin.math.hypot

/**
 * android 5.0开始加入ViewAnimationUtils类
 * https://developer.android.google.cn/training/animation/reveal-or-hide-view
 * Created by liujie on 2020-01-03.
 */
class CircularRevealFragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.circular_reveal_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
    }

    @OnClick(R.id.btn_show)
    fun startCircularRevealContent(view: View) {
        // previously invisible view
        val myView: View = my_view

        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            val cx = myView.width / 2
            val cy = myView.height / 2

            // get the final radius for the clipping circle
            val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius)
            // make the view visible and start the animation
            myView.visibility = View.VISIBLE
            anim.start()
        } else {
            // set the view to invisible without a circular reveal animation below Lollipop
            myView.visibility = View.INVISIBLE
        }
    }

    @OnClick(R.id.btn_hide)
    fun hideCircularRevealContent(view: View) {
        // previously visible view
        val myView: View = my_view

        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            val cx = myView.width / 2
            val cy = myView.height / 2

            // get the initial radius for the clipping circle
            val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animation (the final radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0f)

            // make the view invisible when the animation is done
            anim.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            })

            // start the animation
            anim.start()
        } else {
            // set the view to visible without a circular reveal animation below Lollipop
            myView.visibility = View.INVISIBLE
        }
    }

}