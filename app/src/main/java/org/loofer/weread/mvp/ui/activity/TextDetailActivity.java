package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.UiUtils;
import org.loofer.framework.widget.imageloader.glide.GlideImageConfig;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerDetailComponent;
import org.loofer.weread.di.module.DetailModule;
import org.loofer.weread.mvp.contract.DetailContract;
import org.loofer.weread.mvp.model.entity.Detail;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.presenter.DetailPresenter;
import org.loofer.weread.utils.AnalysisHTML;
import org.loofer.weread.utils.WebViewUtils;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;

import static org.loofer.framework.utils.Preconditions.checkNotNull;
import static org.loofer.framework.utils.UiUtils.getResources;


/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/7 22:23.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class TextDetailActivity extends WEActivity<DetailPresenter> implements DetailContract.View, ObservableScrollViewCallbacks {


    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.news_top_img_under_line)
    View mNewsTopImgUnderLine;
    @BindView(R.id.news_top_type)
    TextView mTvNewsTopType;
    @BindView(R.id.news_top_date)
    TextView mTvNewsTopDate;
    @BindView(R.id.news_top_title)
    TextView mTvNewsTopTitle;
    @BindView(R.id.news_top_author)
    TextView mTvNewsTopAuthor;
    @BindView(R.id.news_top_lead)
    TextView mTvNewsTopLead;
    @BindView(R.id.news_top_lead_line)
    View mNewsTopLeadLine;
    @BindView(R.id.news_top)
    LinearLayout mNewsTop;
    @BindView(R.id.news_parse_web)
    LinearLayout mNewsParseWeb;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.scrollView)
    ObservableScrollView mScrollView;
    @BindView(R.id.favorite)
    ImageView mFavorite;
    @BindView(R.id.write)
    ImageView mWrite;
    @BindView(R.id.share)
    ImageView mShare;
    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    private int mParallaxImageHeight;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerDetailComponent
                .builder()
                .appComponent(appComponent)
                .detailModule(new DetailModule(this)) //请将DetailModule()第一个首字母改为小写
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_art_detail, null, false);
    }

    @Override
    protected void initData() {
        initView();
        Bundle bundle = getIntent().getExtras();
        HomeItem item = bundle.getParcelable("item");
        if (item != null) {
            mWeApplication.getAppComponent()
                    .imageLoader()
                    .loadImage(mApplication, GlideImageConfig.builder().url(item.getThumbnail())
                            .imagerView(mImage)
                            .build());
//            int mode = Integer.valueOf(item.getModel());
            mNewsTopLeadLine.setVisibility(View.VISIBLE);
            mNewsTopImgUnderLine.setVisibility(View.VISIBLE);
            mTvNewsTopType.setText("文 字");
            mTvNewsTopDate.setText(item.getUpdate_time());
            mTvNewsTopTitle.setText(item.getTitle());
            mTvNewsTopAuthor.setText(item.getAuthor());
            mTvNewsTopLead.setText(item.getLead());
            mTvNewsTopLead.setLineSpacing(1.5f, 1.8f);
            mPresenter.getDetail(item.getId());
        }
    }

    private void initView() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
    }


    @Override
    protected boolean useToolbar() {
        return false;
    }

    @Override
    public void showOnFailure() {
        UiUtils.makeText("失败");
    }

    @Override
    public void updateListUI(Detail detailEntity) {
        if (detailEntity.getParseXML() == 1) {
            int i = detailEntity.getLead().trim().length();
            AnalysisHTML analysisHTML = new AnalysisHTML();
            analysisHTML.loadHtml(this, detailEntity.getContent(), analysisHTML.HTML_STRING, mNewsParseWeb, i);
            mTvNewsTopType.setText("文 字");
        } else {
            WebViewUtils.initWebViewSetting(mWebView);
            mNewsParseWeb.setVisibility(View.GONE);
            mImage.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mNewsTop.setVisibility(View.GONE);
            mWebView.loadUrl(addParams2WezeitUrl(detailEntity.getHtml5(), false));
        }
    }


    private String addParams2WezeitUrl(String url, boolean paramBoolean) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(url);
        localStringBuffer.append("?client=android");
        localStringBuffer.append("&device_id=" + DeviceUtils.getDeviceId(this));
        localStringBuffer.append("&version=" + "1.3.0");
        if (paramBoolean)
            localStringBuffer.append("&show_video=0");
        else {
            localStringBuffer.append("&show_video=1");
        }
        return localStringBuffer.toString();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        UiUtils.SnackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        UiUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) i / mParallaxImageHeight);
        mToolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
//        ViewHelper.setTranslationY(image, i / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}