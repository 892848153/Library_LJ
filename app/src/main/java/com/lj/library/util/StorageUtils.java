package com.lj.library.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lj.library.constants.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jie.liu on 16/8/23.
 */
public class StorageUtils {

    /**
     * 获取项目在SDCard中保存缓存的根目录路径.
     *
     * @return
     */
    public static String getCacheRootDirPath() {
        return getSDCardPath() + File.separator + Constants.CACHE_ROOT_DIR_NAME;
    }

    /**
     * 在/data/data/packagename/files/目录下创建文件.
     *
     * @param context
     * @param fileName 文件名，不能有文件分隔符
     * @param mode 取值{@link Context#MODE_PRIVATE}:如果文件不存在,则创建文件, 如果存在,则清空内容 <br/>
     * {@link Context#MODE_APPEND}:如果文件不存在，则创建文件, 如果存在，则将写入游标定位在内容最后。
     * @return
     */
    public static FileOutputStream openFileOutput(@NonNull Context context, @NonNull String fileName, int mode) {
        try {
            return context.openFileOutput(fileName, mode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回/data/data/packagename/files/目录下指定文件的读取流.
     *
     * @param context
     * @param fileName 文件名，不能有文件分隔符
     * @return
     */
    public static FileInputStream opeFileInput(@NonNull Context context, @NonNull String fileName) {
        try {
            return context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回/data/data/packagename/cache文件夹.
     *
     * @param context
     * @return
     */
    public static File getCacheDir(@NonNull Context context) {
        return context.getCacheDir();
    }

    /**
     * 返回/data/data/packagename/files/文件夹.<p/>
     *
     * 利用得到的files文件夹对象，可以在files文件夹下建立子文件夹及文件。
     * 以弥补{@link Context#openFileOutput(String, int)}方法不能创建子文件夹的功能。
     *
     * @param context
     * @return
     */
    public static File getFileDir(@NonNull Context context) {
        return context.getFilesDir();
    }

    /**
     * 创建并返回/data/data/packagename/app_name/文件夹.其中app_name的name就是下面传入的参数<p/>
     *
     * 其后可以利用返回的File对象，像普通的文件操作那样，创建子文件夹，读写文件等等。
     *
     * @param context
     * @param name 文件名，不能有文件分隔符
     * @param mode  一般取值{@link Context#MODE_PRIVATE} ，其他的mode好像都没效果
     * @return
     */
    public static File getDir(@NonNull Context context, @NonNull String name, int mode) {
        return context.getDir(name, mode);
    }

    /**
     * 删除文件, 貌似没有用.
     *
     * @param context
     * @param name 文件名，不能有文件分隔符
     */
    public static void deleteFile(@NonNull Context context, @NonNull String name) {
        context.deleteFile(name);
    }

    /**
     * 返回/data/data/packagename/files/文件夹下的所有文件及顶级文件夹的名字.<p/>
     *
     * 不会递归遍历子文件夹，只抓取顶级子文件夹的名字.
     *
     * @param context
     * @return
     */
    public static String[] fileList(@NonNull Context context) {
        return context.fileList();
    }

    /**
     * 获取primary storage，既手机自带的存储中的/sdcard/Android/data/packagename/files/文件夹下的子文件夹.
     *
     * @param context
     * @param type 取值 <br/>
     * null : 获取/sdcard/Android/data/packagename/files/文件夹 <br/>
     * {@link android.os.Environment#DIRECTORY_MUSIC},<br/>
     *             获取/sdcard/Android/data/packagename/files/Music/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_PODCASTS},<br/>
     *             获取/sdcard/Android/data/packagename/files/Podcasts/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_RINGTONES},<br/>
     *             获取/sdcard/Android/data/packagename/files/Ringtones/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_ALARMS},<br/>
     *             获取/sdcard/Android/data/packagename/files/Alarms/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},<br/>
     *             获取/sdcard/Android/data/packagename/files/Notifications/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_PICTURES},<br/>
     *             获取/sdcard/Android/data/packagename/files/Pictures/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_MOVIES}<br/>
     *             获取/sdcard/Android/data/packagename/files/Movies文件夹<br/>
     */
    public static File getExternalFileDir(@NonNull Context context, String type) {
        return context.getExternalFilesDir(type);
    }

    /**
     * 获取primary storage中，既手机自带的存储中的/sdcard/Android/data/packagename/cache/文件夹.
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(@NonNull Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * 获取SDCard路径.
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcard = getSDCard();
        if (sdcard != null) {
            return sdcard.getAbsolutePath();
        } else  {
            return Environment.getRootDirectory().getAbsolutePath();
        }
    }

    /**
     * 获取SDCard.
     *
     * @return
     */
    public static File getSDCard() {
        if (checkSDCardAvailable()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 获取primary storage，既手机自带的存储中的/sdcard/文件夹下的子文件夹.
     * 存储在这些文件夹下的数据不会随着应用的卸载而删除.
     *
     * @param type 取值 <br/>
     * {@link android.os.Environment#DIRECTORY_MUSIC},<br/>
     *             获取/sdcard/Music/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_PODCASTS},<br/>
     *             获取/sdcard/Podcasts/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_RINGTONES},<br/>
     *             获取/sdcard/Ringtones/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_ALARMS},<br/>
     *             获取/sdcard/Alarms/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},<br/>
     *             获取/sdcard/Notifications/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_PICTURES},<br/>
     *             获取/sdcard/Pictures/文件夹<br/>
     * {@link android.os.Environment#DIRECTORY_MOVIES}<br/>
     *             获取/sdcard/Movies文件夹<br/>
     */
    public static File getExternalStoragePublicDirectory(@NonNull String type) {
        if (checkSDCardAvailable()) {
            return Environment.getExternalStoragePublicDirectory(type);
        } else {
            return null;
        }
    }

    /**
     * 检查是否有SDCard.
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 利用linux命名获取sdcard路径.
     *
     * @return
     */
    public static List<String> getSDPathFromCmd() {
        List<String> sdcardPaths = new ArrayList<String>();
        BufferedInputStream in = null;
        BufferedReader inBr = null;
        String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
        try {
            Process p = run.exec(cmd);// 启动另一个进程来执行命令
            in = new BufferedInputStream(p.getInputStream());
            inBr = new BufferedReader(new InputStreamReader(in));

            String lineStr;
            while ((lineStr = inBr.readLine()) != null) {
                // 获得命令执行后在控制台的输出信息
                LogUtil.i("SDCardUtil:getSDCardPath", lineStr);

                String[] temp = TextUtils.split(lineStr, " ");
                // 得到的输出的第二个空格后面是路径
                String result = temp[1];
                File file = new File(result);
                if (file.isDirectory() && file.canRead() && file.canWrite()) {
                    LogUtil.d("directory can read can write:",
                            file.getAbsolutePath());
                    // 可读可写的文件夹未必是sdcard，我的手机的sdcard下的Android/obb文件夹也可以得到
                    sdcardPaths.add(result);
                }
            }

            // 检查命令是否执行失败。
            if (p.waitFor() != 0 && p.exitValue() == 1) {
                // p.exitValue()==0表示正常结束，1：非正常结束
                LogUtil.i("SDCardUtil:getSDCardPath", "命令执行失败!");
            }
        } catch (Exception e) {
            LogUtil.i("SDCardUtil:getSDCardPath", e.toString());

            sdcardPaths.add(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
        } finally {
            IOStreamUtils.closeReader(inBr);
            IOStreamUtils.closeInputStream(in);
        }

        optimize(sdcardPaths);
        for (Iterator<String> iterator = sdcardPaths.iterator(); iterator
                .hasNext();) {
            String string = iterator.next();
            LogUtil.i("清除过后", string);
        }
        return sdcardPaths;
    }

    private static void optimize(List<String> sdcaredPaths) {
        if (sdcaredPaths.size() == 0) {
            return;
        }
        int index = 0;
        while (true) {
            if (index >= sdcaredPaths.size() - 1) {
                String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
                for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
                    if (sdcaredPaths.get(i).contains(lastItem)) {
                        sdcaredPaths.remove(i);
                    }
                }
                return;
            }

            String containsItem = sdcaredPaths.get(index);
            for (int i = index + 1; i < sdcaredPaths.size(); i++) {
                if (sdcaredPaths.get(i).contains(containsItem)) {
                    sdcaredPaths.remove(i);
                    i--;
                }
            }

            index++;
        }
    }
}
