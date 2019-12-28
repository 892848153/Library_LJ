package com.lj.library.util;

import android.widget.TextView;

public class StringUtils {

	public static String getText(TextView textView) {
		if (textView != null) {
			return textView.getText().toString().trim();
		} else {
			return "";
		}
	}

	public static String optimizePhone(String phone) {
		return phone.substring(0, 3) + "****" + phone.substring(7, 11);
	}

	public static String optimizeBankCard(String bankCard) {
		return bankCard.substring(0, 3) + "*******"
				+ bankCard.substring(15, 19);
	}
}
