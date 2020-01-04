package com.lj.library.fragment.animation.objanim

import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.core.view.animation.PathInterpolatorCompat
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.path_interpolator_fragment.*

/**
 * android 5.0开始加入PathInterpolator类. 有两种方式创建PathInterpolator
 * 1. 给其指定一个Path. 起点必须是（0,0），终点必须是（1,1）
 * 2, 使用贝塞尔曲线初始化，默认贝塞尔曲线的数据点是（0,0）,(1,1)
 * 我们可以使用PathInterpolatorCompat兼容类
 * Created by liujie on 2020-01-04.
 */
class PathInterpolatorFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {

    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.path_interpolator_fragment
    }

    @OnClick(R.id.btn1)
    fun initPathInterpolatorWithPath(view: View) {
        // arcTo() and PathInterpolator only available on API 21+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0.2f, 0.5f)
                lineTo(0.5f, 0.2f)
                lineTo(1f, 1f)
            }
            val pathInterpolator = PathInterpolatorCompat.create(path)

            ObjectAnimator.ofFloat(tv, View.TRANSLATION_X, 100F).apply {
                interpolator = pathInterpolator
                start()
            }
        }
    }

    @OnClick(R.id.btn2)
    fun initObjectAnimatorWithPath(view: View) {
        // arcTo() and PathInterpolator only available on API 21+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val path = Path().apply {
                arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true)
            }

            ObjectAnimator.ofFloat(tv, View.X, View.Y, path).apply {
                duration = 2000
                start()
            }
        }
    }

    @OnClick(R.id.btn3)
    fun definePathInterpolatorWithXml(view: View) {

    }
}