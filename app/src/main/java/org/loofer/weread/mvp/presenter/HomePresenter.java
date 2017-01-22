package org.loofer.weread.mvp.presenter;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.loofer.framework.base.AppManager;
import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.RxUtils;
import org.loofer.framework.utils.UiUtils;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.mvp.contract.HomeContract;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.ui.activity.AudioDetailActivity;
import org.loofer.weread.mvp.ui.activity.TextDetailActivity;
import org.loofer.weread.mvp.ui.activity.VideoDetailActivity;
import org.loofer.weread.mvp.ui.adapter.HomePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * 通过Template生成对应页面的MVP和Dagger代码,请注意输入框中输入的名字必须相同
 * 由于每个项目包结构都不一定相同,所以每生成一个文件需要自己导入import包名,可以在设置中设置自动导入包名
 * 请在对应包下按以下顺序生成对应代码,Contract->Model->Presenter->Activity->Module->Component
 * 因为生成Activity时,Module和Component还没生成,但是Activity中有它们的引用,所以会报错,但是不用理会
 * 继续将Module和Component生成完后,编译一下项目再回到Activity,按提示修改一个方法名即可
 * 如果想生成Fragment的相关文件,则将上面构建顺序中的Activity换为Fragment,并将Component中inject方法的参数改为此Fragment
 */


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
@ActivityScope
public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private RxPermissions mRxPermissions;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;
    private List<HomeItem> mHomeItemList = new ArrayList<>();
    private int mPage = 1;
    private String pageId = "0";
    private String lastItemCreateTime = "0";
    private final HomePagerAdapter mHomePagerAdapter;

    @Inject
    public HomePresenter(HomeContract.Model model, HomeContract.View rootView
            , RxErrorHandler handler, Application application, RxPermissions rxPermissions
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mRxPermissions = rxPermissions;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;

        mHomePagerAdapter = new HomePagerAdapter(mHomeItemList);
        mHomePagerAdapter.setOnItemClickListener(mOnItemClickListener);
        mRootView.setAdapter(mHomePagerAdapter);//设置Adapter

    }


    public void getListByPage(final boolean pullToRefresh) {
        if (pullToRefresh) {
            mPage = 1;
            pageId = "0";
            lastItemCreateTime = "0";
        } else {
            pageId = mHomePagerAdapter.getLastItemId();
            lastItemCreateTime = mHomePagerAdapter.getLastItemCreateTime();
        }
        mModel.getListByPage(mPage, 0, pageId, DeviceUtils.getDeviceId(mApplication), lastItemCreateTime)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (mPage == 1)
                            mRootView.showLoading();//显示上拉刷新的进度条
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
                                   mHomePagerAdapter.notifyDataSetChanged();
//                            mRootView.updateListUI(baseJson.getData());
//                                   } else {
//                                       mRootView.showNoMore();
//                                   }
                               }
                           }

                );

    }

   HomePagerAdapter.OnItemClickListener mOnItemClickListener = new HomePagerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(HomeItem item, View itemView, int postion) {
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
        this.mRxPermissions = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

}