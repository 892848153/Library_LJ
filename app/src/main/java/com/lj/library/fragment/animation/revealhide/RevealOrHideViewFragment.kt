package com.lj.library.fragment.animation.revealhide

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
import java.util.ArrayList

/**
 * https://developer.android.google.cn/training/animation/reveal-or-hide-view
 * Created by liujie_gyh on 2020-01-03.
 */
class RevealOrHideViewFragment: BaseFragment(), AdapterView.OnItemClickListener {

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
        menuList.add(Menu(CrossfadeFragment::class.java, "With Crossfade Animator Demo"))
        menuList.add(Menu(CardFlipFragment::class.java, "With Card Flip Animator Demo"))
        menuList.add(Menu(CircularRevealFragment::class.java, "With Circular Reveal Animator Demo"))
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