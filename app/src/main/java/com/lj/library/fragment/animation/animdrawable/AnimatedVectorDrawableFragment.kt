package com.lj.library.fragment.animation.animdrawable

import android.os.Bundle
import android.widget.ImageView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import butterknife.BindView
import com.lj.library.R
import com.lj.library.fragment.BaseFragment

/**
 * 5.0开始加入AnimatedXXXXXXDrawable系列的类。android 不支持svg图片。
 * 不过5.0开始加入了可以支持部分svg功能的矢量图, xml中表现为在res/drawable文件下的以<vector>为根标签的文件，
 * 在代码中表现为VectorDrawable类。VectorDrawable是静态的矢量图，AnimatedVectorDrawable是可以动的矢量图。
 * 可以使用vector asset studio工具从svg/psd图片生成<vector>图片文件。为了兼容低于5.0的版本，有两种方式
 * 1.Vector Asset Studio为<vector>文件生成png文件兼容低于5.0的系统（可以在build/generated/res/pngs/里面找到生成的图片）
 *    没有引入Support Library则就是使用此方案
 * 2. 使用Support Library中的VectorDrawableCompat, AnimatedVectorDrawableCompat类使低版本的系统也能使用矢量图
 *    在build.gradle文件中加入
 *    <pre>
 *        android {
             defaultConfig {
                vectorDrawables.useSupportLibrary = true
              }
           }

           dependencies {
              compile 'com.android.support:appcompat-v7:23.2.0'
            }
 *     </pre>
 *
 */
class AnimatedVectorDrawableFragment: BaseFragment() {

    @BindView(R.id.iv_2)
    lateinit var img2: ImageView

    @BindView(R.id.iv_3)
    lateinit var img3: ImageView

    @BindView(R.id.iv_33)
    lateinit var img33: ImageView

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.animated_vector_drawable_fragment
    }

    override fun initComp(savedInstanceState: Bundle?) {
        img2.setImageResource(R.drawable.ic_colorize_black_24dp)

        // define an animated vector graphic as three separate resource files 推荐这种方式
        AnimatedVectorDrawableCompat.create(mContext, R.drawable.avd).also {
            img3.setImageDrawable(it)
            it?.start()
        }

        // define an animated vector graphic as a single XML file defining the entire drawable
        AnimatedVectorDrawableCompat.create(mContext, R.drawable.avd_single).also {
            img33.setImageDrawable(it)
            it?.start()
        }

    }
}
