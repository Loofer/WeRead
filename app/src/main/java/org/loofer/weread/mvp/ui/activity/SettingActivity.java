package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.BuildConfig;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerSettingComponent;
import org.loofer.weread.di.module.SettingModule;
import org.loofer.weread.mvp.contract.SettingContract;
import org.loofer.weread.mvp.presenter.SettingPresenter;

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
 * 创建日期 ：2017/1/13 20:51.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class SettingActivity extends WEActivity<SettingPresenter> implements SettingContract.View {


    @BindView(R.id.push_toggle)
    Switch mPushToggle;
    @BindView(R.id.cacheSize)
    TextView mCacheSize;
    @BindView(R.id.cacheLayout)
    RelativeLayout mCacheLayout;
    @BindView(R.id.rl_about)
    RelativeLayout mAbout;
    @BindView(R.id.feedback)
    RelativeLayout mFeedback;
    @BindView(R.id.version_tv)
    TextView mVersionTv;
    @BindView(R.id.checkUpgrade)
    RelativeLayout mCheckUpgrade;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerSettingComponent
                .builder()
                .appComponent(appComponent)
                .settingModule(new SettingModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_setting, null, false);
    }

    @Override
    protected void initData() {
        setToolbarTitle("设 置");
        mVersionTv.setText(BuildConfig.VERSION_NAME);
    }


    @OnClick(R.id.rl_about)
    public void initClick(View view) {
        UiUtils.startActivity(AboutActivity.class);
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


}