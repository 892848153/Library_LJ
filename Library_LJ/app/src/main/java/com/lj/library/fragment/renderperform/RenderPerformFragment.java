package com.lj.library.fragment.renderperform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 测试onMeasure,onLayout, onDraw性能.
 * Created by liujie_gyh on 16/3/1.
 */
public class RenderPerformFragment extends BaseFragment {
    @Bind(R.id.frame_btn)
    Button mFrameBtn;
    @Bind(R.id.linear_btn)
    Button mLinearBtn;
    @Bind(R.id.relative_btn)
    Button mRelativeBtn;
    @Bind(R.id.linear_weight_btn)
    Button mLinearWeightBtn;

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.render_perform_fragment, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.frame_btn, R.id.linear_btn, R.id.relative_btn, R.id.linear_weight_btn})
    public void onClick(View view) {
        BaseFragment targetFragment = new RenderContentFragment();
        Bundle args = new Bundle();
        switch (view.getId()) {
            case R.id.frame_btn:
                putLayoutIdInArgs(args, R.layout.framelayout_uitest);
                break;
            case R.id.linear_btn:
                putLayoutIdInArgs(args, R.layout.linearlayout_uitest);
                break;
            case R.id.relative_btn:
                putLayoutIdInArgs(args, R.layout.relativelayout_uitest);
                break;
            case R.id.linear_weight_btn:
                putLayoutIdInArgs(args, R.layout.linear_weight_uitest);
                break;
        }
        targetFragment.setArguments(args);
        startFragment(targetFragment);
    }

    private void putLayoutIdInArgs(Bundle args, int layoutId) {
        args.putInt("layoutId", layoutId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
