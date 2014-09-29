package com.lj.library.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import com.lj.library.R;

public class BaseFragment extends Fragment {

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
	 * 	LayoutInflater localInflater = setTheme(inflater);
	 * 	View view = localInflater.inflate(R.layout.item_flagment_info, container,
	 * 			false);
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
