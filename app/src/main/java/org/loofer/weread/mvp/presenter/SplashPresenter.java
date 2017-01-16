package org.loofer.weread.mvp.presenter;

import android.app.Application;
import android.content.res.AssetManager;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.loofer.framework.base.AppManager;
import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.L;
import org.loofer.framework.utils.LogUtils;
import org.loofer.framework.utils.PermissionUtil;
import org.loofer.framework.utils.RxUtils;
import org.loofer.framework.utils.SPUtils;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.rxerrorhandler.core.RxErrorHandler;
import org.loofer.weread.app.utils.FileUtil;
import org.loofer.weread.mvp.contract.SplashContract;
import org.loofer.weread.mvp.model.entity.Splash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.loofer.framework.utils.DeviceUtils.NETTYPE_WIFI;


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
 * 版权： xx有限公司 版权所有（c）2016
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
    private RxPermissions mRxPermissions;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    private final int WRITE_EXTERNAL_STORAGE = 0;
    private final int READ_EXTERNAL_STORAGE = 1;
    private final int READ_PHONE_STATE = 2;

    private final int REQUEST_PERMISSION_SUCCESS = 3;
    private final int REQUEST_PERMISSION_FAILURE = 4;


    @Inject
    public SplashPresenter(SplashContract.Model model, SplashContract.View rootView
            , RxErrorHandler handler, Application application, RxPermissions rxPermissions
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mRxPermissions = rxPermissions;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }


    public void requestCodePermissions() {

        PermissionUtil.readPhonestate(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }
        }, mRxPermissions, mErrorHandler);
        PermissionUtil.externalReadStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
            }
        }, mRxPermissions, mRootView, mErrorHandler);
        PermissionUtil.externalWriteStorage(new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                //基本权限已授权
                delaySplash();
                getSplash();
            }
        }, mRxPermissions, mRootView, mErrorHandler);
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
        List<String> picList = FileUtil.getAllAD();
        if (picList.size() > 0) {
            Random random = new Random();
            int index = random.nextInt(picList.size());
            int imgIndex = (int) SPUtils.get(mApplication, "splash_img_index", 0);
//            LogUtils.i("当前的imgIndex=" + imgIndex);
            L.i("当前的imgIndex=" + imgIndex);
            if (index == imgIndex) {
                if (index >= picList.size()) {
                    index--;
                } else if (imgIndex == 0) {
                    if (index + 1 < picList.size()) {
                        index++;
                    }
                }
            }
            SPUtils.put(mApplication, "splash_img_index", index);
            LogUtils.i("当前的picList.size=" + picList.size() + ",index = " + index);
            L.i("当前的picList.size=" + picList.size() + ",index = " + index);
            File file = new File(picList.get(index));
            try {
                InputStream fis = new FileInputStream(file);
                mRootView.setImageDrawable(fis);
                mRootView.animWelcomeImage();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

            }
        } else {
            try {
                AssetManager assetManager = mApplication.getAssets();
                InputStream in = assetManager.open("welcome_default.jpg");
                mRootView.setImageDrawable(in);
                mRootView.animWelcomeImage();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private int requestPermissions(int permissionCode) {
        final int[] permissionGranted = {REQUEST_PERMISSION_FAILURE};
        switch (permissionCode) {
            case WRITE_EXTERNAL_STORAGE:

                return permissionGranted[0];
            case READ_EXTERNAL_STORAGE:
                PermissionUtil.externalReadStorage(new PermissionUtil.RequestPermission() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        permissionGranted[0] = REQUEST_PERMISSION_SUCCESS;
                    }
                }, mRxPermissions, mRootView, mErrorHandler);
                return permissionGranted[0];
            case READ_PHONE_STATE:
                PermissionUtil.readPhonestate(new PermissionUtil.RequestPermission() {
                    @Override
                    public void onRequestPermissionSuccess() {
                        permissionGranted[0] = REQUEST_PERMISSION_SUCCESS;
                    }
                }, mRxPermissions, mErrorHandler);
                return permissionGranted[0];
        }
        return permissionGranted[0];
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