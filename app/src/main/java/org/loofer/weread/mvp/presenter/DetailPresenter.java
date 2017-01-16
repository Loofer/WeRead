package org.loofer.weread.mvp.presenter;

import android.app.Application;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.loofer.framework.base.AppManager;
import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.SchedulerTransformer;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.mvp.contract.DetailContract;
import org.loofer.weread.mvp.model.entity.BaseJson;
import org.loofer.weread.mvp.model.entity.Detail;

import javax.inject.Inject;

import rx.Subscriber;


/**
 * 通过Template生成对应页面的MVP和Dagger代码,请注意输入框中输入的名字必须相同
 * 由于每个项目包结构都不一定相同,所以每生成一个文件需要自己导入import包名,可以在设置中设置自动导入包名
 * 请在对应包下按以下顺序生成对应代码,Contract->Model->Presenter->Activity->Module->Component
 * 因为生成Activity时,Module和Component还没生成,但是Activity中有它们的引用,所以会报错,但是不用理会
 * 继续将Module和Component生成完后,编译一下项目再回到Activity,按提示修改一个方法名即可
 * 如果想生成Fragment的相关文件,则将上面构建顺序中的Activity换为Fragment,并将Component中inject方法的参数改为此Fragment
 */


/**
 *============================================================
 * 版权： xx有限公司 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/7 22:23.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 *==========================================================
 */

@ActivityScope
public class DetailPresenter extends BasePresenter<DetailContract.Model, DetailContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private RxPermissions mRxPermissions;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public DetailPresenter(DetailContract.Model model, DetailContract.View rootView
            , RxErrorHandler handler, Application application, RxPermissions rxPermissions
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mRxPermissions = rxPermissions;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }


    public void getDetail(String itemId){
        mModel.getDetail("api", "getPost", itemId, 1)
                .compose(new SchedulerTransformer<BaseJson<Detail>>())
                .subscribe(new Subscriber<BaseJson<Detail>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRootView.showOnFailure();
                    }

                    @Override
                    public void onNext(BaseJson<Detail> detailDatas) {
                        mRootView.updateListUI(detailDatas.getData());
                    }
                });
    }

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