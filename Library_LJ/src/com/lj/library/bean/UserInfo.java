package com.lj.library.bean;

/**
 * 用户信息.
 * 
 * @time 2014年7月31日 上午11:56:16
 * @author jie.liu
 */

public class UserInfo {

	public String userId;

	public static UserInfo getEmptyInstance() {
		UserInfo instance = new UserInfo();
		instance.userId = "";
		return instance;
	}

}
