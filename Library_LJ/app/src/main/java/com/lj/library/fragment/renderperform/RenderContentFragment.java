package com.lj.library.fragment.renderperform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 16/3/1.
 */
public class RenderContentFragment extends BaseFragment {
    @Override
    protected View onCreateView(LayoutInflater inflater) {
        int layoutId = getLayoutId(getArguments());
        View view = layoutId != -1 ? inflater.inflate(layoutId, null) : null;
        return view;
    }

    private int getLayoutId(Bundle args) {
        int layoutId = -1;
        if (args != null) {
            layoutId = args.getInt("layoutId", -1);
        }
        return layoutId;
    }
}
