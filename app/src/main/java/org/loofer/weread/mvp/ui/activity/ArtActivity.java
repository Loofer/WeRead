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
import org.loofer.weread.di.component.DaggerArtComponent;
import org.loofer.weread.di.module.ArtModule;
import org.loofer.weread.mvp.contract.ArtContract;
import org.loofer.weread.mvp.presenter.ArtPresenter;
import org.loofer.weread.widget.HorizontalDividerItemDecoration;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static org.loofer.framework.utils.Preconditions.checkNotNull;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/11 21:04.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class ArtActivity extends WEActivity<ArtPresenter> implements ArtContract.View, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerview_art)
    RecyclerView mRecyclerviewArt;
    @BindView(R.id.swiperefreshLayout_art)
    SwipeRefreshLayout mSwiperefreshLayoutArt;
    private int mMode;

    private Paginate mPaginate;
    private boolean isLoadingMore;


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerArtComponent
                .builder()
                .appComponent(appComponent)
                .artModule(new ArtModule(this)) //请将ArtModule()第一个首字母改为小写
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_art, null, false);
    }

    @Override
    protected void initData() {
        String title = getIntent().getStringExtra("title");
        setToolbarTitle(title);
        mMode = getIntent().getIntExtra("mode", 1);
        mPresenter.getListByPage(mMode, true);
    }


    @Override
    public void setAdapter(DefaultAdapter adapter) {
        mRecyclerviewArt.setAdapter(adapter);
        initRecycleView();
        initPaginate();
    }

    private void initRecycleView() {
        mSwiperefreshLayoutArt.setOnRefreshListener(this);
        configRecycleView(mRecyclerviewArt, new LinearLayoutManager(this));
    }

    private void configRecycleView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                        mSwiperefreshLayoutArt.setRefreshing(true);
                    }
                });
    }

    @Override
    public void hideLoading() {
        mSwiperefreshLayoutArt.setRefreshing(false);
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

    @Override
    public void onRefresh() {
        mPresenter.getListByPage(mMode, true);
    }

    private void initPaginate() {
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    mPresenter.getListByPage(mMode, false);
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

            mPaginate = Paginate.with(mRecyclerviewArt, callbacks)
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