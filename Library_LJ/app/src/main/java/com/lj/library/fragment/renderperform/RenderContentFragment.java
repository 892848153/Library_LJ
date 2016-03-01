package com.lj.library.fragment.renderperform;

import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 16/3/1.
 */
public class RenderContentFragment extends BaseFragment {
    @Override
    protected View onCreateView(LayoutInflater inflater) {
        int layoutId = getLayoutId();
        View view = layoutId != -1 ? inflater.inflate(layoutId, null) : null;
        return view;
    }

    private int getLayoutId() {
        int layoutId = -1;
        if (getArguments() != null) {
            layoutId = getArguments().getInt("layoutId", -1);
        }
        return layoutId;
    }
}
