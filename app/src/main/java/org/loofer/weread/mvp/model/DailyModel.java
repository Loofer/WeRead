package org.loofer.weread.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.mvp.BaseModel;
import org.loofer.weread.app.utils.TimeUtils;
import org.loofer.weread.mvp.contract.DailyContract;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;
import org.loofer.weread.mvp.model.entity.BaseJson;
import org.loofer.weread.mvp.model.entity.HomeItem;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/22 13:33.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 * ==========================================================
 */
public class DailyModel extends BaseModel<ServiceManager, CacheManager> implements DailyContract.Model {
    private Gson mGson;
    private Application mApplication;

    public DailyModel(ServiceManager serviceManager, CacheManager cacheManager, Gson gson, Application application) {
        super(serviceManager, cacheManager);
        this.mGson = gson;
        this.mApplication = application;
    }

    @Override
    public void onDestory() {
        super.onDestory();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<List<HomeItem>> getListByPage(int page, int model, String pageId, String deviceId, String createTime) {
        return mServiceManager.getCommonService().
                getList("api", "getList", page, model, pageId, createTime, "android", "1.3.0", TimeUtils.getCurrentSeconds(), deviceId, 1)
                .map(new Func1<BaseJson<List<HomeItem>>, List<HomeItem>>() {
                    @Override
                    public List<HomeItem> call(BaseJson<List<HomeItem>> listBaseJson) {
                        return listBaseJson.getData();
                    }
                });
    }
}