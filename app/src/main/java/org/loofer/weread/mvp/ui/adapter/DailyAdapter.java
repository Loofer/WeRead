package org.loofer.weread.mvp.ui.adapter;

import android.view.View;

import org.loofer.framework.base.BaseHolder;
import org.loofer.framework.base.DefaultAdapter;
import org.loofer.weread.R;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.ui.holder.DailyItemHolder;

import java.util.List;


public class DailyAdapter extends DefaultAdapter<HomeItem> {
    public DailyAdapter(List<HomeItem> infos) {
        super(infos);
    }


    @Override
    public BaseHolder<HomeItem> getHolder(View v) {
        return new DailyItemHolder(v);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_daily;
    }

    public String getLastItemId(){
        if (getInfos().size()==0){
            return "0";
        }
        HomeItem item = getInfos().get(getInfos().size()-1);
        return item.getId();
    }
    public String getLastItemCreateTime(){
        if (getInfos().size()==0){
            return "0";
        }
        HomeItem item = getInfos().get(getInfos().size()-1);
        return item.getCreate_time();
    }

}
