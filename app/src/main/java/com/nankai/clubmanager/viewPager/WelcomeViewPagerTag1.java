package com.nankai.clubmanager.viewPager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.nankai.clubmanager.R;

import java.util.ArrayList;
import java.util.List;


public class WelcomeViewPagerTag1 extends RelativeLayout {

    private WelcomeViewPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<View> mViewList = new ArrayList<>();
    private int[] mLayouts = new int[] {R.layout.guide_view_one, R.layout.guide_view_two, R.layout.guide_view_three,
        R.layout.guide_view_four};

    public WelcomeViewPagerTag1(Context context) {
        super(context);
        init();
    }

    public WelcomeViewPagerTag1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_welcome_viewpager, this);
        mViewPager = (ViewPager) this.findViewById(R.id.welcome_viewpager);
        {
            /** 初始化4个页面 */
            for (int i = 0; i < mLayouts.length; i++) {
                View view = View.inflate(getContext(), mLayouts[i], null);
                mViewList.add(view);
            }
        }

        mAdapter = new WelcomeViewPagerAdapter(mViewList, getContext());
        mViewPager.setAdapter(mAdapter);
    }

}
