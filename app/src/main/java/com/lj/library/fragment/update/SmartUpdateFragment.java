package com.lj.library.fragment.update;

import android.os.Bundle;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by liujie_gyh on 16/9/12.
 */
public class SmartUpdateFragment extends BaseFragment {

    @Override
    protected int initLayout(Bundle savedInstanceState) {
        return R.layout.test_fragment;
    }

    @Override
    protected void initComp(Bundle savedInstanceState) {
//        String localApkPath = AppUtils.getSourceApkPath(mActivity, mActivity.getPackageName());
//        String localApkMd5 = getMd5ByFile(new File(localApkPath));
//        // localApkSign跟本地版本号一起发送到服务器请求最新版本,服务器验证
//        // 若版本号不是最新的, 并且localApkMd5与服务器上的一致,
//        // 则返回最新版本号, patch文件的url, 新apk文件的url, 新apk文件的md5值
//        // 服务器最好再返回一个增量更新的开关变量, 万一出问题了,可以直接关闭该功能
//
//        // 假设网络请求返回, 并且下载好了patch文件,
//        int newVerCode = 510;
//        // 将旧apk和patch合成新的APK文件
//        int patchResult = PatchUtils.patch(AppUtils.getSourceApkPath(mActivity, mActivity.getPackageName()),
//                PatchUtils.getNewApkFilePath(mActivity, newVerCode),
//                PatchUtils.getPatchFilePath(mActivity, newVerCode));
//
//        // 合成成功
//        if (patchResult == 0) {
//            File newApkFile = new File(PatchUtils.getNewApkFilePath(mActivity, newVerCode));
//            String newApkMd5 = getMd5ByFile(newApkFile);
//            // 校验合成的APK文件是否正确,"2sdnfaf3489rq2jif"为服务器返回的正确的新APK文件的MD5值
//            if (newApkMd5.equals("2sdnfaf3489rq2jif")) {
//                AppUtils.installApp(mActivity, newApkFile);
//            }
//        }
    }

    private String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            value = bytes2Hex(digester.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }

        return new String(res);
    }
}
