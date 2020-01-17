package com.lj.library.fragment.animation.revealhide

import android.os.Bundle
import android.view.View
import butterknife.OnClick
import com.lj.library.R
import com.lj.library.fragment.BaseFragment
import com.lj.library.fragment.ContentFragment

/**
 * Created by liujie on 2020-01-03.
 */
class CardFlipFragment: BaseFragment() {

    override fun initComp(savedInstanceState: Bundle?) {
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.card_flip_fragment
    }

    @OnClick(R.id.btn)
    fun startCardFlip(view: View) {
// Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        activity?.supportFragmentManager?.beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                ?.setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out
                )

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                ?.replace(R.id.fragment_container, ContentFragment())

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                ?.addToBackStack(null)

                // Commit the transaction.
                ?.commit()
    }
}