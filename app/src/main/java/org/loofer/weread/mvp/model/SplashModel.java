package org.loofer.weread.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BaseModel;
import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.L;
import org.loofer.weread.app.utils.FileUtil;
import org.loofer.weread.app.utils.TimeUtils;
import org.loofer.weread.mvp.contract.SplashContract;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;
import org.loofer.weread.mvp.model.entity.Splash;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Observable;


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
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/2 17:30.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

@ActivityScope
public class SplashModel extends BaseModel<ServiceManager, CacheManager> implements SplashContract.Model {
    private final OkHttpClient mClient;
    private Gson mGson;
    private Application mApplication;

    @Inject
    public SplashModel(ServiceManager serviceManager, CacheManager cacheManager, Gson gson, Application application, OkHttpClient client) {
        super(serviceManager, cacheManager);
        this.mGson = gson;
        this.mApplication = application;
        this.mClient = client;
    }

    @Override
    public Observable<Splash> getSplash() {
        String client = "android";
        String version = "1.3.0";
        Long time = TimeUtils.getCurrentSeconds();
        String deviceId = DeviceUtils.getDeviceId(mApplication);
        return mServiceManager.getCommonService().getSplash(client, version, time, deviceId);
    }

    /**
     * 下载图片
     */
    @Override
    public void downloadSplash(String url) {
//        LogUtils.i("开始下载图片...");
        L.i("开始下载图片...");
        final Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                L.e(e.getMessage());
//                LogUtils.d(e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                FileUtil.createSdDir();
                String url = response.request().url().toString();
                int index = url.lastIndexOf("/");
                String pictureName = url.substring(index+1);
                if(FileUtil.isFileExist(pictureName)){
                    return;
                }
                L.i("pictureName="+pictureName);
//                LogUtils.i("pictureName="+pictureName);
                FileOutputStream fos = new FileOutputStream(FileUtil.createFile(pictureName));
                InputStream in = response.body().byteStream();
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = in.read(buf))!=-1){
                    fos.write(buf,0,len);
                }
                fos.flush();
                in.close();
                fos.close();
            }
        });
    }

    @Override
    public void onDestory() {
        super.onDestory();
        this.mGson = null;
        this.mApplication = null;
    }

}