package org.loofer.weread.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.loofer.framework.utils.UiUtils;
import org.loofer.weread.R;
import org.loofer.weread.event.Event;
import org.loofer.weread.mvp.ui.activity.ArtActivity;
import org.loofer.weread.mvp.ui.activity.DailyActivity;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * ============================================================
 * 版权： xx有限公司 版权所有（c）2016
 * <p>
 * 作者：Loofer
 * 版本：1.0
 * 创建日期 ：2017/1/7 14:09.
 * 描述：
 * <p>
 * 注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * Modified Date Modify Content:
 * <p>
 * ==========================================================
 */

public class LeftMenuFragment extends Fragment {

    @BindView(R.id.right_slide_close)
    ImageView mIvRightSlideClose;
    @BindView(R.id.search)
    ImageView mIvSearch;
    @BindView(R.id.logo_iv)
    ImageView mLogoIv;
    @BindView(R.id.home_page_tv)
    TextView mTvHomePage;
    @BindView(R.id.words_tv)
    TextView mTvWords;
    @BindView(R.id.voice_tv)
    TextView mTvVoice;
    @BindView(R.id.video_tv)
    TextView mTvVideo;
    @BindView(R.id.calendar_tv)
    TextView mTvCalendar;

    private Unbinder mUnbinder;
    private List<View> mViewList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        loadView();
        return view;
    }

    private void loadView() {
        mViewList = new ArrayList<>();
        mViewList.add(mTvHomePage);
        mViewList.add(mTvWords);
        mViewList.add(mTvVoice);
        mViewList.add(mTvVideo);
        mViewList.add(mTvCalendar);
    }


    @OnClick({R.id.right_slide_close, R.id.search, R.id.home_page_tv, R.id.words_tv, R.id.voice_tv, R.id.video_tv, R.id.calendar_tv})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.right_slide_close:
                EventBus.getDefault().post(new Event(1000,"closeMenu"));
                break;
            case R.id.search:
                UiUtils.makeText("搜索");
                break;
            case R.id.home_page_tv:
                EventBus.getDefault().post(new Event(1000,"closeMenu"));
                break;
            case R.id.words_tv:
                intent = new Intent(getActivity(), ArtActivity.class);
                intent.putExtra("mode",1);
                intent.putExtra("title","文  字");
                UiUtils.startActivity(intent);
                break;
            case R.id.voice_tv:
                intent = new Intent(getActivity(), ArtActivity.class);
                intent.putExtra("mode",3);
                intent.putExtra("title","声  音");
                UiUtils.startActivity(intent);
                break;
            case R.id.video_tv:
                intent = new Intent(getActivity(), ArtActivity.class);
                intent.putExtra("mode",2);
                intent.putExtra("title","影  像");
                UiUtils.startActivity(intent);
                break;
            case R.id.calendar_tv:
                intent = new Intent(getActivity(), DailyActivity.class);
                UiUtils.startActivity(intent);
                break;
        }
    }


    public void startAnim() {
        startIconAnim(mIvRightSlideClose);
        startIconAnim(mIvSearch);
        startColumnAnim();
    }

    private void startColumnAnim() {
        TranslateAnimation localTranslateAnimation = new TranslateAnimation(0F, 0.0F, 0.0F, 0.0F);
        localTranslateAnimation.setDuration(700L);
        for (int j=0;j<mViewList.size();j++){
            View localView = this.mViewList.get(j);
            localView.startAnimation(localTranslateAnimation);
            localTranslateAnimation = new TranslateAnimation(j * -35,0.0F, 0.0F, 0.0F);
            localTranslateAnimation.setDuration(700L);
        }
    }

    private void startIconAnim(View paramView) {
        ScaleAnimation localScaleAnimation = new ScaleAnimation(0.1F, 1.0F, 0.1F, 1.0F, paramView.getWidth() / 2, paramView.getHeight() / 2);
        localScaleAnimation.setDuration(1000L);
        paramView.startAnimation(localScaleAnimation);
        float f1 = paramView.getWidth() / 2;
        float f2 = paramView.getHeight() / 2;
        localScaleAnimation = new ScaleAnimation(1.0F, 0.5F, 1.0F, 0.5F, f1, f2);
        localScaleAnimation.setInterpolator(new BounceInterpolator());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != Unbinder.EMPTY) mUnbinder.unbind();
        mUnbinder = null;
    }
}
