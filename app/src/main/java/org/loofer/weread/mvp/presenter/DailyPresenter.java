package org.loofer.weread.mvp.presenter;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.loofer.framework.base.DefaultAdapter;
import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.RxUtils;
import org.loofer.framework.utils.UiUtils;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.mvp.contract.DailyContract;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.ui.activity.AudioDetailActivity;
import org.loofer.weread.mvp.ui.activity.TextDetailActivity;
import org.loofer.weread.mvp.ui.activity.VideoDetailActivity;
import org.loofer.weread.mvp.ui.adapter.DailyAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 10:42.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 * ==========================================================
 */
@ActivityScope
public class DailyPresenter extends BasePresenter<DailyContract.Model, DailyContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private DailyAdapter mAdapter;
    private List<HomeItem> mHomeItemList = new ArrayList<>();
    private int mPage = 1;
    private int model =4;
    private String pageId = "0";
    private String lastItemCreateTime = "0";


    @Inject
    public DailyPresenter(DailyContract.Model model, DailyContract.View rootView
            , RxErrorHandler handler, Application application) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        mAdapter = new DailyAdapter(mHomeItemList);
        mRootView.setAdapter(mAdapter);//设置Adapter
//        mAdapter.setOnItemClickListener(mOnRecyclerViewItemClickListener);
    }


    public void getListByPage(final boolean pullToRefresh) {
        if (pullToRefresh) {
            mPage = 1;
            pageId = "0";
            lastItemCreateTime = "0";
        } else {
            pageId = mAdapter.getLastItemId();
            lastItemCreateTime = mAdapter.getLastItemCreateTime();
        }
        mModel.getListByPage(mPage, model, pageId, DeviceUtils.getDeviceId(mApplication), lastItemCreateTime)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mPage == 1)
                            mRootView.showLoading();//显示上拉刷新的进度条
                        else
                            mRootView.startLoadMore();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (mPage == 2)
                            mRootView.hideLoading();//隐藏上拉刷新的进度条
                        else
                            mRootView.endLoadMore();
                    }
                })
                .compose(RxUtils.<List<HomeItem>>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new Subscriber<List<HomeItem>>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   mRootView.showOnFailure();
                               }

                               @Override
                               public void onNext(List<HomeItem> homeItemList) {
//                                   int size = homeItemList.size();
//                                   if (size > 0) {
                                   if (mPage == 1)
                                       mHomeItemList.clear();
                                   for (HomeItem item : homeItemList) {
                                       mHomeItemList.add(item);
                                   }
                                   mPage++;
                                   mAdapter.notifyDataSetChanged();
//                            mRootView.updateListUI(baseJson.getData());
//                                   } else {
//                                       mRootView.showNoMore();
//                                   }
                               }
                           }

                );

    }

    DefaultAdapter.OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener = new DefaultAdapter.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, Object data, int position) {
            HomeItem item = (HomeItem) data;
            int model = Integer.valueOf(item.getModel());
            Intent intent;
            switch (model) {
                case 5:
                    Uri uri = Uri.parse(item.getHtml5());
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    UiUtils.startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(mApplication, AudioDetailActivity.class);
                    intent.putExtra("item", item);
                    UiUtils.startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(mApplication, VideoDetailActivity.class);
                    intent.putExtra("item", item);
                    UiUtils.startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(mApplication, TextDetailActivity.class);
                    intent.putExtra("item", item);
                    UiUtils.startActivity(intent);
                    break;
                default:
                    intent = new Intent(mApplication, TextDetailActivity.class);
                    intent.putExtra("item", item);
                    UiUtils.startActivity(intent);
            }
        }
    };




    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mApplication = null;
    }

}