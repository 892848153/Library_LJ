package com.lj.library.fragment.materialdesign

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.elevation_and_shadow_fragment.*

/**
 * https://blog.usejournal.com/playing-with-elevation-in-android-91af4f3be596
 * https://tips.seebrock3r.me/playing-with-elevation-in-android-part-2-2b415795ceb6?
 * https://tips.seebrock3r.me/playing-with-elevation-in-android-part-1-36b901287249
 *
 * In the Material Design system, there are two light sources.
 * One is a key light that sits above the top of the screen,
 * and an ambient light that sits directly above the centre of the screen:
 * 此demo是对elevation以及shadow的理解.
 * Created by liujie on 2020-01-07.
 */
class ElevationAndShadowFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {
        // android 5.0才支持Outline
        tv_6.outlineProvider = object: ViewOutlineProvider() {

            override fun getOutline(view: View, outline: Outline) {
                ViewOutlineProvider.BOUNDS.getOutline(view, outline)
            }
        }

        // 需要将View裁剪Outline形状需要调用下面这个方法或者android:clipToOutline属性
        // (官方文档说可以用android:clipToOutline，实际上由于android系统有bug，不能使用此属性，只能调用下面这个方法这一条路)
        // 一个Outline能不能用于裁剪View，需要看Outline.canClip()方法的返回值
        // 裁剪View很耗性能
        tv_77.clipToOutline = true   // ImageView的话,clipToOutline是clip成android:src的图片的Outline而不是background
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.elevation_and_shadow_fragment
    }
}