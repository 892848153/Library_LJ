package com.lj.library.widget.viewpager.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lj.library.R;
import com.lj.library.util.LogUtil;
import com.lj.library.widget.viewpager.MyViewPager;
import com.lj.library.widget.viewpager.OnPageChangeListener;
import com.lj.library.widget.viewpager.PagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告栏.
 * <p/>
 * 例一:
 * <p/>
 * <pre>
 * List&lt;View&gt; viewList = new ArrayList&lt;View&gt;();
 * ImageView imgView0 = new ImageView(this);
 * imgView0.setImageResource(R.drawable.bg_flash_top);
 * ImageView imgView1 = new ImageView(this);
 * imgView1.setImageResource(R.drawable.bg_flash_top);
 * ImageView imgView2 = new ImageView(this);
 * imgView2.setImageResource(R.drawable.bg_flash_top);
 * ImageView imgView3 = new ImageView(this);
 * imgView3.setImageResource(R.drawable.bg_flash_top);
 * ImageView imgView4 = new ImageView(this);
 * imgView4.setImageResource(R.drawable.bg_flash_top);
 *
 * viewList.add(imgView0);
 * viewList.add(imgView1);
 * viewList.add(imgView2);
 * viewList.add(imgView3);
 * viewList.add(imgView4);
 *
 * PagerAdapter<UserInfo> adapter = new DefaultPagerAdapter(viewList)<UserInfo>;
 * BannerView banner = (BannerView) findViewById(R.id.banner);
 * banner.setPagerAdapter(adapter);
 * // 默认是不能滚动的
 * banner.enableCycleScroll();
 * banner.setCurrentPage(1);
 * // 开启自动滚动功能
 * banner.startAutoCycle();
 * banner.setCycleInterval(5000);
 * </pre>
 * <p/>
 * 例二:
 * <p/>
 * <pre>
 * BannerView banner = (BannerView) findViewById(R.id.banner);
 * PagerAdapter&lt;ShCarInfoEntity&gt; pagerAdapter = new BannerAdapter();
 * pagerAdapter.init(bean.main_info, true);
 * mBanner.setPageAdapter(pagerAdapter);
 * if (pagerAdapter.getCount() &gt;= 4) {
 * 	mBanner.setCycleInterval(4000);
 * 	mBanner.enableCycleScroll();
 * 	mBanner.startAutoCycle();
 * 	mBanner.setCurrentPage(1);
 * }
 *
 * private class BannerAdapter extends PagerAdapter&lt;ShCarInfoEntity&gt; {
 *
 * 	&#064;Override
 * 	public View getView(int position, ShCarInfoEntity data) {
 * 		View view = LayoutInflater.from(MainActivity.this).inflate(
 * 				R.layout.banner_item, null);
 * 		ScaledImageView iv = (ScaledImageView) view.findViewById(R.id.iv);
 * 		iv.setFrameWeight(660, 300);
 * 		ImageCacheManager manager = new ImageCacheManager(MainActivity.this);
 * 		manager.getBitmap(data.headAddress, iv);
 * 		return view;
 *    }
 * }
 * </pre>
 *
 * @author jie.liu
 * @time 2014年8月6日 上午10:22:16
 */

public class BannerView<T> extends RelativeLayout implements
        OnPageChangeListener {

    private Context mContext;

    private MyViewPager<T> mViewPager;

    private PagerAdapter<T> mPagerAdapter;

    private LinearLayout mIndicatorLlyt;

    private ImageView imgs[];

    private OnPageChangeListener mOutListener;

    private boolean mCycleScrollable;

    private boolean mAutoScrollable;

    private int mPageCount;

    private Handler mHandler;

    private long mInterval = 3000;

    private List<ViewGroup> mParentViews;

    private int mIndicatorResId = R.drawable.page_indicator_bg;

    private final int WHAT_AUTO_CYCLE = 0;

    public BannerView(Context context) {
        super(context);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressWarnings("unchecked")
    private void init(Context context) {
        mContext = context;
        if (mHandler == null) {
            mHandler = new InternalHandler<T>(this);
        }

        if (mParentViews == null) {
            mParentViews = new ArrayList<ViewGroup>();
        }
        if (getChildAt(0) == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            inflater.inflate(R.layout.banner_view, this);
            mViewPager = (MyViewPager<T>) findViewById(R.id.view_pager);
            mIndicatorLlyt = (LinearLayout) findViewById(R.id.indicator_llyt);
            mViewPager.SetOnViewChangeListener(this);
        }
    }

    /**
     * 传递进来的页面必须四页以上才能无限循环滑动，有效页数是两页.
     */
    public void enableCycleScroll() {
        if (mPagerAdapter == null) {
            throw new NullPointerException("Pager Adapter is null");
        }

        if (mCycleScrollable == true || mPageCount < 4) // 传递进来的页面必须四页以上才能轮播，有效页数是两页
            return;

        mCycleScrollable = true;
        invalidateIndicators();
    }

    /**
     * 禁止无限循环滑动.
     */
    public void disableCycleSroll() {
        if (mCycleScrollable == false || mPageCount < 4
                || mPagerAdapter == null) {
            return;
        }

        if (mAutoScrollable) {
            stopAutoCycle();
        }
        // TODO更改View列表的数量
        mCycleScrollable = false;
        invalidateIndicators();
    }

    /**
     * 页面更改时调用此方法，带有循环滑动功能时，这个方法不准.
     *
     * @param listener
     */
    public void SetOnViewChangeListener(OnPageChangeListener listener) {
        if (mOutListener != listener) {
            mOutListener = listener;
        }
    }

    /**
     * 调用此方法会清除当前包含的子控件.
     *
     * @param adapter
     */
    public void setPageAdapter(PagerAdapter<T> adapter) {
        setPageAdapter(adapter, true);
    }

    /**
     * @param adapter
     * @param shouldCleanChildren 是否清除当前包含的子控件
     */
    public void setPageAdapter(PagerAdapter<T> adapter, boolean shouldCleanChildren) {
        if (adapter == null) {
            throw new NullPointerException("PagerAdapter is null");
        }

        if (mPagerAdapter == adapter) {
            return;
        }

        mPagerAdapter = adapter;
        resetBanner();
        mViewPager.setAdapter(mPagerAdapter, shouldCleanChildren);
        invalidateIndicators();
    }

    private void resetBanner() {
        stopAutoCycle();
        disableCycleSroll();
        if (mPagerAdapter != null) {
            mPageCount = mPagerAdapter.getCount();
        }
        mViewPager.setCurrentScreen(0);
    }

    // public PagerAdapter<T> getPagerAdapter() {
    // return mViewPager.getPagerAdapter();
    // }

    /**
     * 设置宽高比例.
     *
     * @param widthWeight  宽的比重, 必须大于0
     * @param heightWeight 高的比重,必须大于0
     */
    public void setFrameWeight(float widthWeight, float heightWeight) {
        mViewPager.setFrameWeight(widthWeight, heightWeight);
    }

    /**
     * 显示提示点，默认是显示的.
     */
    public void showIndicators() {
        mIndicatorLlyt.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏提示点,默认是显示的.
     */
    public void hideIndicator() {
        mIndicatorLlyt.setVisibility(View.GONE);
    }

    /**
     * 创建提示点，会根据是否是循环滑动的来自动的适应点数.
     */
    private void invalidateIndicators() {
        mIndicatorLlyt.removeAllViews();
        int indicatorCount = calcIndicatorCount();
        if (indicatorCount > 0) {
            performCreateIndicators(indicatorCount);
        }
    }

    private int calcIndicatorCount() {
        int indicatorCount = 0;
        if (mCycleScrollable) {
            indicatorCount = mPageCount - 2;
        } else {
            indicatorCount = mPageCount;
        }
        return indicatorCount;
    }

    private void performCreateIndicators(int indicatorCount) {
        if (indicatorCount <= 0) {
            throw new IllegalArgumentException("需要创建的标识点数<=0");
        }

        imgs = new ImageView[indicatorCount];
        for (int i = 0; i < indicatorCount; i++) {
            ImageView img = new ImageView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            img.setImageResource(mIndicatorResId);
            mIndicatorLlyt.addView(img, params);
            imgs[i] = img;
        }
        imgs[0].setEnabled(false);
    }

    /**
     * 设置标识点的图片ID.
     *
     * @param drawableResId
     */
    public void setIndicatorResId(int drawableResId) {
        if (mIndicatorResId != drawableResId) {
            mIndicatorResId = drawableResId;
        }
        resetIndicatorsDrawable();
    }

    private void resetIndicatorsDrawable() {
        if (imgs != null && imgs.length > 0) {
            int length = imgs.length;
            for (int i = 0; i < length; i++) {
                imgs[i].setImageResource(mIndicatorResId);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 当手指触到控件的时候，让父ScrollView交出ontouch权限，也就是让父scrollview 停住不能滚动
                setParentScrollable(false);
                LogUtil.d(this, "onInterceptTouchEvent ACTION_DOWN");
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d(this, "onInterceptTouchEvent ACTION_UP");
            case MotionEvent.ACTION_CANCEL:
                LogUtil.d(this, "onInterceptTouchEvent ACTION_CANCEL");
                // 当手指松开时，让父ScrollView重新拿到onTouch权限
                setParentScrollable(true);
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否把滚动事件交给父scrollview.
     *
     * @param flag
     */
    private void setParentScrollable(boolean flag) {
        for (ViewGroup parentView : mParentViews) {
            parentView.requestDisallowInterceptTouchEvent(!flag);
        }
    }

    /**
     * 增加包含它的可滑动的View，这样可以使滑动动作不冲突.
     *
     * @param parentView
     */
    public void addParentScrollView(ViewGroup parentView) {
        if (parentView == null) {
            throw new NullPointerException("parent View is null");
        }

        if (!mParentViews.contains(parentView)) {
            mParentViews.add(parentView);
        }

        mViewPager.addParentScrollView(parentView);
    }

    /**
     * 设置每个提示点的margin.
     *
     * @param margin
     */
    public void setIndicatorMargin(int margin) {
        setIndicatorMargin(margin, margin, margin, margin);
    }

    /**
     * 设置每个提示点的margin.
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setIndicatorMargin(int left, int top, int right, int bottom) {
        if (imgs != null && imgs.length > 0) {
            int count = imgs.length;
            for (int i = 0; i < count; i++) {
                LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) imgs[i]
                        .getLayoutParams();
                params.setMargins(left, top, right, bottom);
            }
        }
    }

    @Override
    public void OnPageChange(int currentPage) {
        int realCurrentPage = calcRealCurrPage(currentPage);
        if (realCurrentPage >= 0 && realCurrentPage < mPageCount) {
            setCurrentPoint(realCurrentPage);
        }

        if (mAutoScrollable) {
            sendAutoCycleMsg();
        }
        if (mCycleScrollable) {
            snapToRightPage(currentPage);
        }

        if (mOutListener != null) {
            mOutListener.OnPageChange(realCurrentPage);
        }
    }

    /**
     * 计算真实情况的第几页，用于显示标识点.
     *
     * @param currentPage
     * @return
     */
    private int calcRealCurrPage(int currentPage) {
        if (!mCycleScrollable) {
            return currentPage;
        }

        int realCurrentPage = (currentPage % mPageCount) - 1;
        if (realCurrentPage == -1) {
            realCurrentPage = mPageCount - 3;
            return realCurrentPage;
        }
        if (realCurrentPage == mPageCount - 2) {
            realCurrentPage = 0;
            return realCurrentPage;
        }
        return realCurrentPage;
    }

    /**
     * 标识出当前页面的点.
     *
     * @param position
     */
    private void setCurrentPoint(int position) {
        if (position < 0 || position > mPageCount - 1 || imgs == null) {
            return;
        }

        int length = imgs.length;
        for (int i = 0; i < length; i++) {
            imgs[i].setEnabled(true);
            if (i == position) {
                imgs[i].setEnabled(false);
            }
        }
    }

    /**
     * 轮播时，用于首尾页的时候滑动到真实的页面.
     *
     * @param currentPage
     */
    private void snapToRightPage(int currentPage) {
        if (mCycleScrollable) {
            int snapPage = calcSnapPage(currentPage);
            if (currentPage == 0 || currentPage == mPageCount - 1) {
                mViewPager.snapToScreenWithoutAnim(snapPage);
            }
        }
    }

    /**
     * 计算需要滑动到的页面,用于首尾页的时候滑动.
     *
     * @param currentPage
     * @return
     */
    private int calcSnapPage(int currentPage) {
        if (!mCycleScrollable) {
            return currentPage;
        }

        int snapPage = currentPage;
        if (snapPage == 0) {
            snapPage = mPageCount - 2;
            return snapPage;
        }
        if (snapPage == mPageCount - 1) {
            snapPage = 1;
            return snapPage;
        }
        return snapPage;
    }

    /**
     * 设置当前页面的为第几页，从0开始.
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        mViewPager.setCurrentScreen(currentPage);
    }

    /**
     * 开始自动轮播，必须先调用{@link #enableCycleScroll()}方法在调用此方法才有效.
     */
    public void startAutoCycle() {
        if (mCycleScrollable) {
            mAutoScrollable = true;
            enableCycleScroll();
            sendAutoCycleMsg();
        }
    }

    private void sendAutoCycleMsg() {
        if (mAutoScrollable) {
            clearAutoCycleMsgs();
            mHandler.sendEmptyMessageDelayed(WHAT_AUTO_CYCLE, mInterval);
        }
    }

    /**
     * 停止自动轮播功能.
     */
    public void stopAutoCycle() {
        mAutoScrollable = false;
        clearAutoCycleMsgs();
    }

    private void clearAutoCycleMsgs() {
        if (mHandler.hasMessages(WHAT_AUTO_CYCLE)) {
            mHandler.removeMessages(WHAT_AUTO_CYCLE);
        }
    }

    /**
     * 设置页面滚动时间间隔，开启自动轮播功能时才有效.
     *
     * @param interval 时间间隔，单位是毫秒
     */
    public void setCycleInterval(long interval) {
        if (mAutoScrollable) {
            mInterval = interval;
            stopAutoCycle();
            startAutoCycle();
        }
    }

    /**
     * 设置页面切换动画的时间, 默认是500毫秒.
     *
     * @param mills
     */
    public void setScrollDuration(int mills) {
        if (mViewPager != null) {
            mViewPager.setScrollDuration(mills);
        }
    }

    /**
     * 是否页面在滑动状态.
     *
     * @return 只要页面不是刚好一整页占据着控件就返回true
     */
    public boolean isScolling() {
        return mViewPager.isScrolling();
    }

    private static class InternalHandler<T> extends Handler {

        private final WeakReference<BannerView<T>> mViewRef;

        public InternalHandler(BannerView<T> view) {
            mViewRef = new WeakReference<BannerView<T>>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BannerView<T> bannerView = mViewRef.get();
            if (bannerView != null && !bannerView.isScolling()) {
                bannerView.mViewPager.snapToScreen(bannerView.mViewPager
                        .getCurrentScreen() + 1);
            }
        }
    }
}
