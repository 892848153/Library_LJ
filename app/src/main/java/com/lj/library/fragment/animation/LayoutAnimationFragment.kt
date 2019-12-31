package com.lj.library.fragment.animation

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.lj.library.R
import com.lj.library.adapter.SimpleAdapter
import com.lj.library.fragment.BaseFragment

class LayoutAnimationFragment: BaseFragment() {

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.layout_animation_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        val data: MutableList<String> = mutableListOf()
        for (i in 0..50) {
            data.add(i.toString())
        }
        val adapter = SimpleAdapter(data)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
    }
}
