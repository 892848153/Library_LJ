package com.lj.library.fragment.animation.transition

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.OnItemClick
import com.lj.library.R
import com.lj.library.adapter.MenuAdapter
import com.lj.library.bean.Menu
import com.lj.library.fragment.BaseFragment
import com.lj.library.fragment.animation.revealhide.CircularRevealFragment
import java.util.*

/**
 * 参考 https://www.jianshu.com/p/69d48f313dc4
 *     https://blog.csdn.net/sunsteam/article/details/71565999
 *
 * 使用Transition框架，可以导入implementation "androidx.transition:transition:1.2.0"
 *
 * Android 4.4.2 中引入了 Transition 过渡动画，不过那时的 API 的功能比较简单，只能对整个 Activity 或 Fragment 做动画，
 * Google 在 Android 5.0 的 Material Design 中引入更完整的 Transition 框架.
 * android 的Transition主要分为四部分:
 * 1.ContentTransition，2. SharedElementTransition 3.同一个页面中的场景过渡动画(Scene/LayoutTransition) 4.Circular Reveal
 *
 * Created by liujie on 2020-01-10.
 */
class TransitionFragment: BaseFragment(), AdapterView.OnItemClickListener {

    @BindView(R.id.list_view)
    lateinit var mListView: ListView

    var mAdapter: MenuAdapter? = null

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.main_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        val list: List<Menu> = buildMenus()
        mAdapter = MenuAdapter(list, mContext)
        mListView.adapter = mAdapter
    }

    private fun buildMenus(): List<Menu> {
        val menuList: MutableList<Menu> = ArrayList()
        menuList.add(Menu(LayoutTransitionFragment::class.java, "LayoutTransition Demo"))
        menuList.add(Menu(TransitionAndSceneFragment::class.java, "Transition and Scene Demo"))
        menuList.add(Menu(ContentTransitionFragment::class.java, "ContentTransition Demo"))
        menuList.add(Menu(SharedElementTransitionFragment::class.java, "SharedElementTransition Demo"))
        menuList.add(Menu(CircularRevealFragment::class.java, "Circular Reveal Demo"))
        return menuList
    }

    @OnItemClick(R.id.list_view)
    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        try {
            val menu = mAdapter!!.getItem(i) as Menu
            val fragment = menu.target.newInstance() as Fragment
            startFragment(fragment)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}