package com.lj.library.imagemanager.fresco;

import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by liujie_gyh on 16/8/11.
 */
public class FrescoImgLoader {

    public static void loadImage(String url, SimpleDraweeView draweeView) {
        Uri uri = Uri.parse(url);
        draweeView.setImageURI(uri);
    }

    public static void loadImage(Uri uri, SimpleDraweeView draweeView) {
        draweeView.setImageURI(uri);
    }
}
