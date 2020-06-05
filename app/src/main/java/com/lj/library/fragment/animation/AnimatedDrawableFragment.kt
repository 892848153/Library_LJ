package com.lj.library.fragment.animation

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
import com.lj.library.fragment.animation.animdrawable.AnimatedImageDrawableFragment
import com.lj.library.fragment.animation.animdrawable.AnimatedStateListDrawableFragment
import com.lj.library.fragment.animation.animdrawable.AnimatedVectorDrawableFragment
import java.util.*

/**
 * 5.0开始 加入AnimatedStateListDrawable
 */
class AnimatedDrawableFragment: BaseFragment(), AdapterView.OnItemClickListener {

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
        menuList.add(Menu(AnimatedStateListDrawableFragment::class.java, "AnimatedStateListDrawable Demo"))
        menuList.add(Menu(AnimatedVectorDrawableFragment::class.java, "AnimatedVectorDrawable Demo"))
        menuList.add(Menu(AnimatedImageDrawableFragment::class.java, "AnimatedImageDrawableFragment Demo"))
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