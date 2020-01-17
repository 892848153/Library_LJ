package com.lj.library.fragment.animation.transition

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.*
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import kotlinx.android.synthetic.main.transition_and_scene_fragment.*

/**
 * https://developer.android.google.cn/training/transitions
 * https://developer.android.google.cn/reference/android/transition/Transition.html
 * https://developer.android.google.cn/reference/android/transition/TransitionManager.html
 * https://developer.android.google.cn/training/transitions/custom-transitions
 *
 * Created by liujie on 2020-01-08.
 */
class TransitionAndSceneFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.transition_and_scene_fragment
    }

    /**
     *  The resulting scene represents the state of the view hierarchy at the time you created the Scene instance.
     *  If you change the view hierarchy, you have to recreate the scene.
     *  The framework creates the scene from the entire view hierarchy in the file; you can not create a scene from part of a layout file.
     */
    @OnClick(R.id.btn_1)
    fun createSceneFromLayoutResource() {
        val aScene: Scene = Scene.getSceneForLayout(scene_root, R.layout.a_scene, context!!)
        val bScene: Scene = Scene.getSceneForLayout(scene_root, R.layout.b_scene, context!!)
        transition(bScene)
    }

    /**
     * You can also create a Scene instance in your code from a ViewGroup object.
     * Use this technique when you modify the view hierarchies directly in your code or when you generate them dynamically.
     */
    @OnClick(R.id.btn_2)
    fun createSceneInCode() {
        val aSceneViewHierarchy = layoutInflater.inflate(R.layout.a_scene, null)
        val bSceneViewHierarchy = layoutInflater.inflate(R.layout.b_scene, null)
        val aScene = Scene(scene_root_1, aSceneViewHierarchy)
        val bScene = Scene(scene_root_1, bSceneViewHierarchy)
        transition(aScene)
    }

    /**
     *  Transition：transition代表了切换scene的动画类型。
     *  一般指定目标Scene就行，开始Scene会自动指定
     *  Any Transition has two main jobs:
     *  (1) capture property values, and
     *  (2) play animations based on changes to captured property values
     */
    private fun transition(endingScene: Scene) {
        // Create a transition instance from a resource file（specify a built-in transition in a resource file）
        var fadeTransition: Transition = TransitionInflater.from(context!!).inflateTransition(R.transition.fade)
        // Create a transition instance in your code
        var fadeTransitionFromCode: Transition = Fade()

        // If you do not specify a transition instance,
        // the transition manager can apply an automatic transition that does something reasonable for most situations
        TransitionManager.go(endingScene, fadeTransition)
    }

    /**
     * transition不支持继承至AdapterView的控件，比如ListView, RecyclerView。
     * 我们可以选择Scene中的某一部分view来应用transition
     */
    fun chooseSpecificTargetViews() {
        // Create a transition instance in your code
        var fadeTransitionFromCode: Transition = Fade()
//        fadeTransitionFromCode.removeTarget()
//        fadeTransitionFromCode.addTarget()
    }

    /**
     * you can create and apply a transition between two states of a view hierarchy using a delayed transition
     *  This feature of the transitions framework starts with the current view hierarchy state, records changes you make to its views,
     *  and applies a transition that animates the changes when the system redraws the user interface.
     *  当你remove一个View， add一个View时，可以不使用Scene就可以应用transition
     */
    @OnClick(R.id.btn_3)
    fun applyTransitionWithoutScenes() {
        val labelText = TextView(context).apply {
            text = "Label"
        }
        val rootView: ViewGroup = mRootView.findViewById(R.id.scene_root_2)
            val fade = Fade(Fade.IN)
            TransitionManager.beginDelayedTransition(scene_root_2, fade)
            rootView.addView(labelText)
    }

    /**
     * https://developer.android.google.cn/training/transitions/custom-transitions
     * https://github.com/android/animation-samples/blob/master/CustomTransition/Application/src/main/java/com/example/android/customtransition/ChangeColor.java
     */
    class CustomTransition: Transition() {

        // Define a key for storing a property value in
        // TransitionValues.values with the syntax
        // package_name:transition_class:property_name to avoid collisions
        private val PROPNAME_BACKGROUND = "com.lj.library.fragment.animation:CustomTransition:background"

        /**
         * The framework calls this function for every view in the starting scene
         * contains a reference to the view and a Map instance in which you can store the view values you want.
         *
         */
        override fun captureStartValues(transitionValues: TransitionValues) {
            // Call the convenience method captureValues
            captureValues(transitionValues)
        }

        /**
         * The framework calls the captureEndValues(TransitionValues) function once for every target view in the ending scene
         */
        override fun captureEndValues(transitionValues: TransitionValues) {
            captureValues(transitionValues)
        }

        // For the view in transitionValues.view, get the values you
        // want and put them in transitionValues.values
        private fun captureValues(transitionValues: TransitionValues) {
            // Get a reference to the view
            val view = transitionValues.view
            // Store its background property in the values map
            transitionValues.values[PROPNAME_BACKGROUND] = view.background
        }

        /**
         * If the starting scene has five targets of which two are removed from the ending scene,
         * and the ending scene has the three targets from the starting scene plus a new target,
         * then the framework calls createAnimator() six times:
         * three of the calls animate the fading out and fading in of the targets that stay in both scene objects;
         * two more calls animate the fading out of the targets removed from the ending scene;
         * and one call animates the fading in of the new target in the ending scene.
         */
        override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
            if (null == startValues || null == endValues) {
                return null
            }
            val view: View = endValues.view
            val startBackground: Drawable = startValues.values.get(PROPNAME_BACKGROUND) as Drawable
            val endBackground: Drawable = endValues.values.get(PROPNAME_BACKGROUND) as Drawable

            if (startBackground is ColorDrawable && endBackground is ColorDrawable) {
                val startColor: ColorDrawable = startBackground
                val endColor: ColorDrawable = endBackground
                if (startColor.color != endColor.color) {
                    val animator: ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), startColor.color, endColor.color)
                    animator.addUpdateListener {
                        animator.animatedValue?.let {
                            view.setBackgroundColor(it as Int)
                        }

                    }
                    return animator
                }
            }
            return null
        }
    }
}