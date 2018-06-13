package com.lj.library.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
 * /data/data/packagename文件夹为内部存储，访问不需要申请权限.<br/>
 *
 * /sdcard/Android/data/packagename文件夹为外部存储上的私有存储目录，
 * 从 Android 4.4 开始，读取或写入应用私有目录中的文件不再需要 READ_EXTERNAL_STORAGE 或 WRITE_EXTERNAL_STORAGE 权限。<br/>
 *
 * /sdcard文件夹下除Android目录下的所有文件都是外部存储上的共有文件.<br/>
 *
 * 只有{@link #getExternalFilesDirs}方法才有可能访问你插入的sdcard，其他的方法都是访问primary external storage的.
 *
 *
 *
 *
 * Created by jie.liu on 16/8/23.
 */
public class StorageUtils {

    /**
     * 获取项目在外部存储中保存缓存的根目录路径.
     *
     * @return
     */
    public static String getAppCacheRootDirPath() {
        return getExternalStorageDirectoryPath() + File.separator + Constants.CACHE_ROOT_DIR_NAME;
    }

    /**
     * 在/data/data/packagename/files目录下创建文件.
     *
     * @param context
     * @param fileName 文件名，不能有文件分隔符
     * @param mode     取值{@link Context#MODE_PRIVATE}:如果文件不存在,则创建文件, 如果存在,则清空内容 <br/>
     *                 {@link Context#MODE_APPEND}:如果文件不存在，则创建文件, 如果存在，则将写入游标定位在内容最后。<br/>
     *                 {@link Context#MODE_WORLD_READABLE}和{@link Context#MODE_WORLD_WRITEABLE}从API17开始已弃用，
     *                 从 Android N 开始，使用这些常量将会导致引发 SecurityException。
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
     * 返回/data/data/packagename/files目录下指定文件的读取流.
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
     * 返回/data/data/packagename/cache文件夹.<P/>
     * <p>
     * 当设备的内部存储空间不足时，Android 可能会删除这些缓存文件以回收空间。 但您不应该依赖系统来为您清理这些文件，
     * 而应该始终自行维护缓存文件，使其占用的空间保持在合理的限制范围内（例如 1 MB）。 当用户卸载您的应用时，这些文件也会被移除。
     *
     * @param context
     * @return
     */
    public static File getCacheDir(@NonNull Context context) {
        if (isExternalStorageWritable()) {
            return context.getCacheDir();
        } else {
            return null;
        }
    }

    /**
     * 返回/data/data/packagename/files文件夹.<p/>
     * <p>
     * 利用得到的files文件夹对象，可以在files文件夹下建立子文件夹及文件。
     * 以弥补{@link Context#openFileOutput(String, int)}方法不能创建子文件夹的功能。
     *
     * @param context
     * @return
     */
    public static File getFileDir(@NonNull Context context) {
        if (isExternalStorageWritable()) {
            return context.getFilesDir();
        } else {
            return null;
        }
    }

    /**
     * 创建并返回/data/data/packagename/app_name文件夹.其中app_name的name就是下面传入的参数<p/>
     * <p>
     * 其后可以利用返回的File对象，像普通的文件操作那样，创建子文件夹，读写文件等等。
     *
     * @param context
     * @param name    文件名，不能有文件分隔符
     * @param mode    取值{@link Context#MODE_PRIVATE}:如果文件不存在,则创建文件, 如果存在,则清空内容 <br/>
     *                {@link Context#MODE_APPEND}:如果文件不存在，则创建文件, 如果存在，则将写入游标定位在内容最后。<br/>
     *                {@link Context#MODE_WORLD_READABLE}和{@link Context#MODE_WORLD_WRITEABLE}从API17开始已弃用，
     *                从 Android N 开始，使用这些常量将会导致引发 SecurityException。
     * @return
     */
    public static File getDir(@NonNull Context context, @NonNull String name, int mode) {
        if (isExternalStorageWritable()) {
            return context.getDir(name, mode);
        } else {
            return null;
        }
    }

    /**
     * 删除应用的私有文件即/data/data/packagename/files文件夹下的文件.
     *
     * @param context
     * @param name    文件名，不能有文件分隔符
     */
    public static void deleteFile(@NonNull Context context, @NonNull String name) {
        if (isExternalStorageWritable()) {
            context.deleteFile(name);
        }
    }

    /**
     * 返回/data/data/packagename/files文件夹下的所有文件及顶级文件夹的名字.<p/>
     * <p>
     * 不会递归遍历子文件夹，只抓取顶级子文件夹的名字.
     *
     * @param context
     * @return
     */
    public static String[] fileList(@NonNull Context context) {
        if (isExternalStorageWritable()) {
            return context.fileList();
        } else {
            return null;
        }
    }

    /**
     * 获取primary storage，既手机自带的存储中的/sdcard/Android/data/packagename/files文件夹下的子文件夹.
     * 当用户卸载您的应用时，此目录及其内容将被删除。此外，系统媒体扫描程序不会读取这些目录中的文件，
     * 因此不能从 MediaStore 内容提供程序访问这些文件。
     *
     * @param context
     * @param type    取值 <br/>
     *                null : 获取/sdcard/Android/data/packagename/files文件夹 <br/>
     *                {@link android.os.Environment#DIRECTORY_MUSIC},<br/>
     *                获取/sdcard/Android/data/packagename/files/Music文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_PODCASTS},<br/>
     *                获取/sdcard/Android/data/packagename/files/Podcasts文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_RINGTONES},<br/>
     *                获取/sdcard/Android/data/packagename/files/Ringtones文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_ALARMS},<br/>
     *                获取/sdcard/Android/data/packagename/files/Alarms文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},<br/>
     *                获取/sdcard/Android/data/packagename/files/Notifications文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_PICTURES},<br/>
     *                获取/sdcard/Android/data/packagename/files/Pictures文件夹<br/>
     *                {@link android.os.Environment#DIRECTORY_MOVIES}<br/>
     *                获取/sdcard/Android/data/packagename/files/Movies文件夹<br/>
     */
    public static File getExternalFileDir(@NonNull Context context, String type) {
        if (isExternalStorageWritable()) {
            return context.getExternalFilesDir(type);
        } else {
            return null;
        }
    }

    /**
     * 该方法将会返回包含各个位置条目的 File 数组。 数组中的第一个条目被视为外部主存储；
     * 除非该位置已满或不可用，否则应该使用该位置。 如果您希望在支持 Android 4.3 和更低版本的同时访问两个可能的位置，
     * 请使用支持库中的静态方法 ContextCompat.getExternalFilesDirs()。
     * 在 Android 4.3 和更低版本中，此方法也会返回一个 File 数组，但其中始终仅包含一个条目。
     *
     * @param context
     * @param type The type of files directory to return. May be {@code null}
     *            for the root of the files directory or one of the following
     *            constants for a subdirectory:
     *            {@link android.os.Environment#DIRECTORY_MUSIC},
     *            {@link android.os.Environment#DIRECTORY_PODCASTS},
     *            {@link android.os.Environment#DIRECTORY_RINGTONES},
     *            {@link android.os.Environment#DIRECTORY_ALARMS},
     *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *            {@link android.os.Environment#DIRECTORY_PICTURES}, or
     *            {@link android.os.Environment#DIRECTORY_MOVIES}.
     * @return
     */
    public static File[] getExternalFilesDirs(@NonNull Context context, String type) {
        if (isExternalStorageWritable()) {
            return ContextCompat.getExternalFilesDirs(context, type);
        } else {
            return null;
        }
    }

    /**
     * 获取primary storage中，既手机自带的存储中的/sdcard/Android/data/packagename/cache文件夹.
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(@NonNull Context context) {
        if (isExternalStorageWritable()) {
            return context.getExternalCacheDir();
        } else {
            return null;
        }
    }

    /**
     * 获取primary storage，既手机自带的存储中的/sdcard文件夹下的子文件夹.
     * 存储在这些文件夹下的数据不会随着应用的卸载而删除.<p/>
     * <p>
     * 一般而言，应该将用户可通过您的应用获取的新文件保存到设备上的“公共”位置，以便其他应用能够在其中访问这些文件，
     * 并且用户也能轻松地从该设备复制这些文件。 执行此操作时，应使用共享的公共目录之一
     *
     * @param type 取值 <br/>
     *             {@link android.os.Environment#DIRECTORY_MUSIC},<br/>
     *             获取/sdcard/Music文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_PODCASTS},<br/>
     *             获取/sdcard/Podcasts文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_RINGTONES},<br/>
     *             获取/sdcard/Ringtones文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_ALARMS},<br/>
     *             获取/sdcard/Alarms文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},<br/>
     *             获取/sdcard/Notifications文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_PICTURES},<br/>
     *             获取/sdcard/Pictures文件夹<br/>
     *             {@link android.os.Environment#DIRECTORY_MOVIES}<br/>
     *             获取/sdcard/Movies文件夹<br/>
     */
    public static File getExternalStoragePublicDirectory(@NonNull String type) {
        if (isExternalStorageWritable()) {
            return Environment.getExternalStoragePublicDirectory(type);
        } else {
            return null;
        }
    }

    /**
     * 获取SDCard根目录路径.
     *
     * @return
     */
    public static String getExternalStorageDirectoryPath() {
        File sdcard = getExternalStorageDirectory();
        if (sdcard != null) {
            return sdcard.getAbsolutePath();
        } else {
            return Environment.getRootDirectory().getAbsolutePath();
        }
    }

    /**
     * 获取SDCard根目录.
     *
     * @return
     */
    public static File getExternalStorageDirectory() {
        if (isExternalStorageWritable()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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
                LogUtil.i("SDCardUtil:getExternalStorageDirectoryPath", lineStr);

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
                LogUtil.i("SDCardUtil:getExternalStorageDirectoryPath", "命令执行失败!");
            }
        } catch (Exception e) {
            LogUtil.i("SDCardUtil:getExternalStorageDirectoryPath", e.toString());

            sdcardPaths.add(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
        } finally {
            IOStreamUtils.closeReader(inBr);
            IOStreamUtils.closeInputStream(in);
        }

        optimize(sdcardPaths);
        for (Iterator<String> iterator = sdcardPaths.iterator(); iterator
                .hasNext(); ) {
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
