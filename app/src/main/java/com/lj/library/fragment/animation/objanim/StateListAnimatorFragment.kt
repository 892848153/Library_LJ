package com.lj.library.fragment.animation.objanim

import android.animation.AnimatorInflater
import android.os.Bundle
import android.widget.Button
import butterknife.BindView
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

class StateListAnimatorFragment: BaseFragment() {

    @BindView(R.id.btn_attach_by_code)
    lateinit var codeBtn: Button

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.state_list_animator_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        codeBtn.stateListAnimator = AnimatorInflater.loadStateListAnimator(mContext, R.animator.state_list_animate_scale)

    }
}
