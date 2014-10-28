package com.lj.library.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络检查工具类.
 * 
 * @time 2014年9月3日 下午2:39:11
 * @author jie.liu
 */
public class NetworkChecker {

	/**
	 * 判断网络是否连接.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	/**
	 * 获取网络类型.
	 * <p/>
	 * 判断网络类型是2G，3G，WIFI还是无效的网络.
	 * 
	 * @param activity
	 * @return
	 * @see NetworkType
	 */
	public static NetworkType getNetworkType(Activity activity) {
		ConnectivityManager connectMgr = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectMgr != null) {
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if (info != null) {
				return getNetworkType(info);
			}
		}

		return NetworkType.NETWORK_TYPE_INVALID;
	}

	private static NetworkType getNetworkType(NetworkInfo info) {
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return NetworkType.NETWORK_TYPE_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int mobileNetType = info.getSubtype();
			return getNetworkType(mobileNetType);
		}

		return NetworkType.NETWORK_TYPE_INVALID;
	}

	private static NetworkType getNetworkType(int mobileNetType) {
		switch (mobileNetType) {
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return NetworkType.NETWORK_TYPE_2G; // ~ 14-64 kbps 电信2G
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return NetworkType.NETWORK_TYPE_2G; // ~ 50-100 kbps 移动或者联通2G
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return NetworkType.NETWORK_TYPE_3G; // ~ 400-1000 kbps 电信3G
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return NetworkType.NETWORK_TYPE_3G; // ~ 600-1400 kbps 电信3G
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return NetworkType.NETWORK_TYPE_2G; // ~ 100 kbps 移动或者联通2G
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return NetworkType.NETWORK_TYPE_3G; // ~ 2-14 Mbps 联通3G
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return NetworkType.NETWORK_TYPE_3G; // ~ 400-7000 kbps 联通3G
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return NetworkType.NETWORK_TYPE_3G; // ~ 5 Mbps 电信3G
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return NetworkType.NETWORK_TYPE_INVALID;
		default:
			return NetworkType.NETWORK_TYPE_INVALID;
		}
	}

	public static enum NetworkType {
		NETWORK_TYPE_INVALID, NETWORK_TYPE_2G, NETWORK_TYPE_3G, NETWORK_TYPE_WIFI;
	}
}
