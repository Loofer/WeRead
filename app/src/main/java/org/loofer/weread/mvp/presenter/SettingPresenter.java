package org.loofer.weread.mvp.presenter;

import android.app.Application;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.loofer.framework.base.AppManager;
import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.mvp.contract.SettingContract;

import javax.inject.Inject;





/**
 *============================================================
 * 版权： xx有限公司 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 22:12.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 *==========================================================
 */

@ActivityScope
public class SettingPresenter extends BasePresenter<SettingContract.Model, SettingContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private RxPermissions mRxPermissions;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public SettingPresenter(SettingContract.Model model, SettingContract.View rootView
            , RxErrorHandler handler, Application application, RxPermissions rxPermissions
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mRxPermissions = rxPermissions;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
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