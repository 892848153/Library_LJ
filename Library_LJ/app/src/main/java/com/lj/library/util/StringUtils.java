package com.lj.library.util;

import junit.framework.Assert;
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
		Assert.assertEquals(11, phone.length());
		return phone.substring(0, 3) + "****" + phone.substring(7, 11);
	}

	public static String optimizeBankCard(String bankCard) {
		Assert.assertEquals(19, bankCard.length());
		return bankCard.substring(0, 3) + "*******"
				+ bankCard.substring(15, 19);
	}
}
