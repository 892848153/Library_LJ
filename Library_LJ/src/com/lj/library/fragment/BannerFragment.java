package com.lj.library.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.R;
import com.lj.library.util.LogUtil;

/**
 * Created by liujie_gyh on 15/9/3.
 */
public class BannerFragment extends BaseFragment {
    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.banner_fragment, null);
        initViews(view);
        return view;
    }

    private void  initViews(View view) {

    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        return super.onBackPressed();
    }
}
