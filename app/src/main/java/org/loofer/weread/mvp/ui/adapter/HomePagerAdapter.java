package org.loofer.weread.mvp.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.loofer.framework.utils.KnifeUtil;
import org.loofer.framework.widget.imageloader.ImageLoader;
import org.loofer.framework.widget.imageloader.glide.GlideImageConfig;
import org.loofer.weread.R;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.widget.PagerAdapter;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import common.WEApplication;
import rx.Observable;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/22 10:06.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class HomePagerAdapter extends PagerAdapter {

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
    @BindView(R.id.type_container)
    LinearLayout container;


    LinkedList<View> mViews;
    private List<HomeItem> mHomeItemList;
    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    private WEApplication mApplication;

    public HomePagerAdapter(List<HomeItem> list) {
        this.mHomeItemList = list;
        this.mViews = new LinkedList<>();

    }

    public int getCount() {
        return mHomeItemList == null ? 0 : mHomeItemList.size();
    }

    public List<HomeItem> getHomeItemList() {
        return mHomeItemList;
    }

    public HomeItem getItem(int i) {
        if (i < this.mHomeItemList.size()) {
            return this.mHomeItemList.get(i);
        }
        return null;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        if (position >= this.mHomeItemList.size()) {
            return super.instantiateItem(container, position);
        }
        View view;
        if (this.mViews.size() > 0) {
            view = this.mViews.removeFirst();
        } else {
            view = View.inflate(WEApplication.getContext().getApplicationContext(), R.layout.item_main_page, null);
        }
        mApplication = (WEApplication) view.getContext().getApplicationContext();
        mImageLoader = mApplication.getAppComponent().imageLoader();
        KnifeUtil.bindTarget(this, view);//绑定
        initView(this.mHomeItemList.get(position),position);
        container.addView(view);
        return view;
    }

    public void destroyItem(ViewGroup container, int i, Object object) {
        container.removeView((View) object);
        this.mViews.add((View) object);
    }

    private void initView(final HomeItem item, final int postion) {
        if (item == null)
            return;

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
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!= mOnItemClickListener){
                        mOnItemClickListener.onItemClick(item,container,postion);
                    }
                }
            });
        }
    }

    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(HomeItem item,View itemView,int postion);
    }


    public String getLastItemId(){
        if (mHomeItemList.size()==0){
            return "0";
        }
        HomeItem item =mHomeItemList.get(mHomeItemList.size()-1);
        return item.getId();
    }
    public String getLastItemCreateTime(){
        if (mHomeItemList.size()==0){
            return "0";
        }
        HomeItem item = mHomeItemList.get(mHomeItemList.size()-1);
        return item.getCreate_time();
    }


//    public int getItemPosition(Object obj) {
//        return -2;
//    }


}
