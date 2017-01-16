package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.loofer.framework.base.DefaultAdapter;
import org.loofer.framework.utils.UiUtils;
import org.loofer.slidingmenu.SlidingMenu;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerHomeComponent;
import org.loofer.weread.di.module.HomeModule;
import org.loofer.weread.event.Event;
import org.loofer.weread.mvp.contract.HomeContract;
import org.loofer.weread.mvp.presenter.HomePresenter;
import org.loofer.weread.mvp.ui.fragment.LeftMenuFragment;
import org.loofer.weread.mvp.ui.fragment.RightMenuFragment;
import org.loofer.weread.widget.home.PagingScrollHelper;
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

    @BindView(R.id.recyclerview_home)
    RecyclerView mRecyclerViewHome;
    @BindView(R.id.swiperefreshLayout_home)
    SwipeRefreshLayout mSwipeRefreshLayoutHome;

    @BindView(R.id.toolbar_title_tv)
    TextView mTitle;
    @BindView(R.id.common_toolbar)
    Toolbar mCommonToolbar;


    private Paginate mPaginate;
    private boolean isLoadingMore;

    private SlidingMenu mSlidingMenu;
    private LeftMenuFragment mLeftMenu;
    private RightMenuFragment mRightMenu;
    PagingScrollHelper scrollHelper = new PagingScrollHelper();
    private long mLastClickTime;


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
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerViewHome.setAdapter(adapter);
        initRecycleView();
        initPaginate();
    }

    private void initRecycleView() {
        mSwipeRefreshLayoutHome.setOnRefreshListener(this);
        configRecycleView(mRecyclerViewHome, new LinearLayoutManager(this));
        scrollHelper.setUpRecycleView(mRecyclerViewHome);
//        scrollHelper.setOnPageChangeListener(this);
    }

    private void configRecycleView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * 开始加载更多
     */
    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    /**
     * 介绍加载更多
     */
    @Override
    public void endLoadMore() {
        isLoadingMore = false;
    }

    /**
     * 初始化Paginate,用于加载更多
     */
    private void initPaginate() {
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    mPresenter.getListByPage(false);
                }

                @Override
                public boolean isLoading() {
                    return isLoadingMore;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            };

            mPaginate = Paginate.with(mRecyclerViewHome, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .setLoadingListItemCreator(new LoadingListItemCreator() {
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_more, parent, false);
                            return new RecyclerView.ViewHolder(view) {
                            };
                        }

                        @Override
                        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                        }
                    })
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }


    private void initRecommend() {

    }


    @Override
    public void onRefresh() {
        mPresenter.getListByPage(true);
    }

    @Subscriber
    public void closeMenu(Event event) {
        mSlidingMenu.showContent();
    }


    @Override
    public void showNoMore() {
        UiUtils.makeText("没有更多数据了");
    }

    @Override
    public void showOnFailure() {
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
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
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