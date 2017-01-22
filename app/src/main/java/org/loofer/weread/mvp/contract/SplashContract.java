package org.loofer.weread.mvp.contract;

import org.loofer.framework.mvp.BaseView;
import org.loofer.framework.mvp.IModel;
import org.loofer.weread.mvp.model.entity.Splash;

import java.io.InputStream;

import rx.Observable;


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

public interface SplashContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends BaseView {
        void setImageDrawable(InputStream inputStream);
        void animWelcomeImage();
    }

    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {
        Observable<Splash> getSplash();
        void downloadSplash(String url);
    }
}