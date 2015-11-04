package com.lj.library.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具.
 *
 * @author jie.liu
 */
public class ZipUtil {

    private onZipListener mOnZipListener;

    public enum ZipError {
        ZIP_FILE_NOT_EXISTS, ZIP_FILE_PATH_IS_EMPTY, TARGET_FILE_HAS_EXISTS, TARGET_FILE_NOT_EXISTS, TARGET_FILE_PATH_IS_EMPTY, IOEXCEPTION, ZIPEXCEPTION
    }

    /**
     * 压缩.
     *
     * @param srcFileOrDirPath 需要被压缩的文件或者文件夹路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void zip(String srcFileOrDirPath) throws FileNotFoundException, IOException {
        zip("", srcFileOrDirPath);
    }

    /**
     * 压缩.
     *
     * @param zipFilePath      压缩产生的zip包文件名--带路径,如果为null或空则默认按文件名生产压缩文件名
     * @param srcFileOrDirPath 需要被压缩的文件或者文件夹路径
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void zip(String zipFilePath, String srcFileOrDirPath) throws FileNotFoundException, IOException {
        zip(zipFilePath, "", srcFileOrDirPath);
    }

    /**
     * 压缩.
     *
     * @param zipFilePath      压缩产生的zip包文件名--带路径,如果为null或空则默认按文件名生产压缩文件名
     * @param relativePath     相对路径，默认为空, 用来标示每一个压缩节点的，代码递归需要这个参数，不用传
     * @param srcFileOrDirPath 需要被压缩的文件或者文件夹路径
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NullPointerException  if {@code srcFileOrDirPath} is {@code null}
     */
    private void zip(String zipFilePath, String relativePath, String srcFileOrDirPath)
            throws IOException {
        if (mOnZipListener != null) {
            mOnZipListener.onZipBegin();
        }
        String filePath = zipFilePath;
        if (TextUtils.isEmpty(filePath)) {
            filePath = createZipFilePathBy(srcFileOrDirPath);
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
        }
        zipOnAsyn(filePath, relativePath, srcFileOrDirPath);
    }

    private String createZipFilePathBy(String srcFileOrDirPath) {
        String filePath;
        if (TextUtils.isEmpty(srcFileOrDirPath)) {
            if (mOnZipListener != null) {
                mOnZipListener.onZipError(ZipError.TARGET_FILE_PATH_IS_EMPTY);
            }
            return null;
        }

        File zipFile = new File(srcFileOrDirPath);
        if (!zipFile.exists()) {
            if (mOnZipListener != null) {
                mOnZipListener.onZipError(ZipError.ZIP_FILE_NOT_EXISTS);
            }
            return null;
        }

        if (zipFile.isDirectory()) {
            filePath = srcFileOrDirPath + ".zip";
        } else {
            if (srcFileOrDirPath.indexOf(".") > 0) {
                filePath = srcFileOrDirPath.substring(0, srcFileOrDirPath.lastIndexOf(".") + 1) + "zip";
            } else {
                filePath = srcFileOrDirPath + ".zip";
            }
        }
        return filePath;
    }

    /**
     * 异步线程压缩.
     *
     * @param zipFilePath
     * @param relativePath
     * @param srcFileOrDirPath
     * @throws FileNotFoundException
     */
    private void zipOnAsyn(String zipFilePath, String relativePath, String srcFileOrDirPath)
            throws FileNotFoundException {
        LogUtil.i(this, "开启线程压缩文件:" + srcFileOrDirPath);
        HandlerThread thread = new HandlerThread("ZipThread");
        thread.start();

        Looper looper = thread.getLooper();
        mZipHandler = new ZipHandler(looper, zipFilePath, relativePath, srcFileOrDirPath);
        mZipHandler.sendEmptyMessage(0);
    }

    private ZipHandler mZipHandler;

    private class ZipHandler extends Handler {

        private String mZipFilePath;
        private String mRelativePath;
        private String mSrcFileOrDirPath;
        private ZipOutputStream mZos;

        public ZipHandler(Looper looper, String zipFilePath, String relativePath, String srcFileOrDirPath)
                throws FileNotFoundException {
            super(looper);
            mZipFilePath = zipFilePath;
            mRelativePath = relativePath;
            mSrcFileOrDirPath = srcFileOrDirPath;
            if (TextUtils.isEmpty(mZipFilePath)) {
                if (mOnZipListener != null) {
                    mOnZipListener.onZipError(ZipError.ZIP_FILE_PATH_IS_EMPTY);
                }
            } else {
                mZos = new ZipOutputStream(new FileOutputStream(mZipFilePath));
            }
        }

        @Override
        public void handleMessage(Message msg) {
            int result = 0;
            try {
                zip(mZos, mRelativePath, mSrcFileOrDirPath);
            } catch (IOException e) {
                if (mOnZipListener != null) {
                    mOnZipListener.onZipError(ZipError.IOEXCEPTION);
                }
                e.printStackTrace();
                result = -1;
            } finally {
                closeZipOutputStream();
            }

            if (result == 0 && mOnZipListener != null) {
                mOnZipListener.onZipFinished(mZipFilePath);
            }
        }

        private void closeZipOutputStream() {
            try {
                if (null != mZos) {
                    mZos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩
     *
     * @param zos              压缩输出流
     * @param relativePath     相对路径
     * @param srcFileOrDirPath 文件或文件夹绝对路径
     * @throws IOException
     */
    private void zip(ZipOutputStream zos, String relativePath, String srcFileOrDirPath) throws IOException {
        if (TextUtils.isEmpty(srcFileOrDirPath)) {
            if (mOnZipListener != null) {
                mOnZipListener.onZipError(ZipError.TARGET_FILE_PATH_IS_EMPTY);
            }
            return;
        }

        File file = new File(srcFileOrDirPath);
        if (!file.exists()) {
            if (mOnZipListener != null) {
                mOnZipListener.onZipError(ZipError.TARGET_FILE_NOT_EXISTS);
            }
            return;
        }

        if (file.isDirectory()) {
            LogUtil.d(this, "压缩文件夹:" + file.getAbsolutePath());
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File tempFile = files[i];
                if (tempFile.isDirectory()) {
                    LogUtil.d(this, "压缩文件夹:" + tempFile.getAbsolutePath());
                    String newRelativePath = relativePath + tempFile.getName() + File.separator;
                    createZipNode(zos, newRelativePath);
                    zip(zos, newRelativePath, tempFile.getPath());
                } else {
                    zipFile(zos, tempFile, relativePath);
                }
            }
        } else {
            zipFile(zos, file, relativePath);
        }
    }

    /**
     * 压缩文件
     *
     * @param zos          压缩输出流
     * @param file         文件对象
     * @param relativePath 相对路径
     * @throws IOException
     */
    private void zipFile(ZipOutputStream zos, File file, String relativePath) throws IOException {
        ZipEntry entry = new ZipEntry(relativePath + file.getName());
        LogUtil.d(this, "压缩文件:" + entry.getName());
        zos.putNextEntry(entry);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            final int BUFFERSIZE = 2 << 10;
            int length = 0;
            byte[] buffer = new byte[BUFFERSIZE];
            while ((length = is.read(buffer, 0, BUFFERSIZE)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.flush();
            zos.closeEntry();
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    /**
     * 创建目录
     *
     * @param zos          zip输出流
     * @param relativePath 相对路径
     * @throws IOException
     */
    private void createZipNode(ZipOutputStream zos, String relativePath) throws IOException {
        LogUtil.d(this, "创建ZipEntry:" + relativePath);
        ZipEntry zipEntry = new ZipEntry(relativePath);
        zos.putNextEntry(zipEntry);
        zos.closeEntry();
    }

    public void unzipFile(String zipFilePath, String targetDirPath) {
        if (TextUtils.isEmpty(zipFilePath)) {
            if (mOnZipListener != null) {
                mOnZipListener.onUnzipError(ZipError.ZIP_FILE_PATH_IS_EMPTY);
            }
            return;
        }

        File file = new File(zipFilePath);
        if (!file.exists()) {
            if (mOnZipListener != null) {
                mOnZipListener.onUnzipError(ZipError.ZIP_FILE_NOT_EXISTS);
            }
            return;
        }

        unzipFile(file, targetDirPath);
    }

    /**
     * 解压缩功能. 将zipFile文件解压到指定目录下
     *
     * @param zipFile
     * @param targetDirPath 解压后文件会在该文件夹下
     */
    public void unzipFile(File zipFile, String targetDirPath) {
        if (!zipFile.exists()) {
            if (mOnZipListener != null) {
                mOnZipListener.onUnzipError(ZipError.ZIP_FILE_NOT_EXISTS);
            }
            return;
        }

        LogUtil.i(this, "开启线程解压文件:" + zipFile.getAbsolutePath());
        HandlerThread thread = new HandlerThread("UnzipThread");
        thread.start();

        Looper looper = thread.getLooper();
        mUnzipHandler = new UnzipHandler(looper, zipFile, targetDirPath);
        mUnzipHandler.sendEmptyMessage(0);

    }

    public void setOnZipListener(onZipListener onZipListener) {
        mOnZipListener = onZipListener;
    }

    private UnzipHandler mUnzipHandler;

    private class UnzipHandler extends Handler {

        private final File mZipFile;
        private String mTargetDirPath;

        public UnzipHandler(Looper looper, File zipFile, String dirPath) {
            super(looper);
            mZipFile = zipFile;
            mTargetDirPath = dirPath;
        }

        @Override
        public void handleMessage(Message msg) {
            int result = 0;
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(mZipFile);
                Enumeration<? extends ZipEntry> zList = zipFile.entries();
                ZipEntry zipEntry = null;
                byte[] buf = new byte[1024];
                while (zList.hasMoreElements()) {
                    zipEntry = zList.nextElement();
                    if (zipEntry.isDirectory()) {
                        LogUtil.d(this, "zipEntry.getName() = " + zipEntry.getName());
                        if (!mTargetDirPath.endsWith(File.separator)) {
                            mTargetDirPath += File.separator;
                        }

                        String dirStr = mTargetDirPath + zipEntry.getName();
                        dirStr = new String(dirStr.getBytes("8859_1"), "GB2312");
                        LogUtil.d(this, "str = " + dirStr);
                        File f = new File(dirStr);
                        f.mkdir();
                        continue;
                    }
                    LogUtil.d(this, "zipEntry.getName() = " + zipEntry.getName());
                    OutputStream os = new BufferedOutputStream(
                            new FileOutputStream(getRealFile(mTargetDirPath, zipEntry.getName())));
                    InputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    int readLen = 0;
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        os.write(buf, 0, readLen);
                    }
                    is.close();
                    os.close();
                }
                zipFile.close();
            } catch (ZipException e) {
                if (mOnZipListener != null) {
                    mOnZipListener.onUnzipError(ZipError.ZIPEXCEPTION);
                }
                e.printStackTrace();
                result = -1;
            } catch (IOException e) {
                if (mOnZipListener != null) {
                    mOnZipListener.onUnzipError(ZipError.IOEXCEPTION);
                }
                e.printStackTrace();
                result = -1;
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                        zipFile = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LogUtil.d(this, "finish");

            if (result == 0 && mOnZipListener != null) {
                mOnZipListener.onUnzipFinished(mZipFile, mTargetDirPath);
            }
        }
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir          指定根目录
     * @param relativeFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private File getRealFile(String baseDir, String relativeFileName) {
        String[] dirs = relativeFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);
            }
            LogUtil.d(this, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                LogUtil.d(this, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            LogUtil.d(this, "2ret = " + ret);
        } else {
            ret = new File(baseDir, relativeFileName);
        }
        return ret;
    }

    /**
     * 得到压缩文件的路径，指支持调用{@link ZipUtil#zip(String)}
     * 方法自动生成zip路径，或者调用其他压缩方法，不指定zipFilePath.
     *
     * @param srcFileOrDirPath 被压缩的文件或者文件夹的路径
     * @return
     */
    public String getZipFilePath(String srcFileOrDirPath) {
        String filePath = null;
        File temp = new File(srcFileOrDirPath);
        if (temp.isDirectory()) {
            filePath = srcFileOrDirPath + ".zip";
        } else {
            if (srcFileOrDirPath.indexOf(".") > 0) {
                filePath = srcFileOrDirPath.substring(0, srcFileOrDirPath.lastIndexOf(".") + 1) + "zip";
            } else {
                filePath = srcFileOrDirPath + ".zip";
            }
        }
        return filePath;
    }

    public interface onZipListener {

        /**
         * 开始解压文件
         */
        void onUnzipBegin();

        /**
         * 解压完成调用此方法
         *
         * @param zipFile   被解压的zip文件
         * @param targetDir 将文件解压到此文件夹下
         */
        void onUnzipFinished(File zipFile, String targetDir);

        /**
         * 解压出错调用
         *
         * @param zipError
         */
        void onUnzipError(ZipError zipError);

        /**
         * 开始压缩文件.
         */
        void onZipBegin();

        /**
         * 压缩成功返回.
         *
         * @param zipFilePath
         */
        void onZipFinished(String zipFilePath);

        /**
         * 压缩出错返回.
         *
         * @param zipError
         */
        void onZipError(ZipError zipError);
    }
}
