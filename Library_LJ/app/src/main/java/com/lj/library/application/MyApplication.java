package com.lj.library.application;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.lj.library.BuildConfig;
import com.lj.library.bean.UserInfo;
import com.lj.library.constants.Constants;
import com.lj.library.dao.realm.MyMigration;
import com.lj.library.dao.realm.MySchemaModule;
import com.lj.library.util.PreferenceUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private final List<Activity> mActivityList = new LinkedList<Activity>();

    private static MyApplication sInstance;

    private UserInfo mUserInfo;

    /**
     * leakCanary: Use a RefWatcher to watch references that should be GCed
     */
    private RefWatcher mRefWatcher;

    public static synchronized MyApplication getInstance() {
        if (null == sInstance) {
            sInstance = new MyApplication();
        }
        return sInstance;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /**
     * 删除实例.
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        Iterator<Activity> iterator = mActivityList.iterator();
        while (iterator.hasNext()) {
            Activity targetActivity = iterator.next();
            if (targetActivity == activity) {
                iterator.remove();
            }
        }
    }

    /**
     * 退出APP.
     */
    public void exit() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sInstance = this;
        mRefWatcher = LeakCanary.install(this);

        restoreUserInfoFromPref();
        initStrictMode();
        initRealm();

        // Thread.setDefaultUncaughtExceptionHandler(new
        // MyUncaughtExceptionHandler());
    }

    private void restoreUserInfoFromPref() {
        PreferenceUtil util = new PreferenceUtil(this, Constants.PREFERENCE_FILE);
        UserInfo userInfo = util.getUserInfo();
        // 有用户信息, 说明此时是登录状态
        if (userInfo != null && !TextUtils.isEmpty(userInfo.userId)) {
            mUserInfo = userInfo;
        }
    }

    /**
     * 开启严苛模式.
     */
    private void initStrictMode() {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    /**
     * 初始化Realm
     */
    private void initRealm() {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        RealmConfiguration config = new RealmConfiguration.Builder(sInstance)
                .name("default.realm")  //在Context.getFileDir()的路径下生成一个该名字的文件
                .encryptionKey(key)  //AES-256加密的key
                .schemaVersion(0) //  数据库版本号, 默认是0
                .modules(new MySchemaModule())  //数据库支持的表,默认支持代码中所有扫描到的表
                .migration(new MyMigration())  // 数据库升级会调用这个
                //开发阶段,调用此函数会在代表数据库表的Bean类和数据库里面的数据匹配不上的时候,直接删除数据库,重新建立一个匹配的上的
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    /**
     * 缓存用户信息，有内存和外村缓存.
     *
     * @param userInfo 传入null表示退出
     */
    public void storeUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;

        if (userInfo == null) {
            // 退出
            PreferenceUtil util = new PreferenceUtil(this, Constants.PREFERENCE_FILE);
            util.setUserInfo(UserInfo.getEmptyInstance());
        } else {
            new PreferenceUtil(this, Constants.PREFERENCE_FILE).setUserInfo(userInfo);
        }
    }

    /**
     * 获取用户的缓存信息.
     *
     * @return 用户没有登录则返回null
     */
    public UserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        }

        PreferenceUtil util = new PreferenceUtil(sInstance, Constants.PREFERENCE_FILE);
        UserInfo userInfo = util.getUserInfo();
        String userId = userInfo.userId;
        if (!TextUtils.isEmpty(userId)) {
            // 说明此时是登录状态
            storeUserInfo(userInfo);
            return mUserInfo;
        } else {
            // 未登录，返回null
            return null;
        }
    }

    /**
     * 判断是否登录.
     */
    public boolean isLogined() {
        if (getUserInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    public static RefWatcher getRefWatcher() {
        return getInstance().mRefWatcher;
    }
}
