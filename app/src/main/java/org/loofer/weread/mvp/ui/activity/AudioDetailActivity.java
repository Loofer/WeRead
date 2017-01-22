package org.loofer.weread.mvp.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import org.loofer.framework.utils.DeviceUtils;
import org.loofer.framework.utils.L;
import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;
import org.loofer.weread.app.utils.TimeUtils;
import org.loofer.weread.di.component.DaggerDetailComponent;
import org.loofer.weread.di.module.DetailModule;
import org.loofer.weread.mvp.contract.DetailContract;
import org.loofer.weread.mvp.model.entity.Detail;
import org.loofer.weread.mvp.model.entity.HomeItem;
import org.loofer.weread.mvp.presenter.DetailPresenter;
import org.loofer.weread.utils.AnalysisHTML;
import org.loofer.weread.utils.WebViewUtils;
import org.loofer.weread.widget.audioplayer.AudioPlayBackService;
import org.loofer.weread.widget.audioplayer.AudioPlayState;
import org.loofer.weread.widget.audioplayer.IAudioPlayBack;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import common.AppComponent;
import common.WEActivity;

import static org.loofer.framework.utils.Preconditions.checkNotNull;


/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/8 10:15.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class AudioDetailActivity extends WEActivity<DetailPresenter> implements DetailContract.View, ObservableScrollViewCallbacks,IAudioPlayBack.Callback {


    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.button_play_last)
    AppCompatImageView mButtonPlayLast;
    @BindView(R.id.button_play_toggle)
    AppCompatImageView mButtonPlayToggle;
    @BindView(R.id.button_play_next)
    AppCompatImageView mButtonPlayNext;
    @BindView(R.id.layout_play_controls)
    LinearLayout mLayoutPlayControls;
    @BindView(R.id.text_view_progress)
    TextView mTextViewProgress;
    @BindView(R.id.seek_bar)
    AppCompatSeekBar mSeekBar;
    @BindView(R.id.text_view_duration)
    TextView mTextViewDuration;
    @BindView(R.id.layout_progress)
    LinearLayout mLayoutProgress;
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

    private Handler mHandleProgress = new Handler();


    private AudioPlayBackService mPlaybackService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackService = ((AudioPlayBackService.LocalBinder) service).getService();
            register();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unRegister();
            mPlaybackService = null;
        }
    };
    private String mSong;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        mHandleProgress.removeCallbacks(runnable);
        mHandleProgress = null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerDetailComponent
                .builder()
                .appComponent(appComponent)
                .detailModule(new DetailModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_audio, null, false);
    }


    @Override
    protected boolean useToolbar() {
        return false;
    }

    @Override
    protected void initData() {
        initView();
        Bundle bundle = getIntent().getExtras();
        HomeItem item = bundle.getParcelable("item");
        if (item != null) {
            Glide.with(this).load(item.getThumbnail()).centerCrop().into(mImage);
            mNewsTopLeadLine.setVisibility(View.VISIBLE);
            mNewsTopImgUnderLine.setVisibility(View.VISIBLE);
            mNewsTopType.setText("音 频");
            mNewsTopDate.setText(item.getUpdate_time());
            mNewsTopTitle.setText(item.getTitle());
            mNewsTopAuthor.setText(item.getAuthor());
            mNewsTopLead.setText(item.getLead());
            mNewsTopLead.setLineSpacing(1.5f, 1.8f);
            mPresenter.getDetail(item.getId());
        }
        bindPlaybackService();
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
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }


    @Override
    public void updateListUI(Detail detailEntity) {
        mSong = detailEntity.getFm();
        if (detailEntity.getParseXML() == 1) {
            int i = detailEntity.getLead().trim().length();
            AnalysisHTML analysisHTML = new AnalysisHTML();
            analysisHTML.loadHtml(this, detailEntity.getContent(), analysisHTML.HTML_STRING, mNewsParseWeb, i);
            mNewsTopType.setText("音 频");
        } else {
            WebViewUtils.initWebViewSetting(mWebView);
            mNewsParseWeb.setVisibility(View.GONE);
            mImage.setVisibility(View.GONE);
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

    @OnClick(R.id.button_play_toggle)
    public void onClick() {
        if (mPlaybackService == null || mSong == null) {
//            LogUtils.d("mPlaybackService == null");
            L.d("mPlaybackService == null");
            return;
        }
        if (mPlaybackService.isPlaying()) {
            if (mSong.equals(mPlaybackService.getSong())) {
                mPlaybackService.pause();
                mButtonPlayToggle.setImageResource(R.drawable.ic_play);
            } else {
                mPlaybackService.play(mSong);
                mButtonPlayToggle.setImageResource(R.drawable.ic_pause);
            }
        } else {
            if (mSong.equals(mPlaybackService.getSong())) {
                mPlaybackService.play();
            } else {
                mPlaybackService.play(mSong);
            }
            mButtonPlayToggle.setImageResource(R.drawable.ic_pause);
        }
    }


    private int getSeekDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / mSeekBar.getMax()));
    }

    private int getCurrentSongDuration() {
        int duration = 0;
        if (mPlaybackService != null) {
            duration = mPlaybackService.getDuration();
        }
        return duration;
    }

    public void bindPlaybackService() {
        this.bindService(new Intent(this, AudioPlayBackService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void register() {
        mPlaybackService.registerCallback(this);
    }

    private void unRegister() {
        if (mPlaybackService != null) {
            mPlaybackService.unregisterCallback(this);
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showOnFailure() {
        UiUtils.makeText("失败");
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


    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPlaybackService.seekTo(getSeekDuration(seekBar.getProgress()));
            playTimer();
        }
    };

    //play 回调
//    IAudioPlayBack.Callback mPlayCallback = new IAudioPlayBack.Callback() {
        @Override
        public void onComplete(AudioPlayState state) {
            cancelTimer();
        }

        @Override
        public void onPlayStatusChanged(AudioPlayState status) {
//            LogUtils.d("onPlayStatusChanged.......status=" + status);
            L.d("onPlayStatusChanged.......status=" + status);
            switch (status) {
                case INIT:
                    break;
                case PREPARE:
                    break;
                case PLAYING:
                    updateDuration();
                    playTimer();
                    mButtonPlayToggle.setImageResource(R.drawable.ic_pause);
//                    LogUtils.d(mPlaybackService.getDuration() + "");
                    L.d(mPlaybackService.getDuration() + "");
                    break;
                case PAUSE:
                    cancelTimer();
                    mButtonPlayToggle.setImageResource(R.drawable.ic_play);
                    break;
                case ERROR:
                    break;
                case COMPLETE:
                    cancelTimer();
                    mButtonPlayToggle.setImageResource(R.drawable.ic_play);
                    mSeekBar.setProgress(0);
                    break;
            }
        }

        @Override
        public void onPosition(int position) {
//            LogUtils.d("onPosition.......=" + position);
            L.d("onPosition.......=" + position);
        }
//    };

    private void updateProgressTextWithProgress(int progress) {
        mTextViewProgress.setText(TimeUtils.formatDuration(progress));
    }

    private void updateDuration() {
        mTextViewDuration.setText(TimeUtils.formatDuration(mPlaybackService.getDuration()));
    }

    Timer timer = null;

    private void playTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mPlaybackService == null)
                    return;
                if (mPlaybackService.isPlaying()) {
                    mHandleProgress.post(runnable);
                }
            }
        }, 0, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mPlaybackService.isPlaying()) {
                if (isFinishing()) {
                    return;
                }
                int progress = (int) (mSeekBar.getMax()
                        * ((float) mPlaybackService.getProgress() / (float) mPlaybackService.getDuration()));
                updateProgressTextWithProgress(mPlaybackService.getProgress());
                if (progress >= 0 && progress <= mSeekBar.getMax()) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        mSeekBar.setProgress(progress, true);
//                    } else {
//                        mSeekBar.setProgress(progress);
//                    }
                }
            }
        }
    };

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) i / mParallaxImageHeight);
        mToolBar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

}