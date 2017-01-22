package org.loofer.weread.mvp.contract;


import org.loofer.framework.base.DefaultAdapter;
import org.loofer.framework.mvp.BaseView;
import org.loofer.framework.mvp.IModel;
import org.loofer.weread.mvp.model.entity.HomeItem;

import java.util.List;

import rx.Observable;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2016
 *
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/22 13:31.
 * 描述：
 *
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 *
 * ==========================================================
 */
public interface DailyContract {
    //对于经常使用的关于UI的方法可以定义到BaseView中,如显示隐藏进度条,和显示文字消息
    interface View extends BaseView {

        void setAdapter(DefaultAdapter adapter);

        void startLoadMore();

        void endLoadMore();

        void showNoMore();

        void showOnFailure();
    }

    //Model层定义接口,外部只需关心model返回的数据,无需关心内部细节,及是否使用缓存
    interface Model extends IModel {
        //        Observable<List<Gank>> getGanks(String categery, int pageSize, int page);
        Observable<List<HomeItem>> getListByPage(int page, int model, String pageId, String deviceId, String createTime);
    }
}