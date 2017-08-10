package com.nankai.clubmanager.viewPager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nankai.clubmanager.R;

import java.util.ArrayList;
import java.util.List;

public class AboutUsViewPager extends Activity {
    private View view1, view2, view3,view4;// 需要滑动的页卡
    private ViewPager viewPager;
    private List<View> pageViews;// 把需要滑动的页卡添加到这个list中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_view_pager);
        // 像普通控件一样先初始化
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        initView();
        MyAdapter pagerAdapter = new MyAdapter();
        viewPager.setAdapter(pagerAdapter);
    }
    private void initView() {
        //将要分页显示的View装入数组中
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.about_us_1, null));
        pageViews.add(inflater.inflate(R.layout.about_us_2, null));
        pageViews.add(inflater.inflate(R.layout.about_us_3, null));
        pageViews.add(inflater.inflate(R.layout.about_us_4, null));
    }
    public class MyAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public int getCount() {

            return pageViews.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pageViews.get(position));

        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }


        // 将每一页的布局填充如ViewGroup容器中
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageViews.get(position));

            return pageViews.get(position);
        }

    };
}
