package org.loofer.weread.mvp.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.loofer.framework.utils.StatusBarUtil;
import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerSplashComponent;
import org.loofer.weread.di.module.SplashModule;
import org.loofer.weread.mvp.contract.SplashContract;
import org.loofer.weread.mvp.presenter.SplashPresenter;
import org.loofer.weread.widget.FixedImageView;

import java.io.InputStream;

import butterknife.BindView;
import common.AppComponent;
import common.WEActivity;

import static org.loofer.framework.utils.Preconditions.checkNotNull;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/2 17:31.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class SplashActivity extends WEActivity<SplashPresenter> implements SplashContract.View {


    @BindView(R.id.splash_img)
    FixedImageView mSplashImg;
    @BindView(R.id.weread_logo_iv)
    ImageView mWereadLogoIv;
    @BindView(R.id.publish_logo_iv)
    ImageView mPublishLogoIv;
    private RxPermissions mRxPermissions;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        mRxPermissions = new RxPermissions(this);
        DaggerSplashComponent
                .builder()
                .appComponent(appComponent)
                .splashModule(new SplashModule(this)) //请将SplashModule()第一个首字母改为小写
                .build()
                .inject(this);
    }


    @Override
    protected void setStatusBar() {
        FullScreencall();
        StatusBarUtil.setTranslucentForImageView(this, mSplashImg);
    }

    @Override
    protected boolean useToolbar() {
        return false;
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_splash, null, false);
    }


    @Override
    public void setImageDrawable(InputStream inputStream) {
        mSplashImg.setImageDrawable(InputStream2Drawable(inputStream));
    }


    @Override
    public void animWelcomeImage() {
        mSplashImg
                .animate()
                .scaleX(1.2F)
                .scaleY(1.2F)
                .setDuration(3000L)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    public Drawable InputStream2Drawable(InputStream is) {
        Drawable drawable = BitmapDrawable.createFromStream(is, "splashImg");
        return drawable;
    }

    @Override
    protected void initData() {
        mPresenter.requestCodePermissions();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
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
    protected void onDestroy() {
        super.onDestroy();
        this.mRxPermissions = null;
    }

}