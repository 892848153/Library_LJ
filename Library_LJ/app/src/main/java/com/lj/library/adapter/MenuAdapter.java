package com.lj.library.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lj.library.bean.Menu;

import java.util.List;

/**
 * Created by liujie_gyh on 15/9/3.
 */
public class MenuAdapter extends BaseParentAdapter<Menu>{


    public MenuAdapter(List<Menu> items, Activity activity) {
        super(items, activity);
    }

    public MenuAdapter(List<Menu> items, Activity activity, int layoutRes) {
        super(items, activity, layoutRes);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        TextView textView =  getAdapterView(view, android.R.id.text1);
        textView.setText(mItems.get(i).describe);
        return view;
    }
}
