package org.loofer.weread.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.mvp.BaseModel;
import org.loofer.weread.app.utils.TimeUtils;
import org.loofer.weread.mvp.contract.HomeContract;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;
import org.loofer.weread.mvp.model.entity.BaseJson;
import org.loofer.weread.mvp.model.entity.HomeItem;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;


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
 * 创建日期 ：2016/12/28 16:58.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class HomeModel extends BaseModel<ServiceManager, CacheManager> implements HomeContract.Model {
    private Gson mGson;
    private Application mApplication;

    public HomeModel(ServiceManager serviceManager, CacheManager cacheManager, Gson gson, Application application) {
        super(serviceManager, cacheManager);
        this.mGson = gson;
        this.mApplication = application;
    }


    @Override
    public Observable<List<HomeItem>> getListByPage(int page, int model, String pageId, String deviceId, String createTime) {
//        Observable<BaseJson<List<HomeItem>>> items = mServiceManager.getCommonService().
//                getList("api", "getList", page, model, pageId, createTime, "android", "1.3.0", TimeUtils.getCurrentSeconds(), deviceId, 1);
//
//        return mCacheManager.getCommonCache()
//                .getList(items, new DynamicKeyGroup(model + page + pageId, model + page + pageId))
//                .map(new Func1<Reply<BaseJson<List<HomeItem>>>, List<HomeItem>>() {
//                    @Override
//                    public List<HomeItem> call(Reply<BaseJson<List<HomeItem>>> baseJsonReply) {
//                        return baseJsonReply.getData().getData();
//                    }
//                });
        return mServiceManager.getCommonService().
                getList("api", "getList", page, model, pageId, createTime, "android", "1.3.0", TimeUtils.getCurrentSeconds(), deviceId, 1)
                .map(new Func1<BaseJson<List<HomeItem>>, List<HomeItem>>() {
                    @Override
                    public List<HomeItem> call(BaseJson<List<HomeItem>> listBaseJson) {
                        return listBaseJson.getData();
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