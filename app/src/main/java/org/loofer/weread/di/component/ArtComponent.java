package org.loofer.weread.di.component;

import org.loofer.framework.di.scope.ActivityScope;
import org.loofer.weread.di.module.ArtModule;
import org.loofer.weread.mvp.ui.activity.ArtActivity;

import common.AppComponent;
import dagger.Component;


/**
 *============================================================
 * 版权： xx有限公司 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/11 21:36.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 *==========================================================
 */

@ActivityScope
@Component(modules = ArtModule.class, dependencies = AppComponent.class)
public interface ArtComponent {
    void inject(ArtActivity activity);
}