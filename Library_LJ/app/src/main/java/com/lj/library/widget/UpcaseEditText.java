package com.lj.library.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 自动将输入的字母转化成大写的输入框.
 * 
 * @time 2014年10月30日 下午2:02:57
 * @author jie.liu
 */
public class UpcaseEditText extends EditText {

	public UpcaseEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public UpcaseEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UpcaseEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		addTextChangedListener(new UpcaseEditorListener());
	}

	private class UpcaseEditorListener implements TextWatcher {

		private int mChangeCase;

		private String mUpcaseStr;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (mChangeCase == 0) {
				mUpcaseStr = s.toString().toUpperCase();
				if (s.toString().equals("")) {
					mChangeCase = 0;
				} else {
					mChangeCase = 1;
				}
				s.clear();
			} else if (mChangeCase == 1) {
				mChangeCase = 2;
				s.append(mUpcaseStr);
			} else {
				mChangeCase = 0;
			}
		}
	}
}
