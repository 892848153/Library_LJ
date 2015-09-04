package com.lj.library.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lj.library.R;
import com.lj.library.adapter.MenuAdapter;
import com.lj.library.bean.Menu;
import com.lj.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页菜单目录.
 * Created by liujie_gyh on 15/9/3.
 */
public class MainFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private MenuAdapter mAdapter;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.main_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        List<Menu> list = buildMenus();
        mAdapter = new MenuAdapter(list, mActivity);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    private List<Menu> buildMenus() {
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(new Menu(new BannerFragment(), "Banner Demo"));
        return menuList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Menu menu = (Menu) mAdapter.getItem(i);
        startFragment(menu.targetFragment);
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        mActivity.finish();
        return true;
    }
}
