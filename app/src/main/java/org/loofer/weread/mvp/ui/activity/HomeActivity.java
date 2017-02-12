package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.loofer.framework.utils.UiUtils;
import org.loofer.slidingmenu.SlidingMenu;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerHomeComponent;
import org.loofer.weread.di.module.HomeModule;
import org.loofer.weread.event.Event;
import org.loofer.weread.mvp.contract.HomeContract;
import org.loofer.weread.mvp.presenter.HomePresenter;
import org.loofer.weread.mvp.ui.adapter.HomePagerAdapter;
import org.loofer.weread.mvp.ui.fragment.LeftMenuFragment;
import org.loofer.weread.mvp.ui.fragment.RightMenuFragment;
import org.loofer.weread.widget.PagerAdapter;
import org.loofer.weread.widget.VerticalViewPager;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * ============================================================
 * 版权： x x 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2016/12/28 16:59.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class HomeActivity extends WEActivity<HomePresenter> implements HomeContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.verticalViewPager)
    VerticalViewPager mVerticalViewPager;
    @BindView(R.id.swiperefreshLayout_home)
    SwipeRefreshLayout mSwipeRefreshLayoutHome;

    @BindView(R.id.toolbar_title_tv)
    TextView mTitle;
    @BindView(R.id.common_toolbar)
    Toolbar mCommonToolbar;


    private SlidingMenu mSlidingMenu;
    private LeftMenuFragment mLeftMenu;
    private RightMenuFragment mRightMenu;
    private long mLastClickTime;
    private HomePagerAdapter mHomePagerAdapter;


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerHomeComponent
                .builder()
                .appComponent(appComponent)
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_home, null, false);
    }


    @Override
    protected boolean useToolbar() {
        return false;
    }

    @Override
    protected void initData() {
        mTitle.setText("单 读");
        mCommonToolbar.setNavigationIcon(R.drawable.column);
        mCommonToolbar.inflateMenu(R.menu.menu_main);
        mCommonToolbar.setBackgroundResource(R.drawable.shadow);
        mSwipeRefreshLayoutHome.setOnRefreshListener(this);
        initMenu();
        initRecommend();
        mPresenter.getListByPage(true);

    }


    View.OnClickListener mOnNaviClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSlidingMenu.showMenu();
            mLeftMenu.startAnim();
        }
    };

    Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mSlidingMenu.showSecondaryMenu();
            mRightMenu.startAnim();
            return false;
        }
    };


    private void initMenu() {
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        // 设置触摸屏幕的模式
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置渐入渐出效果的值
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setFadeEnabled(true);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.left_menu);

        mLeftMenu = new LeftMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.left_menu, mLeftMenu).commit();
        mSlidingMenu.setSecondaryMenu(R.layout.right_menu);
        mRightMenu = new RightMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.right_menu, mRightMenu).commit();
        mCommonToolbar.setNavigationOnClickListener(mOnNaviClickListener);
        mCommonToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
    }

    private float lastDownX = 0F;
    private float lastDownY = 0F;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownX = event.getRawX();
                lastDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getRawX();
                float upY = event.getRawY();
                if (lastDownX - upX > 100 && Math.abs(lastDownY - upY) < 200) {
                    if (!mSlidingMenu.isSecondaryMenuShowing()) {
                        EventBus.getDefault().post(new Event(1000, "closeMenu"));
                    }
                } else if (upX - lastDownX > 100 && Math.abs(lastDownY - upY) < 200) {
                    if (mSlidingMenu.isSecondaryMenuShowing()) {
                        EventBus.getDefault().post(new Event(1000, "closeMenu"));
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setAdapter(final PagerAdapter adapter) {
        mHomePagerAdapter = (HomePagerAdapter) adapter;
        mVerticalViewPager.setAdapter(adapter);
        mVerticalViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }

    private boolean mIsLoading;
    VerticalViewPager.OnPageChangeListener mOnPageChangeListener = new VerticalViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mHomePagerAdapter.getCount() <= position + 1 && !mIsLoading && positionOffset == 0.0f) {
                if (mIsLoading) {
                    UiUtils.makeText("正在努力加载...");
                    return;
                }
                mPresenter.getListByPage(false);
                mIsLoading = true;
            }

        }

        @Override
        public void onPageSelected(int position) {
            setRefreshEnable(position == 0);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void initRecommend() {

    }

    @Override
    public void onRefresh() {
        mPresenter.getListByPage(true);
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        mSwipeRefreshLayoutHome.setEnabled(enable);
    }

    @Subscriber
    public void closeMenu(Event event) {
        mSlidingMenu.showContent();
    }


    @Override
    public void showNoMore() {
        UiUtils.makeText("没有更多数据了");
        mIsLoading = false;
    }

    @Override
    public void showOnFailure() {
        mIsLoading = false;
        UiUtils.makeText("加载数据失败，请检查您的网络");
    }


    @Override
    public void onBackPressed() {
        if (mSlidingMenu.isMenuShowing() || mSlidingMenu.isSecondaryMenuShowing()) {
            mSlidingMenu.showContent();
        } else {
            if (System.currentTimeMillis() - mLastClickTime <= 2000L) {
                super.onBackPressed();
            } else {
                mLastClickTime = System.currentTimeMillis();
                UiUtils.makeText("再按一次退出");
            }

        }
    }

    @Override
    public void showLoading() {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mSwipeRefreshLayoutHome.setRefreshing(true);
                    }
                });
    }

    @Override
    public void endLoadMore() {
        mIsLoading = false;
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayoutHome.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void killMyself() {

    }

}