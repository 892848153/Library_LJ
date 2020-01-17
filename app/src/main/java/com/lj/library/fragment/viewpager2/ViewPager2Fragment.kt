package com.lj.library.fragment.viewpager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import com.lj.library.fragment.ContentFragment
import kotlinx.android.synthetic.main.view_pager2_fragment.*

/**
 * ViewPager migrate to ViewPager2 参考: https://developer.android.google.cn/training/animation/vp2-migration
 *
 * ViewPager2只支持两种Adapter。1.RecyclerView.Adapter, 2. FragmentStateAdapter
 *
 * Created by liujie on 2020-01-17.
 */
/**
 * The number of pages (wizard steps) to show in this demo.
 */
private const val NUM_PAGES = 5

class ViewPager2Fragment: BaseFragment() {

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.view_pager2_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        view_pager_1.adapter = ScreenSlidePagerAdapter(mContext as FragmentActivity)
        view_pager_2.adapter = ScreenSlidePagerAdapter(mContext as FragmentActivity)
        view_pager_3.adapter = ScreenSlidePagerAdapter(mContext as FragmentActivity)
//        view_pager_3.layoutDirection = ViewPager2.LAYOUT_DIRECTION_RTL
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = ContentFragment(position.toString())
    }

}