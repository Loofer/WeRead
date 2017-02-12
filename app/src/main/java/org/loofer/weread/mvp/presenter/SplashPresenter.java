package org.loofer.weread.mvp.presenter;

import android.app.Application;
import android.content.res.AssetManager;

import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.L;
import org.loofer.framework.utils.PermissionUtil;
import org.loofer.framework.utils.RxUtils;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.mvp.contract.SplashContract;
import org.loofer.weread.mvp.model.entity.Splash;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.loofer.framework.utils.DeviceUtils.NETTYPE_WIFI;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/2 17:31.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

@ActivityScope
public class SplashPresenter extends BasePresenter<SplashContract.Model, SplashContract.View> {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;


    @Inject
    public SplashPresenter(SplashContract.Model model, SplashContract.View rootView,
                           RxErrorHandler handler, Application application) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
    }


    public void requestCodePermissions() {

        PermissionUtil.readPhonestate(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }
        }, mRootView.getRxPermissions(), mErrorHandler);
        PermissionUtil.externalReadStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }
        }, mRootView.getRxPermissions(), mRootView, mErrorHandler);
        PermissionUtil.externalWriteStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //基本权限已授权
                delaySplash();
//                getSplash();
            }
        }, mRootView.getRxPermissions(), mRootView, mErrorHandler);
    }

    private void getSplash() {
        mModel.getSplash()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .compose(RxUtils.<Splash>bindToLifecycle(mRootView))//使用RXlifecycle,使subscription和activity一起销毁
                .subscribe(new Subscriber<Splash>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        LogUtils.e(e.getMessage());
                        L.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Splash splash) {
                        if (DeviceUtils.getNetworkType(mApplication) == NETTYPE_WIFI) {
                            if (splash != null) {
                                List<String> imgs = splash.images;
                                for (String url : imgs) {
                                    mModel.downloadSplash(url);
                                }
                            }
                        } else {
                            L.i("不是WIFI环境,就不去下载图片了");
//                            LogUtils.i("不是WIFI环境,就不去下载图片了");
                        }
                    }
                });
    }


    private void delaySplash() {
//        List<String> picList = FileUtil.getAllAD();
//        if (picList.size() > 0) {
//            Random random = new Random();
//            int index = random.nextInt(picList.size());
//            int imgIndex = (int) SPUtils.get(mApplication, "splash_img_index", 0);
////            LogUtils.i("当前的imgIndex=" + imgIndex);
//            L.i("当前的imgIndex=" + imgIndex);
//            if (index == imgIndex) {
//                if (index >= picList.size()) {
//                    index--;
//                } else if (imgIndex == 0) {
//                    if (index + 1 < picList.size()) {
//                        index++;
//                    }
//                }
//            }
//            SPUtils.put(mApplication, "splash_img_index", index);
//            LogUtils.i("当前的picList.size=" + picList.size() + ",index = " + index);
//            L.i("当前的picList.size=" + picList.size() + ",index = " + index);
//            File file = new File(picList.get(index));
//            try {
//                InputStream fis = new FileInputStream(file);
//                mRootView.setImageDrawable(fis);
//                mRootView.animWelcomeImage();
//                fis.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//
//            }
//        } else {
        try {
            AssetManager assetManager = mApplication.getAssets();
            InputStream in = assetManager.open("welcome_default.jpg");
            mRootView.setImageDrawable(in);
            mRootView.animWelcomeImage();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mApplication = null;
    }

}