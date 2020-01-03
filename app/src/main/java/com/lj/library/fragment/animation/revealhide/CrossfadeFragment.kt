package com.lj.library.fragment.animation.revealhide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.view.View
import butterknife.BindView
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

/**
 * 采用淡入淡出效果切换两个View。一个慢慢消失，一个慢慢显示
 *
 * Created by liujie on 2020-01-03.
 */
class CrossfadeFragment: BaseFragment() {

    @BindView(R.id.content)
    lateinit var contentView: View

    @BindView(R.id.loading_spinner)
    lateinit var loadingView: View

    private var shortAnimationDuration: Int = 0

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.crossfade_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        // Initially hide the content view.
        contentView.visibility = View.GONE

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        Handler().postDelayed({ crossfade() }, 1500)
    }

    private fun crossfade() {
        contentView.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        loadingView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        loadingView.visibility = View.GONE
                    }
                })
    }
}