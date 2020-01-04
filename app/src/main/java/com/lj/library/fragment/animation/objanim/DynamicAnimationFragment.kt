package com.lj.library.fragment.animation.objanim

import android.os.Bundle
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.dynamic_animation_fragment.*

/**
 *
 * 需要导入 implementation "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03"
 * Created by liujie on 2020-01-04.
 */
class DynamicAnimationFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.dynamic_animation_fragment
    }

    /**
     * https://developer.android.google.cn/guide/topics/graphics/fling-animation
     */
    @OnClick(R.id.btn1)
    fun flingAnimation(view: View) {
        FlingAnimation(tv, DynamicAnimation.TRANSLATION_X).apply {
            setStartVelocity(1000f) // 设置初始速度
            setMinValue(0f)  // 设置动画的最小值，到达此最小值时会停止动画。不设置则无限制
            setMaxValue(2000f)  // 设置动画的最大值，到达此最大值时会停止动画。不设置则无限制
            friction = 1.1f     //  摩擦力，默认是 1
            // 设置最小可见变化值，即每一帧最小变化值
            // 当动画改变的是DynamicAnimation.ViewProperty时，无需设置此值，DynamicAnimation中有了默认值
            // 如果改变的是自定义的属性且此属性跟像素无关的话，则需要设置此值，
            // 推荐采用计算的方式得到minimumVisibleChange值，Minimum visible change = Range of custom property value / Range of animiation in pixels
            // 比如应用动画从0到100改变progress。 对应到像素的改变是200个像素。则minimumVisibleChange = 100 / 200 = 0.5
//            minimumVisibleChange = DynamicAnimation.MIN_VISIBLE_CHANGE_PIXELS
            start()
        }
    }

    /**
     * 弹簧动画
     * https://developer.android.google.cn/guide/topics/graphics/spring-animation
     */
    @OnClick(R.id.btn2)
    fun springAnimation(view: View) {
        // Setting up a spring animation to animate the view’s translationY property with the final
        // spring position at 0.    finalPosition=0f表示动画停止时translationY=0f
        SpringAnimation(tv, DynamicAnimation.TRANSLATION_Y, 0f).apply {
            setStartValue(500f) // translationY起始值为500f
            spring.apply {
                // 设置阻尼比，不能小于0。默认是SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY.
                // 阻尼比描述了在弹簧振动中逐渐减小弹力值。通过使用阻尼比，您可以定义从一次弹跳到下一次弹跳的振动衰减速度。 有四种阻尼弹簧的方法：
                // 1. 过阻尼：当阻尼比大于1时会发生过阻尼。 它让物体快速回到静止位置
                // 2. 临界阻尼：当阻尼比等于1时，会发生临界阻尼。 它可使对象在最短时间内返回到静止位置。
                // 3. 欠阻尼(不足阻尼)：当阻尼比小于1时，发生欠阻尼。 它可以使物体多次冲过静止位置，然后逐渐回到静止位置
                // 4. 无阻尼：当阻尼比等于零时，发生无阻尼。 它可以让物体永远振动。
                dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY

                // 设置刚度, 必须是正数。默认是SpringForce.STIFFNESS_MEDIUM
                stiffness = SpringForce.STIFFNESS_MEDIUM
            }
        }.start() // 启动动画使用start()或者animateToFinalPosition()。推荐animateToFinalPosition()即使动画正在执行也可以调用此方法

        // 结束动画的方法 cancel()/skipToEnd()
        // 1. The cancel() method terminates the animation at the value where it is
        // 2. The skipToEnd() method skips the animation to the final value and then terminates it.
        // 调用skipToEnd()方法需要先检测弹簧状态。if (canSkipToEnd()) { skipToEnd() }
        // 阻尼比大于0时，canSkipToEnd()返回true

    }
}