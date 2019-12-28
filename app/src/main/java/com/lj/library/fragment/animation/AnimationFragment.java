package com.lj.library.fragment.animation;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lj.library.R;
import com.lj.library.adapter.MenuAdapter;
import com.lj.library.bean.Menu;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Created by liujie_gyh on 15/9/9.
 */
public class AnimationFragment extends BaseFragment implements AdapterView.OnItemClickListener {

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
        mAdapter = new MenuAdapter(list, mContext);
        mListView.setAdapter(mAdapter);
    }

    private List<Menu> buildMenus() {
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(new Menu(FrameAnimFragment.class, "Frame Anim Demo"));
        menuList.add(new Menu(TweenAnimFragment.class, "Tween Anim Demo"));
        menuList.add(new Menu(ObjectAnimFragment.class, "Object Anim Demo"));
        return menuList;
    }

    @OnItemClick(R.id.list_view)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            Menu menu = (Menu) mAdapter.getItem(i);
            Fragment fragment = (Fragment) menu.target.newInstance();
            startFragment(fragment);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.d(this, "onBackPressed");
        return false;
    }
}
