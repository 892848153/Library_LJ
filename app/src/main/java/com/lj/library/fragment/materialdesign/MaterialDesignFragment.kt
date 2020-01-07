package com.lj.library.fragment.materialdesign

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.OnItemClick
import com.lj.library.R
import com.lj.library.adapter.MenuAdapter
import com.lj.library.bean.Menu
import com.lj.library.fragment.BaseFragment
import java.util.*

/**
 * Created by liujie on 2020-01-06.
 */
class MaterialDesignFragment: BaseFragment(), OnItemClickListener {

    @BindView(R.id.list_view)
    lateinit var mListView: ListView

    private var mAdapter: MenuAdapter? = null

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.main_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        val list = buildMenus()
        mAdapter = MenuAdapter(list, mContext)
        mListView.adapter = mAdapter
    }

    private fun buildMenus(): List<Menu> {
        val menuList: MutableList<Menu> = ArrayList()
        menuList.add(Menu(CardViewFragment::class.java, "CardView Demo"))

        return menuList
    }

    @OnItemClick(R.id.list_view)
    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        try {
            val menu = mAdapter!!.getItem(i) as Menu
            val fragment = menu.target.newInstance() as Fragment
            startFragment(fragment)
        } catch (e: java.lang.InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}