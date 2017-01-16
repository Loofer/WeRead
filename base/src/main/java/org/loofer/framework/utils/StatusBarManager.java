package org.loofer.framework.utils;

import android.app.Activity;
import android.os.Build;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.loofer.framework.widget.StatusBarView;

/**
 * ============================================================
 * 版权： xx xx 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2016/12/28 13:29.
 * 描述：状态栏管理类
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */
public class StatusBarManager {
    private Activity mActivity;
    private TintType mTintType;   //色彩类型
    private int mAlpha;  //透明度值
    private int mStatusBarColor;   //状态栏颜色

    public enum TintType {
        GRADIENT, PURECOLOR  //渐变，纯色   ps:纯色效果仅适用于android 5.0以上
    }

    /**
     * 构造函数
     *
     * @param activity
     * @param tintType
     * @param alpha
     * @param statusBarColor
     */
    private StatusBarManager(Activity activity, TintType tintType, int alpha, int statusBarColor) {
        this.mActivity = activity;
        this.mTintType = tintType;
        this.mAlpha = alpha;
        this.mStatusBarColor = UiUtils.getColor(statusBarColor);
        windowConfig();
    }

    /**
     * 窗口相关配置
     */
    private void windowConfig() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //透明状态栏
            if (TintType.PURECOLOR == mTintType) {
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                mActivity.getWindow().setStatusBarColor(UiUtils.calculateColorWithAlpha(mStatusBarColor, mAlpha));//设置状态栏颜色
            } else if (TintType.GRADIENT == mTintType) {
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                addStatusBarView(mActivity, UiUtils.calculateColorWithAlpha(mStatusBarColor, mAlpha));
            }
            //透明底部导航栏
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            addStatusBarView(mActivity, UiUtils.calculateColorWithAlpha(mStatusBarColor, mAlpha));
        }
    }


    /**
     * 生成一个和状态栏大小相同的矩形条
     *
     * @return 状态栏矩形条
     */
    private StatusBarView createStatusBarView(Activity activity, int color) {
        StatusBarView statusBarView = new StatusBarView(activity);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.getStatusBarHeight()));
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /**
     * 添加状态栏
     *
     * @param activity
     * @param color
     */
    public void addStatusBarView(Activity activity, int color) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        if (decorView.getChildAt(0) instanceof StatusBarView) {
            decorView.getChildAt(0).setBackgroundColor(color);
        } else {
            StatusBarView statusView = createStatusBarView(activity, color);
            decorView.addView(statusView);
        }
    }

    /**
     * builder配置类
     */
    public static class builder {
        private static final int DEFAULT_ALPHA = 60;  //默认透明度数值
        private Activity activity;
        private TintType tintType = TintType.PURECOLOR;  //默认纯色效果
        private int alpha = DEFAULT_ALPHA;
        private int statusBarColor;

        /**
         * 构造方法
         *
         * @param activity
         */
        public builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 设置色彩类型
         *
         * @param tintType
         * @return
         */
        public builder setTintType(TintType tintType) {
            this.tintType = tintType;
            return this;
        }

        /**
         * 设置透明度
         *
         * @param alpha
         * @return
         */
        public builder setAlpha(int alpha) {
            if (alpha >= 0 & alpha <= 255)
                this.alpha = alpha;
            return this;
        }

        /**
         * 设置状态栏背景颜色
         *
         * @param StatusBarColor
         * @return
         */
        public builder setStatusBarColor(int StatusBarColor) {
            this.statusBarColor = StatusBarColor;
            return this;
        }

        /**
         * 创建StatusBarManager对象并返回
         *
         * @return
         */
        public StatusBarManager create() {
            return new StatusBarManager(activity, tintType, alpha, statusBarColor);
        }
    }
}
