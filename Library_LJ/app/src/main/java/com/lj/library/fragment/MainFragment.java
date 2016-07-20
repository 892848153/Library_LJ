package com.lj.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lj.library.R;
import com.lj.library.activity.TabHostActivity;
import com.lj.library.adapter.MenuAdapter;
import com.lj.library.bean.Menu;
import com.lj.library.fragment.animation.AnimationFragment;
import com.lj.library.fragment.banner.BannerFragment;
import com.lj.library.fragment.dagger.DaggerFragment;
import com.lj.library.fragment.http.HttpDemoFragment;
import com.lj.library.fragment.permissionmanage.PermissionTestFragment;
import com.lj.library.fragment.renderperform.RenderPerformFragment;
import com.lj.library.fragment.rx.RxJavaFragment;
import com.lj.library.fragment.serialization.SerializationFragment;
import com.lj.library.util.ContextUtil;
import com.lj.library.util.LogUtil;
import com.lj.library.util.Toaster;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * 主页菜单目录.
 * Created by liujie_gyh on 15/9/3.
 */
public class MainFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private MenuAdapter mAdapter;

    @Bind(R.id.list_view)
    ListView mListView;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, null);
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        List<Menu> list = buildMenus();
        mAdapter = new MenuAdapter(list, mActivity);
        mListView.setAdapter(mAdapter);
    }

    private List<Menu> buildMenus() {
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(new Menu(new BannerFragment(), "Banner Demo"));
        menuList.add(new Menu(new AnimationFragment(), "Animation Demo"));
        menuList.add(new Menu(TabHostActivity.class, "TabHostActivity Demo"));
        menuList.add(new Menu(new RenderPerformFragment(), "Render Performance"));
        menuList.add(new Menu(new HttpDemoFragment(), "Http Demo"));
        menuList.add(new Menu(new RxJavaFragment(), "RxJava Demo"));
        menuList.add(new Menu(new DaggerFragment(), "Dagger Demo"));
        menuList.add(new Menu(new PermissionTestFragment(), "Android6.0 Permission"));
        menuList.add(new Menu(new SerializationFragment(), "Serialization Demo"));
        return menuList;
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Menu menu = (Menu) mAdapter.getItem(i);
        if (menu.targetFragment != null) {
            startFragment(menu.targetFragment);
        } else if (menu.targetActivity != null) {
            ContextUtil.pushToActivity(mActivity, menu.targetActivity);
        } else {
            Toaster.showShort(mActivity, "未知的启动项");
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        mActivity.finish();
        return true;
    }
}
