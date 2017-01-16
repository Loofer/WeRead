package common;

import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.loofer.framework.base.BaseActivity;
import org.loofer.framework.mvp.BasePresenter;
import org.loofer.framework.utils.StatusBarUtil;
import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;

import java.lang.reflect.Method;



/**
 * Created by jess on 8/5/16 13:13
 * contact with jess.yan.effort@gmail.com
 */
public abstract class WEActivity<P extends BasePresenter> extends BaseActivity<P> {
    protected WEApplication mWeApplication;
    private Toolbar mToolbar;
    private FrameLayout mContent;
    private TextView mToolbarTitle;

    @Override
    protected void ComponentInject() {
        mWeApplication = (WEApplication) getApplication();
        setupActivityComponent(mWeApplication.getAppComponent());
    }


    @Override
    protected View initBaseLayout() {
        View baseView = View.inflate(this, R.layout.common_base, null);
        mContent = (FrameLayout) baseView.findViewById(R.id.fl_content);
        mToolbar = (Toolbar) baseView.findViewById(R.id.common_toolbar);
        mToolbarTitle = (TextView) baseView.findViewById(R.id.toolbar_title_tv);

        setInflateMenu();
        //设置相关默认操作
        setTitleNavigationIcon(R.drawable.abc_ic_ab_back_material);
        setTitleBgColor(R.color.colorPrimary);

        initToolBarlistener();

        mContent.addView(initLayout());
        return baseView;
    }

    /**
     * menu 后返回键监听
     */
    private void initToolBarlistener() {
        //左边Navigation Button监听回调
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackOnClickNavigationAction(v);
            }
        });
        //右边菜单item监听回调
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return callbackOnMenuAction(item);
            }
        });
    }


    /**
     * 为mToolbar设置menu项
     */
    private void setInflateMenu() {
        if (getMenuLayoutId() > 0) {
            mToolbar.inflateMenu(getMenuLayoutId());
            setMenuIconVisible(isMenuIconVisible());
        }
    }

    /**
     * 获取菜单资源id，默认无，子类可重写
     *
     * @return
     */
    protected int getMenuLayoutId() {
        return 0;
    }

    /**
     * 获取mToolbar
     *
     * @return
     */
    public Toolbar getToolbar() {
        checkToolbar(mToolbar);
        return mToolbar;
    }

    /**
     * 设置主标题
     *
     * @param object
     */
    public void setMainTitle(Object object) {
        checkToolbar(mToolbar);
        mToolbar.setTitle(UiUtils.getString(object));
    }

    /**
     * 设置子类标题
     *
     * @param object
     */
    public void setSubTitle(Object object) {
        checkToolbar(mToolbar);
        mToolbar.setSubtitle(UiUtils.getString(object));
    }

    /**
     * 设置主标题字体颜色
     *
     * @param object
     */
    public void setMainTitleColor(Object object) {
        checkToolbar(mToolbar);
        mToolbar.setTitleTextColor(UiUtils.getColor(object));
    }

    /**
     * 设置子标题字体颜色
     *
     * @param object
     */
    public void setSubTitleColor(Object object) {
        checkToolbar(mToolbar);
        mToolbar.setSubtitleTextColor(UiUtils.getColor(object));
    }

    /**
     * 设置logoIcon
     *
     * @param resId
     */

    public void setLogoIcon(int resId) {
        checkToolbar(mToolbar);
        mToolbar.setLogo(resId);
    }

    /**
     * 设置中间标题
     *
     * @param object
     */
    public void setToolbarTitle(Object object) {
        mToolbarTitle.setText(UiUtils.getString(object));
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param color
     */
    protected void setTitleBgColor(int color) {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(UiUtils.getColor(color));
        }
//        StatusBarUtil.setColor(this, color);
    }
    /**
     * 设置标题栏背景颜色
     *
     * @param color
     */
    protected void setTitleBgDrawable(int drawable) {
        if (mToolbar != null) {
            mToolbar.setBackgroundResource(drawable);
        }
//        StatusBarUtil.setColor(this, color);
    }

    @Override
    protected void setStatusBar() {
        //https://github.com/laobie/StatusBarUtil
        // http://www.jianshu.com/p/0acc12c29c1b
        StatusBarUtil.setColor(this, R.color.colorPrimary);
    }


    /**
     * 设置左边标题图标
     *
     * @param iconRes
     */
    public void setTitleNavigationIcon(int iconRes) {
        checkToolbar(mToolbar);
        mToolbar.setNavigationIcon(iconRes);
    }

    /**
     * 隐藏标题栏
     */
//    protected void hideToolbar() {
//        if (mToolbar.getVisibility() == View.VISIBLE)
//            mToolbar.setVisibility(View.GONE);
//    }

    /**
     * 不显示 NavigationButton
     */
    public void hideTitleNavigationButton() {
        checkToolbar(mToolbar);
        mToolbar.setNavigationIcon(null);
    }

    /**
     * Navigation Button点击回调，默认回退销毁页面，其他操作子类可重写
     *
     * @param view
     */
    protected void callbackOnClickNavigationAction(View view) {
        onBackPressed();
    }

    /**
     * menu点击回调，默认无，子类可重写
     *
     * @param item
     * @return
     */
    protected boolean callbackOnMenuAction(MenuItem item) {
        return false;
    }


    private void setMenuIconVisible(boolean visible) {
        if (!visible) return;
        checkToolbar(mToolbar);
        Menu menu = mToolbar.getMenu();
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean isMenuIconVisible() {
        return true;
    }

    private void checkToolbar(Toolbar toolbar) {
        if (toolbar == null) {
            throw new IllegalStateException("if you want use toolbar do not override useToolbar method! ");
        }
    }


    //提供AppComponent(提供所有的单例对象)给子类，进行Component依赖
    protected abstract void setupActivityComponent(AppComponent appComponent);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mWeApplication = null;
    }
}
