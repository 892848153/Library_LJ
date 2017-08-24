package com.lj.library.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.FragmentBackManager;
import com.lj.library.fragment.tabhost.FragmentA;
import com.lj.library.fragment.tabhost.FragmentB;
import com.lj.library.fragment.tabhost.FragmentC;
import com.lj.library.fragment.tabhost.FragmentD;

/**
 * Created by liujie_gyh on 15/11/4.
 */
public class TabHostActivity extends FragmentActivity implements BackHandlerInterface {

    private FragmentTabHost mTabHost = null;
    private View mIndicator = null;
    protected FragmentBackManager mFragmentSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host_activity);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content_flyt);

        // 添加tab名称和图标
        mIndicator = getIndicatorView("FragmentA", R.layout.tab_spec);
        mTabHost.addTab(mTabHost.newTabSpec("FragmentA")
                .setIndicator(mIndicator), FragmentA.class, null);

        mIndicator = getIndicatorView("FragmentB", R.layout.tab_spec);
        mTabHost.addTab(
                mTabHost.newTabSpec("FragmentB").setIndicator(mIndicator),
                FragmentB.class, null);

        mIndicator = getIndicatorView("FragmentC", R.layout.tab_spec);
        mTabHost.addTab(
                mTabHost.newTabSpec("FragmentC").setIndicator(mIndicator),
                FragmentC.class, null);

        mIndicator = getIndicatorView("FragmentD", R.layout.tab_spec);
        mTabHost.addTab(
                mTabHost.newTabSpec("FragmentD").setIndicator(mIndicator),
                FragmentD.class, null);
    }

    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = (TextView) v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }

    @Override
    public void onBackPressed() {
        if (mFragmentSelected == null || !mFragmentSelected.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(FragmentBackManager selectedFragment) {
        mFragmentSelected = selectedFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
}
