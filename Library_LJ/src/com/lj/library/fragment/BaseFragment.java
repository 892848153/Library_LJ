package com.lj.library.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import com.lj.library.R;

/**
 * Fragment基础类.
 * 
 * @time 2014年10月28日 上午10:40:13
 * @author jie.liu
 */
public class BaseFragment extends Fragment {

	/** 解决 {@link #getActivity()} 为null的bug **/
	protected Activity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	public void startFragment(BaseFragment targetFragment) {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fragment_container, targetFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void finish() {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		manager.popBackStack();
	}

	protected void clearBackStack() {
		FragmentManager manager = getActivity().getSupportFragmentManager();
		while (manager.getBackStackEntryCount() > 0) {
			manager.popBackStackImmediate();
		}
	}

	/**
	 * 
	 * 给Fragment设置主题,主题传递0，则采用系统的默认主题.
	 * 
	 * <pre>
	 * public View onCreateView(LayoutInflater inflater, ViewGroup container,
	 * 		Bundle savedInstanceState) {
	 * 	inflater = setTheme(inflater);
	 * 	View view = inflater.inflate(R.layout.item_flagment_info, container, false);
	 * 	return view;
	 * }
	 * </pre>
	 * 
	 * 
	 * @param inflater
	 * @return
	 */
	protected LayoutInflater setTheme(LayoutInflater inflater) {
		// 给Fragment设置主题,主题传递0，则采用系统的默认主题
		final Context contextThemeWrapper = new ContextThemeWrapper(
				getActivity(), 0);
		LayoutInflater localInflater = inflater
				.cloneInContext(contextThemeWrapper);
		return localInflater;
	}
}
