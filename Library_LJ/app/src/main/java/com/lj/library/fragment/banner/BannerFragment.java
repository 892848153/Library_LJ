package com.lj.library.fragment.banner;

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

/**
 * 无限循环轮播Banner DEMO.
 * Created by liujie_gyh on 15/9/3.
 */
public class BannerFragment extends BaseFragment {

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.banner_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        BannerView banner = (BannerView) view.findViewById(R.id.banner);
        PagerAdapter<Menu> pagerAdapter = new BannerAdapter();
        List<Menu> list = new ArrayList<Menu>();
        list.add(new Menu());
        list.add(new Menu());
        list.add(new Menu());
        list.add(new Menu());
        pagerAdapter.init(list, true);
        banner.setPageAdapter(pagerAdapter);
        if (pagerAdapter.getCount() >= 4) {
            banner.setCycleInterval(4000);
            banner.setScrollDuration(1000);
            banner.enableCycleScroll();
            banner.startAutoCycle();
            banner.setCurrentPage(1);
        }
    }

    private class BannerAdapter extends PagerAdapter<Menu> {

        @Override
        public View getView(int position, Menu data) {
            View view = LayoutInflater.from(mActivity).inflate(
                    R.layout.banner_item, null);
            ScaledImageView iv = (ScaledImageView) view.findViewById(R.id.iv);
            iv.setFrameWeight(660, 300);
            iv.setImageResource(R.mipmap.banner_test);
            return view;
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        return super.onBackPressed();
    }
}
