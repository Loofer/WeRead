package org.loofer.weread.widget.audioplayer;

/**
 * ============================================================
 * 版权： x x 版权所有（c）2017
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/9 10:44.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public interface IAudioPlayBack {

    boolean play();

    boolean play(String song);

    boolean pause();

    boolean isPlaying();

    int getProgress();

    int getDuration();

    boolean seekTo(int progress);

    void registerCallback(IAudioPlayBack.Callback callback);

    void unregisterCallback(IAudioPlayBack.Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onComplete(AudioPlayState state);

        void onPlayStatusChanged(AudioPlayState status);

        void onPosition(int position);
    }

}
