package com.lj.library.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lj.library.R;
import com.lj.library.activity.TabHostActivity;
import com.lj.library.activity.X5WebViewActivity;
import com.lj.library.adapter.MenuAdapter;
import com.lj.library.bean.Menu;
import com.lj.library.fragment.algorigthm.AlgorithmFragment;
import com.lj.library.fragment.animation.AnimationFragment;
import com.lj.library.fragment.architecture.ArchitectureFragment;
import com.lj.library.fragment.banner.BannerFragment;
import com.lj.library.fragment.dagger.DaggerFragment;
import com.lj.library.fragment.http.HttpDemoFragment;
import com.lj.library.fragment.permissionmanage.PermissionTestFragment;
import com.lj.library.fragment.renderperform.RenderPerformFragment;
import com.lj.library.fragment.rx.RxJavaFragment;
import com.lj.library.fragment.serialization.SerializationFragment;
import com.lj.library.fragment.update.SmartUpdateFragment;
import com.lj.library.util.ContextUtil;
import com.lj.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * 主页菜单目录.
 * Created by liujie_gyh on 15/9/3.
 */
public class MainFragment extends BaseFragment {

    private MenuAdapter mAdapter;

    @BindView(R.id.list_view)
    ListView mListView;

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.main_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        Toolbar toolbar = mRootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        List<Menu> list = buildMenus();
        mAdapter = new MenuAdapter(list, mContext);
        mListView.setAdapter(mAdapter);
    }

    private List<Menu> buildMenus() {
        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(BannerFragment.class, "Banner Demo"));
        menuList.add(new Menu(AnimationFragment.class, "Animation Demo"));
        menuList.add(new Menu(TabHostActivity.class, "TabHostActivity Demo"));
        menuList.add(new Menu(RenderPerformFragment.class, "Render Performance"));
        menuList.add(new Menu(HttpDemoFragment.class, "Http Demo"));
        menuList.add(new Menu(RxJavaFragment.class, "RxJava Demo"));
        menuList.add(new Menu(DaggerFragment.class, "Dagger Demo"));
        menuList.add(new Menu(PermissionTestFragment.class, "Android6.0 Permission"));
        menuList.add(new Menu(SerializationFragment.class, "Serialization Demo"));
        menuList.add(new Menu(AlgorithmFragment.class, "Algorithm Demo"));
        menuList.add(new Menu(X5WebViewActivity.class, "WebView Demo"));
        menuList.add(new Menu(SmartUpdateFragment.class, "Smart Update Demo"));
        menuList.add(new Menu(ArchitectureFragment.class, "Architecture"));
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
                ContextUtil.pushToActivity(mContext, menu.target);
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        mContext.finish();
        return true;
    }
}
