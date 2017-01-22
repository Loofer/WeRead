package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import org.loofer.framework.base.DefaultAdapter;
import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerDailyComponent;
import org.loofer.weread.di.module.DailyModule;
import org.loofer.weread.mvp.contract.DailyContract;
import org.loofer.weread.mvp.presenter.DailyPresenter;
import org.loofer.weread.widget.HorizontalDividerItemDecoration;
import org.loofer.weread.widget.home.PagingScrollHelper;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static org.loofer.framework.utils.Preconditions.checkNotNull;


/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 10:41.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class DailyActivity extends WEActivity<DailyPresenter> implements DailyContract.View, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerview_daily)
    RecyclerView mRecyclerviewDaily;
    @BindView(R.id.swiperefreshLayout_daily)
    SwipeRefreshLayout mSwiperefreshLayoutDaily;

    PagingScrollHelper scrollHelper = new PagingScrollHelper();
    private Paginate mPaginate;
    private boolean isLoadingMore;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerDailyComponent
                .builder()
                .appComponent(appComponent)
                .dailyModule(new DailyModule(this)) //请将DailyModule()第一个首字母改为小写
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_daily, null, false);
    }

    @Override
    protected void initData() {
        setToolbarTitle("单向历");
        mPresenter.getListByPage(true);
    }


    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerviewDaily.setAdapter(adapter);
        initRecycleView();
        initPaginate();
    }

    private void initRecycleView() {
        mSwiperefreshLayoutDaily.setOnRefreshListener(this);
        configRecycleView(mRecyclerviewDaily, new LinearLayoutManager(this));
        scrollHelper.setUpRecycleView(mRecyclerviewDaily);
    }

    private void configRecycleView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void onRefresh() {
        mPresenter.getListByPage(true);
    }

    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    @Override
    public void endLoadMore() {
        isLoadingMore = false;
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
    public void showLoading() {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mSwiperefreshLayoutDaily.setRefreshing(true);
                    }
                });
    }

    @Override
    public void hideLoading() {
        mSwiperefreshLayoutDaily.setRefreshing(false);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

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

            mPaginate = Paginate.with(mRecyclerviewDaily, callbacks)
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

}