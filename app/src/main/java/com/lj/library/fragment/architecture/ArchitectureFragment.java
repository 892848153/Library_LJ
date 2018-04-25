package com.lj.library.fragment.architecture;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lj.library.R;
import com.lj.library.activity.MvpArchitectureActivity;
import com.lj.library.activity.MvvmArchitectureActivity;
import com.lj.library.activity.MvvmArchitectureActivity1;
import com.lj.library.adapter.MenuAdapter;
import com.lj.library.bean.Menu;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.ContextUtil;
import com.lj.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Created by ocean on 2017/8/30.
 */
public class ArchitectureFragment extends BaseFragment {

    private MenuAdapter mAdapter;

    @BindView(R.id.list_view)
    ListView mListView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.main_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        List<Menu> list = buildMenus();
        mAdapter = new MenuAdapter(list, mActivity);
        mListView.setAdapter(mAdapter);
    }

    private List<Menu> buildMenus() {
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(new Menu(MvpArchitectureActivity.class, "MVP architecture activity"));
        menuList.add(new Menu(MvpArchitectureFragment.class, "MVP architecture fragment"));
        menuList.add(new Menu(MvvmArchitectureActivity.class, "MVVM architecture activity"));
        menuList.add(new Menu(MvvmArchitectureActivity1.class, "MVVM architecture activity1"));
        return menuList;
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Menu menu = (Menu) mAdapter.getItem(i);
        // https://zhidao.baidu.com/question/240894762388261364.html
        if (Fragment.class.isAssignableFrom(menu.target)) {
            try {
                Fragment fragment = (Fragment) menu.target.newInstance();
                startFragment(fragment);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (Activity.class.isAssignableFrom(menu.target)){
            ContextUtil.pushToActivity(mActivity, menu.target);
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        return false;
    }
}
