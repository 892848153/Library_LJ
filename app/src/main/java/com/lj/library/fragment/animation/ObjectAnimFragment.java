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
import com.lj.library.fragment.animation.objanim.AnimatorInflaterFragment;
import com.lj.library.fragment.animation.objanim.AnimatorSetFragment;
import com.lj.library.fragment.animation.objanim.DynamicAnimationFragment;
import com.lj.library.fragment.animation.objanim.ObjectAnimatorFragment;
import com.lj.library.fragment.animation.objanim.PathInterpolatorFragment;
import com.lj.library.fragment.animation.objanim.PropertyValueHolderFragment;
import com.lj.library.fragment.animation.objanim.StateListAnimatorFragment;
import com.lj.library.fragment.animation.objanim.TouchToZoomAnimationFragment;
import com.lj.library.fragment.animation.objanim.TypeEvaluateFragment;
import com.lj.library.fragment.animation.objanim.ValueAnimatorFragment;
import com.lj.library.fragment.animation.objanim.ViewAnimFragment;
import com.lj.library.fragment.animation.revealhide.RevealOrHideViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * Created by liujie_gyh on 15/9/9.
 */
public class ObjectAnimFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.list_view)
    ListView mListView;

    private MenuAdapter mAdapter;

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
        menuList.add(new Menu(ObjectAnimatorFragment.class, "ObjectAnimator Demo"));
        menuList.add(new Menu(ValueAnimatorFragment.class, "ValueAnimator Demo"));
        menuList.add(new Menu(AnimatorSetFragment.class, "AnimatorSet Demo"));
        menuList.add(new Menu(AnimatorInflaterFragment.class, "AnimatorInflater Demo"));
        menuList.add(new Menu(StateListAnimatorFragment.class, "StateListAnimator Demo"));
        menuList.add(new Menu(PropertyValueHolderFragment.class, "PropertyValueHolder Demo"));
        menuList.add(new Menu(TypeEvaluateFragment.class, "TypeEvaluate Demo"));
        menuList.add(new Menu(ViewAnimFragment.class, "ViewAnim Demo"));
        menuList.add(new Menu(RevealOrHideViewFragment.class, "Reveal Or Hide View Demo"));
        menuList.add(new Menu(PathInterpolatorFragment.class, "PathInterpolator Demo"));
        menuList.add(new Menu(DynamicAnimationFragment.class, "DynamicAnimation Demo"));
        menuList.add(new Menu(TouchToZoomAnimationFragment.class, "TouchToZoomAnimation Demo"));
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
}
