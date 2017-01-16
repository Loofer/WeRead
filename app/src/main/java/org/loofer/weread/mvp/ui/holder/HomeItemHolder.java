package org.loofer.weread.mvp.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.loofer.framework.base.BaseHolder;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.framework.widget.imageloader.glide.GlideImageConfig;
import org.loofer.weread.R;
import org.loofer.weread.mvp.model.entity.HomeItem;

import butterknife.BindView;
import common.WEApplication;
import rx.Observable;

public class HomeItemHolder extends BaseHolder<HomeItem> {

    @BindView(R.id.image_iv)
    ImageView imageIv;
    @BindView(R.id.comment_tv)
    TextView commentTv;
    @BindView(R.id.like_tv)
    TextView likeTv;
    @BindView(R.id.readcount_tv)
    TextView readcountTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.content_tv)
    TextView contentTv;
    @BindView(R.id.author_tv)
    TextView authorTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.image_type)
    ImageView imageType;
    @BindView(R.id.download_start_white)
    ImageView downloadStartWhite;
    @BindView(R.id.home_advertise_iv)
    ImageView homeAdvertiseIv;
    @BindView(R.id.pager_content)
    RelativeLayout pagerContent;

    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    private final WEApplication mApplication;

    public HomeItemHolder(View itemView) {
        super(itemView);
        //可以在任何可以拿到Application的地方,拿到AppComponent,从而得到用Dagger管理的单例对象
        mApplication = (WEApplication) itemView.getContext().getApplicationContext();
        mImageLoader = mApplication.getAppComponent().imageLoader();
    }

    @Override
    public void setData(HomeItem item) {
        final int model = Integer.valueOf(item.getModel());
        if (model == 5) {
            pagerContent.setVisibility(View.GONE);
            homeAdvertiseIv.setVisibility(View.VISIBLE);
            mImageLoader.loadImage(mApplication, GlideImageConfig
                    .builder()
                    .url(item.getThumbnail())
                    .imagerView(homeAdvertiseIv)
                    .build());
        } else {
            pagerContent.setVisibility(View.VISIBLE);
            homeAdvertiseIv.setVisibility(View.GONE);
            mImageLoader.loadImage(mApplication, GlideImageConfig
                    .builder()
                    .url(item.getThumbnail())
                    .imagerView(imageIv)
                    .build());
            Observable.just(item.getComment()).subscribe(RxTextView.text(commentTv));
            Observable.just(item.getGood()).subscribe(RxTextView.text(likeTv));
            Observable.just(item.getView()).subscribe(RxTextView.text(readcountTv));
            Observable.just(item.getTitle()).subscribe(RxTextView.text(titleTv));
            Observable.just(item.getExcerpt()).subscribe(RxTextView.text(contentTv));
            Observable.just(item.getAuthor()).subscribe(RxTextView.text(authorTv));
            Observable.just(item.getCategory()).subscribe(RxTextView.text(typeTv));
            switch (model) {
                case 2:
                    imageType.setVisibility(View.VISIBLE);
                    downloadStartWhite.setVisibility(View.GONE);
                    imageType.setImageResource(R.drawable.library_video_play_symbol);
                    break;
                case 3:
                    imageType.setVisibility(View.VISIBLE);
                    downloadStartWhite.setVisibility(View.VISIBLE);
                    imageType.setImageResource(R.drawable.library_voice_play_symbol);
                    break;
                default:
                    downloadStartWhite.setVisibility(View.GONE);
                    imageType.setVisibility(View.GONE);
            }
        }
    }
}
