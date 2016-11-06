package com.lj.library.util;

/**
 * Created by liujie_gyh on 2016/10/30.
 */
public class TinkerManager {
//    private static final String TAG = "Tinker.TinkerManager";
//
//    private static ApplicationLike                applicationLike;
//    private static SampleUncaughtExceptionHandler uncaughtExceptionHandler;
//    private static boolean isInstalled = false;
//
//    public static void setTinkerApplicationLike(ApplicationLike appLike) {
//        applicationLike = appLike;
//    }
//
//    public static ApplicationLike getTinkerApplicationLike() {
//        return applicationLike;
//    }
//
//    public static void initFastCrashProtect() {
//        if (uncaughtExceptionHandler == null) {
//            uncaughtExceptionHandler = new SampleUncaughtExceptionHandler();
//            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
//        }
//    }
//
//    public static void setUpgradeRetryEnable(boolean enable) {
//        UpgradePatchRetry.getInstance(applicationLike.getApplication()).setRetryEnable(enable);
//    }
//
//
//    /**
//     * all use default class, simply Tinker install method
//     */
//    public static void sampleInstallTinker(ApplicationLike appLike) {
//        if (isInstalled) {
//            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
//            return;
//        }
//        TinkerInstaller.install(appLike);
//        isInstalled = true;
//
//    }
//
//    /**
//     * you can specify all class you want.
//     * sometimes, you can only install tinker in some process you want!
//     *
//     * @param appLike
//     */
//    public static void installTinker(ApplicationLike appLike) {
//        if (isInstalled) {
//            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
//            return;
//        }
//        //or you can just use DefaultLoadReporter
//        LoadReporter loadReporter = new SampleLoadReporter(appLike.getApplication());
//        //or you can just use DefaultPatchReporter
//        PatchReporter patchReporter = new SamplePatchReporter(appLike.getApplication());
//        //or you can just use DefaultPatchListener
//        PatchListener patchListener = new SamplePatchListener(appLike.getApplication());
//        //you can set your own upgrade patch if you need
//        AbstractPatch upgradePatchProcessor = new UpgradePatch();
//        //you can set your own repair patch if you need
//        AbstractPatch repairPatchProcessor = new RepairPatch();
//
//        TinkerInstaller.install(appLike,
//                loadReporter, patchReporter, patchListener,
//                SampleResultService.class, upgradePatchProcessor, repairPatchProcessor);
//
//        isInstalled = true;
//    }
}
