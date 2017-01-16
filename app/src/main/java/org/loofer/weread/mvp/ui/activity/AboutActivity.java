package org.loofer.weread.mvp.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.loofer.framework.utils.StatusBarUtil;
import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;
import org.loofer.weread.di.component.DaggerAboutComponent;
import org.loofer.weread.di.module.AboutModule;
import org.loofer.weread.mvp.contract.AboutContract;
import org.loofer.weread.mvp.presenter.AboutPresenter;

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
 * 创建日期 ：2016/12/28 21:48.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class AboutActivity extends WEActivity<AboutPresenter> implements AboutContract.View {


    @BindView(R.id.backdrop)
    ImageView mBackdrop;
    @BindView(R.id.common_toolbar)
    Toolbar mAboutToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerAboutComponent
                .builder()
                .appComponent(appComponent)
                .aboutModule(new AboutModule(this)) //请将AboutModule()第一个首字母改为小写
                .build()
                .inject(this);
    }

    @Override
    protected View initLayout() {
        return LayoutInflater.from(this).inflate(R.layout.activity_about, null, false);
    }


    public void initBack() {
        setSupportActionBar(mAboutToolbar);
        mAboutToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mAboutToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
    }

    @Override
    protected boolean useToolbar() {
        return false;
    }

    @Override
    protected void initData() {

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
        UiUtils.startActivity(this, intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

}