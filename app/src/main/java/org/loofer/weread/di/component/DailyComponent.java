package org.loofer.weread.di.component;


import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.weread.di.module.DailyModule;
import org.loofer.weread.mvp.ui.activity.DailyActivity;

import common.AppComponent;
import dagger.Component;


/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/13 12:03.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 * ==========================================================
 */
@ActivityScope
@Component(modules = DailyModule.class, dependencies = AppComponent.class)
public interface DailyComponent {
    void inject(DailyActivity activity);
}