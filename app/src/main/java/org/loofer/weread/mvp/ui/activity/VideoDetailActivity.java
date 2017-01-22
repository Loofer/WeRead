package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.UiUtils;
import org.loofer.player.PreViewManager;
import org.loofer.player.VideoManager;
import org.loofer.player.VideoPlayer;
import org.loofer.player.listener.LockClickListener;
import org.loofer.player.utils.OrientationUtils;
import org.loofer.player.video.StandardVideoPlayer;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerDetailComponent;
import org.loofer.weread.di.module.DetailModule;
import org.loofer.weread.mvp.contract.DetailContract;
import org.loofer.weread.mvp.model.entity.Detail;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.presenter.DetailPresenter;
import org.loofer.weread.utils.AnalysisHTML;
import org.loofer.weread.utils.WebViewUtils;
import org.loofer.weread.widget.videoplayer.DetailVideoView;
import org.loofer.weread.widget.videoplayer.VideoViewClickListener;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;
import tv.danmaku.ijk.media.player.AbstractMediaPlayer;

import static org.loofer.framework.utils.Preconditions.checkNotNull;
import static org.loofer.weread.R.id.toolBar;
import static org.loofer.weread.R.id.webView;

/**
 * 通过Template生成对应页面的MVP和Dagger代码,请注意输入框中输入的名字必须相同
 * 由于每个项目包结构都不一定相同,所以每生成一个文件需要自己导入import包名,可以在设置中设置自动导入包名
 * 请在对应包下按以下顺序生成对应代码,Contract->Model->Presenter->Activity->Module->Component
 * 因为生成Activity时,Module和Component还没生成,但是Activity中有它们的引用,所以会报错,但是不用理会
 * 继续将Module和Component生成完后,编译一下项目再回到Activity,按提示修改一个方法名即可
 * 如果想生成Fragment的相关文件,则将上面构建顺序中的Activity换为Fragment,并将Component中inject方法的参数改为此Fragment
 */

/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/9 11:13.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class VideoDetailActivity extends WEActivity<DetailPresenter> implements DetailContract.View {


    @BindView(R.id.favorite)
    ImageView mFavorite;
    @BindView(R.id.write)
    ImageView mWrite;
    @BindView(R.id.share)
    ImageView mShare;
    @BindView(toolBar)
    Toolbar mToolBar;
    @BindView(R.id.video)
    DetailVideoView mVideoView;
    @BindView(R.id.news_top_img_under_line)
    View mNewsTopImgUnderLine;
    @BindView(R.id.news_top_type)
    TextView mNewsTopType;
    @BindView(R.id.news_top_date)
    TextView mNewsTopDate;
    @BindView(R.id.news_top_title)
    TextView mNewsTopTitle;
    @BindView(R.id.news_top_author)
    TextView mNewsTopAuthor;
    @BindView(R.id.news_top_lead)
    TextView mNewsTopLead;
    @BindView(R.id.news_top_lead_line)
    View mNewsTopLeadLine;
    @BindView(R.id.news_top)
    LinearLayout mNewsTop;
    @BindView(R.id.news_parse_web)
    LinearLayout mNewsParseWeb;
    @BindView(webView)
    WebView mWebView;
    @BindView(R.id.scrollView)
    ObservableScrollView mScrollView;

    private boolean isPlay;
    private boolean isPause;

    private OrientationUtils orientationUtils;


    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerDetailComponent
                .builder()
                .appComponent(appComponent)
                .detailModule(new DetailModule(this)) //请将VideoDetailModule()第一个首字母改为小写
                .build()
                .inject(this);
    }


    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_video_detail, null, false);
    }


    @Override
    protected void initData() {
        initView();
        Bundle bundle = getIntent().getExtras();
        HomeItem item = bundle.getParcelable("item");
        if (item != null) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mVideoView.setThumbImageView(imageView);
            Glide.with(this).load(item.getThumbnail()).centerCrop().into(imageView);
            mVideoView.setUp(item.getVideo(), true, null, item.getTitle());

            //外部辅助的旋转，帮助全屏
            orientationUtils = new OrientationUtils(this, mVideoView);
            //初始化不打开外部的旋转
            orientationUtils.setEnable(false);

            mVideoView.setIsTouchWiget(true);
            //关闭自动旋转
            mVideoView.setRotateViewAuto(false);
            mVideoView.setLockLand(false);
            mVideoView.setShowFullAnimation(false);
            mVideoView.setNeedLockFull(true);

            mVideoView.getFullscreenButton().setOnClickListener(mOnVideoViewBtnListener);
            mVideoView.setStandardVideoAllCallBack(mVideoViewClickListener);
            mVideoView.setLockClickListener(mLockClickListener);

            mNewsTopLeadLine.setVisibility(View.VISIBLE);
            mNewsTopImgUnderLine.setVisibility(View.VISIBLE);
            mNewsTopType.setText(item.getTitle());
            mNewsTopDate.setText(item.getUpdate_time());
            mNewsTopTitle.setText(item.getTitle());
            mNewsTopAuthor.setText(item.getAuthor());
            mNewsTopLead.setText(item.getLead());
            mNewsTopLead.setLineSpacing(1.5f, 1.8f);
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
            mNewsTopLeadLine.setVisibility(View.VISIBLE);
            mNewsTopImgUnderLine.setVisibility(View.VISIBLE);
            int i = detailEntity.getLead().trim().length();
            AnalysisHTML analysisHTML = new AnalysisHTML();
            analysisHTML.loadHtml(this, detailEntity.getContent(), analysisHTML.HTML_STRING, mNewsParseWeb, i);
        } else {
            WebViewUtils.initWebViewSetting(mWebView);
            mNewsParseWeb.setVisibility(View.GONE);
//            video.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mNewsTop.setVisibility(View.GONE);
            mWebView.loadUrl(addParams2WezeitUrl(detailEntity.getHtml5(), false));
        }
    }

    public String addParams2WezeitUrl(String url, boolean paramBoolean) {
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

    LockClickListener mLockClickListener = new LockClickListener() {
        @Override
        public void onClick(View view, boolean lock) {
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils.setEnable(!lock);
            }
        }
    };

    VideoViewClickListener mVideoViewClickListener = new VideoViewClickListener() {

        @Override
        public void onPrepared(String url, Object... objects) {
            super.onPrepared(url, objects);
            //开始播放了才能旋转和全屏
            orientationUtils.setEnable(true);
            isPlay = true;
        }

        @Override
        public void onQuitFullscreen(String url, Object... objects) {
            super.onQuitFullscreen(url, objects);
            if (orientationUtils != null) {
                orientationUtils.backToProtVideo();
            }
        }
    };

    View.OnClickListener mOnVideoViewBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //直接横屏
            orientationUtils.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            mVideoView.startWindowFullscreen(VideoDetailActivity.this, true, true);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPause) {
            AbstractMediaPlayer mediaPlayer = VideoManager.instance().getMediaPlayer();
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            AbstractMediaPlayer mediaPlayer = VideoManager.instance().getMediaPlayer();
            if (null != mediaPlayer && !mediaPlayer.isPlayable()) {
                mediaPlayer.start();
            }
        }
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayer.releaseAllVideos();
        PreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!mVideoView.isIfCurrentIsFullscreen()) {
                    mVideoView.startWindowFullscreen(VideoDetailActivity.this, true, true);
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (mVideoView.isIfCurrentIsFullscreen()) {
                    StandardVideoPlayer.backFromWindowFull(this);
                }
                if (orientationUtils != null) {
                    orientationUtils.setEnable(true);
                }
            }
        }
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
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (StandardVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }
}