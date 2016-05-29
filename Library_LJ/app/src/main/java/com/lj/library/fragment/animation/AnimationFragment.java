package com.lj.library.fragment.animation;

import android.view.LayoutInflater;
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

/**
 * Created by liujie_gyh on 15/9/9.
 */
public class AnimationFragment extends BaseFragment implements AdapterView.OnItemClickListener {

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
        menuList.add(new Menu(new FrameAnimFragment(), "Frame Anim Demo"));
        menuList.add(new Menu(new TweenAnimFragment(), "Tween Anim Demo"));
        menuList.add(new Menu(new ObjectAnimFragment(), "Object Anim Demo"));
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
        return false;
    }
}
