package com.lj.library.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import android.view.MenuItem;

import com.lj.library.R;
import com.lj.library.activity.base.BaseActivity4;
import com.lj.library.activity.base.BaseViewModel;
import com.lj.library.databinding.ActivityMainBinding;
import com.lj.library.fragment.MainFragment;

public class MainActivity extends BaseActivity4<ActivityMainBinding, BaseViewModel> implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected BaseViewModel initViewModel() {
        return null;
    }

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mBinding.navView.setNavigationItemSelectedListener(this);
            mBinding.navView.setCheckedItem(R.id.nav_home);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, new MainFragment());
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
