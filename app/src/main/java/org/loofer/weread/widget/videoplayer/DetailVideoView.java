package org.loofer.weread.widget.videoplayer;

import android.content.Context;
import android.util.AttributeSet;

import org.loofer.player.model.VideoModel;
import org.loofer.player.video.StandardVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/9 18:23.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class DetailVideoView extends StandardVideoPlayer {

//    private TextView mMoreScale;
//
//    private TextView mSwitchSize;

    private List<VideoModel> mUrlList = new ArrayList<>();

    //记住切换数据源类型
    private int mType = 0;

    //数据源
    private int mSourcePosition = 0;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public DetailVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public DetailVideoView(Context context) {
        super(context);
    }

    public DetailVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
//        initView();
    }

//    private void initView() {
//        mMoreScale = (TextView) findViewById(R.id.moreScale);
//        mSwitchSize = (TextView) findViewById(R.id.switchSize);
//
//        //切换清晰度
//        mMoreScale.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mType == 0) {
//                    mType = 1;
//                    mMoreScale.setText("16:9");
//                    VideoType.setShowType(VideoType.SCREEN_TYPE_16_9);
//                    if (mTextureView != null)
//                        mTextureView.requestLayout();
//                } else if (mType == 1) {
//                    mType = 2;
//                    mMoreScale.setText("4:3");
//                    VideoType.setShowType(VideoType.SCREEN_TYPE_4_3);
//                    if (mTextureView != null)
//                        mTextureView.requestLayout();
//                } else if (mType == 2) {
//                    mType = 0;
//                    mMoreScale.setText("默认比例");
//                    VideoType.setShowType(VideoType.SCREEN_TYPE_DEFAULT);
//                    if (mTextureView != null)
//                        mTextureView.requestLayout();
//                }
//            }
//        });
//
//        //切换视频清晰度
//        mSwitchSize.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSwitchDialog();
//            }
//        });
//
//    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param objects       object[0]目前为title
     * @return
     */
    public boolean setUp(List<VideoModel> url, boolean cacheWithPlay, Object... objects) {
        mUrlList = url;
        return setUp(url.get(0).getUrl(), cacheWithPlay, objects);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param objects       object[0]目前为title
     * @return
     */
    public boolean setUp(List<VideoModel> url, boolean cacheWithPlay, File cachePath, Object... objects) {
        mUrlList = url;
        return setUp(url.get(0).getUrl(), cacheWithPlay, cachePath, objects);
    }

//    @Override
//    public int getLayoutId() {
//        return R.layout.layout_detail_video;
//    }

    /**
     * 弹出切换清晰度
     */
//    private void showSwitchDialog() {
//        SwitchVideoTypeDialog switchVideoTypeDialog = new SwitchVideoTypeDialog(getContext());
//        switchVideoTypeDialog.initList(mUrlList, new SwitchVideoTypeDialog.OnListItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                final String name = mUrlList.get(position).getName();
//                if (mSourcePosition != position) {
//                    if ((mCurrentState == VideoPlayer.CURRENT_STATE_PLAYING
//                            || mCurrentState == VideoPlayer.CURRENT_STATE_PAUSE)
//                            && VideoManager.instance().getMediaPlayer() != null) {
//                        final String url = mUrlList.get(position).getUrl();
//                        onVideoPause();
//                        final long currentPosition = mCurrentPosition;
//                        VideoManager.instance().releaseMediaPlayer();
//                        cancelProgressTimer();
//                        hideAllWidget();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                setUp(url, mCache, mCachePath, mObjects);
//                                setSeekOnStart(currentPosition);
//                                startPlayLogic();
//                                cancelProgressTimer();
//                                hideAllWidget();
//                            }
//                        }, 500);
//                        mSwitchSize.setText(name);
//                        mSourcePosition = position;
//                    }
//                } else {
//                    Toast.makeText(getContext(), "已经是 " + name, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        switchVideoTypeDialog.show();
//    }
    
}
