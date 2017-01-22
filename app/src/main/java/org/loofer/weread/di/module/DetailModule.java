package org.loofer.weread.di.module;


import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.weread.mvp.contract.DetailContract;
import org.loofer.weread.mvp.model.DetailModel;

import dagger.Module;
import dagger.Provides;


/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/7 22:24.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

@Module
public class DetailModule {
    private DetailContract.View view;

    /**
     * 构建DetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public DetailModule(DetailContract.View view) {
        this.view = view;
    }


    @ActivityScope
    @Provides
    DetailContract.View provideDetailView() {
        return this.view;
    }


    @ActivityScope
    @Provides
    DetailContract.Model provideDetailModel(DetailModel model) {
        return model;
    }
}