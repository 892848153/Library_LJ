package com.lj.library.fragment.banner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.lj.library.R;
import com.lj.library.bean.Menu;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;
import com.lj.library.widget.ScaledImageView;
import com.lj.library.widget.viewpager.PagerAdapter;
import com.lj.library.widget.viewpager.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 无限循环轮播Banner DEMO.
 * Created by liujie_gyh on 15/9/3.
 */
public class BannerFragment extends BaseFragment {

    @BindView(R.id.banner)
    BannerView mBannerView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.banner_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
//        StatusBarUtils.overflowStatusBar(mContext, true);
//        StatusBarUtils.setFitsSystemWindows(mContext, false);
        PagerAdapter<Menu> pagerAdapter = new BannerAdapter();
        List<Menu> list = new ArrayList<Menu>();
        list.add(new Menu());
        list.add(new Menu());
        list.add(new Menu());
        list.add(new Menu());
        pagerAdapter.init(list, true);
        mBannerView.setPageAdapter(pagerAdapter);
        if (pagerAdapter.getCount() >= 4) {
            mBannerView.setCycleInterval(4000);
            mBannerView.setScrollDuration(1000);
            mBannerView.enableCycleScroll();
            mBannerView.startAutoCycle();
            mBannerView.setCurrentPage(1);
        }
    }

    private class BannerAdapter extends PagerAdapter<Menu> {

        @Override
        public View getView(int position, Menu data) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.banner_item, null);
            ScaledImageView iv = (ScaledImageView) view.findViewById(R.id.iv);
            iv.setFrameWeight(660, 300);
            iv.setImageResource(R.drawable.banner_test);
            return view;
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        return super.onBackPressed();
    }

}
