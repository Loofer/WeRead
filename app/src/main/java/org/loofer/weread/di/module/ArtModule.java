package org.loofer.weread.di.module;

import android.app.Application;

import com.google.gson.Gson;

import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.weread.mvp.contract.ArtContract;
import org.loofer.weread.mvp.model.ArtModel;
import org.loofer.weread.mvp.model.api.cache.CacheManager;
import org.loofer.weread.mvp.model.api.service.ServiceManager;

import dagger.Module;
import dagger.Provides;


/**
 * ============================================================
 * 版权： x x 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/22 13:28.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 * ==========================================================
 */
@Module
public class ArtModule {
    private ArtContract.View view;

    /**
     * 构建ArtModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public ArtModule(ArtContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ArtContract.View provideArtView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ArtContract.Model provideArtModel(ServiceManager serviceManager, CacheManager cacheManager
            , Gson gson, Application application) {
        return new ArtModel(serviceManager, cacheManager, gson, application);
    }
}