package com.lj.library.fragment.animation

import android.os.Bundle
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
        var fadeTransition: Transition = TransitionInflater.from(context!!).inflateTransition(R.transition.fade_transition)
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
}