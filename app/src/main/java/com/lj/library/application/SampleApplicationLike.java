//package com.lj.library.application;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.Application;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.StrictMode;
//import android.support.annotation.Nullable;
//import android.support.multidex.MultiDex;
//import android.text.TextUtils;
//
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.stetho.Stetho;
//import com.lj.library.BuildConfig;
//import com.lj.library.R;
//import com.lj.library.bean.UserInfo;
//import com.lj.library.constants.Constants;
//import com.lj.library.dao.realm.MyMigration;
//import com.lj.library.dao.realm.MySchemaModule;
//import com.lj.library.util.FontSwitcherUtils;
//import com.lj.library.util.PreferenceUtil;
//import com.orhanobut.logger.AndroidLogAdapter;
//import com.orhanobut.logger.FormatStrategy;
//import com.orhanobut.logger.Logger;
//import com.orhanobut.logger.PrettyFormatStrategy;
//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;
//import com.tencent.bugly.Bugly;
//import com.tencent.bugly.beta.Beta;
//import com.tencent.smtt.sdk.QbSdk;
//import com.tencent.tinker.loader.app.DefaultApplicationLike;
//
//import java.security.SecureRandom;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//
//import io.realm.Realm;
//import io.realm.RealmConfiguration;
//
///**
// * Created by liujie_gyh on 2017/4/15.
// */
//public class SampleApplicationLike extends DefaultApplicationLike {
//
//    public SampleApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
//        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
//    }
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    @Override
//    public void onBaseContextAttached(Context base) {
//        super.onBaseContextAttached(base);
//        // you must install multiDex whatever tinker is installed!
//        MultiDex.install(base);
//
//        // 安装tinker
//        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
//        Beta.installTinker(this);
//    }
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
//        getApplication().registerActivityLifecycleCallbacks(callbacks);
//    }
//
//    private final List<Activity> mActivityList = new LinkedList<Activity>();
//
//    private static SampleApplicationLike sInstance;
//
//    private UserInfo mUserInfo;
//
//    /**
//     * leakCanary: Use a RefWatcher to watch references that should be GCed
//     */
//    private RefWatcher mRefWatcher;
//
//    public static synchronized SampleApplicationLike getInstance() {
//        return sInstance;
//    }
//
//    public void addActivity(Activity activity) {
//        mActivityList.add(activity);
//    }
//
//    /**
//     * 删除实例.
//     *
//     * @param activity
//     */
//    public void removeActivity(Activity activity) {
//        Iterator<Activity> iterator = mActivityList.iterator();
//        while (iterator.hasNext()) {
//            Activity targetActivity = iterator.next();
//            if (targetActivity == activity) {
//                iterator.remove();
//            }
//        }
//    }
//
//    /**
//     * 退出APP.
//     */
//    public void exit() {
//        for (Activity activity : mActivityList) {
//            activity.finish();
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sInstance = this;
//        if (LeakCanary.isInAnalyzerProcess(getApplication())) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        getApplication().setTheme(R.style.AppTheme);
//        mRefWatcher = LeakCanary.install(getApplication());
//        Stetho.initializeWithDefaults(getApplication());
//
//        restoreUserInfoFromPref();
//        initStrictMode();
//        initLogger();
//        initBugly();
//        initRealm();
//        initFresco();
//        initQbSdk();
//        switchFont();
//        // Thread.setDefaultUncaughtExceptionHandler(new
//        // MyUncaughtExceptionHandler());
//    }
//
//    private void restoreUserInfoFromPref() {
//        PreferenceUtil util = new PreferenceUtil(getApplication(), Constants.PREFERENCE_FILE);
//        UserInfo userInfo = util.getUserInfo();
//        // 有用户信息, 说明此时是登录状态
//        if (userInfo != null && !TextUtils.isEmpty(userInfo.userId)) {
//            mUserInfo = userInfo;
//        }
//    }
//
//    /**
//     * 开启严苛模式.
//     */
//    private void initStrictMode() {
//        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
//        }
//    }
//
//    /**
//     * 初始化Logger
//     */
//    private void initLogger() {
//        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                // (Optional) Global tag for every log. Default PRETTY_LOGGER
//                .tag("Library_LJ")
//                .build();
//
//        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
//            @Override
//            public boolean isLoggable(final int priority, @Nullable final String tag) {
//                return BuildConfig.DEBUG;
//            }
//        });
//    }
//
//    /**
//     * 初始化Bugly
//     */
//    private void initBugly() {
//        Bugly.init(getApplication(), "b1e580cd5e", true);
//    }
//
//    /**
//     * 初始化Realm
//     */
//    private void initRealm() {
//        byte[] key = new byte[64];
//        new SecureRandom().nextBytes(key);
//        Realm.init(getApplication());
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("default.realm")  //在Context.getFileDir()的路径下生成一个该名字的文件
//                .encryptionKey(key)  //AES-256加密的key
//                .schemaVersion(0) //  数据库版本号, 默认是0
//                .modules(new MySchemaModule())  //数据库支持的表,默认支持代码中所有扫描到的表
//                .migration(new MyMigration())  // 数据库升级会调用这个
//                //开发阶段,调用此函数会在代表数据库表的Bean类和数据库里面的数据匹配不上的时候,直接删除数据库,重新建立一个匹配的上的
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(config);
//    }
//
//    private void initFresco() {
//        Fresco.initialize(getApplication());
//    }
//
//    /**
//     * 初始化腾讯X5内核浏览器
//     */
//    private void initQbSdk() {
//        QbSdk.initX5Environment(getApplication(), QbSdk.WebviewInitType.FIRSTUSE_ONLY, null);
//        if (!QbSdk.isTbsCoreInited()) {
//            QbSdk.preInit(getApplication());
//        }
//    }
//
//    private void switchFont() {
//        FontSwitcherUtils.switchFont(getApplication());
//    }
//
//    /**
//     * 缓存用户信息，有内存和外村缓存.
//     *
//     * @param userInfo 传入null表示退出
//     */
//    public void storeUserInfo(UserInfo userInfo) {
//        mUserInfo = userInfo;
//
//        if (userInfo == null) {
//            // 退出
//            PreferenceUtil util = new PreferenceUtil(getApplication(), Constants.PREFERENCE_FILE);
//            util.setUserInfo(UserInfo.getEmptyInstance());
//        } else {
//            new PreferenceUtil(getApplication(), Constants.PREFERENCE_FILE).setUserInfo(userInfo);
//        }
//    }
//
//    /**
//     * 获取用户的缓存信息.
//     *
//     * @return 用户没有登录则返回null
//     */
//    public UserInfo getUserInfo() {
//        if (mUserInfo != null) {
//            return mUserInfo;
//        }
//
//        PreferenceUtil util = new PreferenceUtil(getApplication(), Constants.PREFERENCE_FILE);
//        UserInfo userInfo = util.getUserInfo();
//        String userId = userInfo.userId;
//        if (!TextUtils.isEmpty(userId)) {
//            // 说明此时是登录状态
//            storeUserInfo(userInfo);
//            return mUserInfo;
//        } else {
//            // 未登录，返回null
//            return null;
//        }
//    }
//
//    /**
//     * 判断是否登录.
//     */
//    public boolean isLogined() {
//        if (getUserInfo() == null) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    public static RefWatcher getRefWatcher() {
//        return getInstance().mRefWatcher;
//    }
//}
