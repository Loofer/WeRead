package org.loofer.weread.di.module;


import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.weread.mvp.contract.SettingContract;
import org.loofer.weread.mvp.model.SettingModel;

import dagger.Module;
import dagger.Provides;



/**
 *============================================================
 * 版权： xx有限公司 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 22:07.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 *==========================================================
 */

@Module
public class SettingModule {
    private SettingContract.View view;

    /**
     * 构建SettingModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     * @param view
     */
    public SettingModule(SettingContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    SettingContract.View provideSettingView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    SettingContract.Model provideSettingModel(SettingModel model) {
        return model;
    }
}