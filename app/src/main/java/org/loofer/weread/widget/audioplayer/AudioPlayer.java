package org.loofer.weread.widget.audioplayer;

import android.media.MediaPlayer;

import org.loofer.framework.utils.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/8 10:46.
 * 描述：音频播放器
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class AudioPlayer implements IAudioPlayBack,MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{

    private static volatile AudioPlayer sInstance;
    private MediaPlayer mPlayer;
    private List<Callback> mCallbacks = new ArrayList<>(2);
    private boolean isPaused;
    private String song;

    private AudioPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
    }

    public static AudioPlayer getInstance() {
        if (sInstance == null) {
            synchronized (AudioPlayer.class) {
                if (sInstance == null) {
                    sInstance = new AudioPlayer();
                }
            }
        }
        return sInstance;
    }
    @Override
    public boolean play() {
        if (isPaused){
            mPlayer.start();
            notifyPlayStatusChanged(AudioPlayState.PLAYING);
            return true;
        }
        return false;
    }

    @Override
    public boolean play(String song) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(song);
            mPlayer.prepare();
            this.song = song;
            notifyPlayStatusChanged(AudioPlayState.PLAYING);
            return true;
        } catch (IOException e) {
            notifyPlayStatusChanged(AudioPlayState.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(AudioPlayState.PAUSE);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public boolean seekTo(int progress) {
        try {
            mPlayer.seekTo(progress);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void releasePlayer() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        sInstance = null;
        song = null;
    }

    private void notifyPlayStatusChanged(AudioPlayState status) {
        for (Callback callback : mCallbacks) {
            callback.onPlayStatusChanged(status);
        }
    }
    private void notifyComplete(AudioPlayState state) {
        for (Callback callback : mCallbacks) {
            callback.onComplete(state);
        }
    }

    public String getSong() {
        return song;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        LogUtils.d("onBufferingUpdate");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        LogUtils.d("onPrepared");
        L.d("onPrepared");
        mPlayer.start();
        notifyPlayStatusChanged(AudioPlayState.PLAYING);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
//        LogUtils.d("onCompletion");
        L.d("onCompletion");
        mPlayer.reset();
        notifyComplete(AudioPlayState.COMPLETE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notifyPlayStatusChanged(AudioPlayState.ERROR);
        return false;
    }
    
}
