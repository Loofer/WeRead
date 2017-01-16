package org.loofer.weread.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.framework.mvp.BaseModel;
import org.loofer.weread.mvp.contract.SettingContract;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;

import javax.inject.Inject;



/**
 *============================================================
 * 版权： xx有限公司 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 22:08.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 *==========================================================
 */

@ActivityScope
public class SettingModel extends BaseModel<ServiceManager, CacheManager> implements SettingContract.Model {
    private Gson mGson;
    private Application mApplication;

    @Inject
    public SettingModel(ServiceManager serviceManager, CacheManager cacheManager, Gson gson, Application application) {
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

}