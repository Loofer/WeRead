package org.loofer.weread.mvp.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.loofer.framework.base.BaseHolder;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.framework.widget.imageloader.glide.GlideImageConfig;
import org.loofer.weread.R;
import org.loofer.weread.app.utils.TimeUtils;
import org.loofer.weread.mvp.model.entity.HomeItem;

import butterknife.BindView;
import common.WEApplication;
import rx.Observable;

public class DailyItemHolder extends BaseHolder<HomeItem> {

    @BindView(R.id.month_tv)
    TextView monthTv;
    @BindView(R.id.year_tv)
    TextView yearTv;
    @BindView(R.id.calendar_iv)
    ImageView calendarIv;

    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    private final WEApplication mApplication;

    public DailyItemHolder(View itemView) {
        super(itemView);
        //可以在任何可以拿到Application的地方,拿到AppComponent,从而得到用Dagger管理的单例对象
        mApplication = (WEApplication) itemView.getContext().getApplicationContext();
        mImageLoader = mApplication.getAppComponent().imageLoader();
    }

    @Override
    public void setData(HomeItem item) {
        mImageLoader.loadImage(mApplication, GlideImageConfig
                .builder()
                .url(item.getThumbnail())
                .imagerView(calendarIv)
                .build());
        String[] arrayOfString = TimeUtils.getCalendarShowTime(item.getUpdate_time());
        if ((arrayOfString != null) && (arrayOfString.length == 3)) {
            Observable.just(arrayOfString[1] + " , " + arrayOfString[2]).subscribe(RxTextView.text(monthTv));
            Observable.just(arrayOfString[0]).subscribe(RxTextView.text(yearTv));
        }
    }
}
