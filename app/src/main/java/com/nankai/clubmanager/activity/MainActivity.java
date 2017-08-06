package com.nankai.clubmanager.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.fragment.FoundViewFragment;
import com.nankai.clubmanager.fragment.HomePageFragment;
import com.nankai.clubmanager.fragment.ManageViewFragment;
import com.nankai.clubmanager.fragment.RegistViewFragment;
import com.nankai.library.NaviView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    private Fragment contentFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @ViewInject(R.id.view_homepage)
    private NaviView mHomePageView;//主页

    @ViewInject(R.id.view_found)
    private NaviView mFoundView;//发现

    @ViewInject(R.id.view_manage)
    private NaviView mManageView;//管理

    @ViewInject(R.id.view_regist)
    private NaviView mRegistView;//报名


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        initUi();
    }

    private void initUi(){

        mHomePageView.setOnClickListener(itemClick);

        mRegistView.setOnClickListener(itemClick);

        mFoundView.setOnClickListener(itemClick);

        mManageView.setOnClickListener(itemClick);

        mHomePageView.setBigIcon(R.drawable.home_page_big);
        mHomePageView.setSmallIcon(R.drawable.home_page_small);
        //fragment管理者
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        Fragment init = new HomePageFragment();
        transaction.replace(R.id.fragmentPager,init,"fragment");
        transaction.commit();

    }

    private View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            transaction = fragmentManager.beginTransaction();
            resetIcon();
            switch (view.getId()) {
                case R.id.view_homepage:
                    mHomePageView.setBigIcon(R.drawable.home_page_big);
                    mHomePageView.setSmallIcon(R.drawable.home_page_small);
                    contentFragment = new HomePageFragment();
                    transaction.replace(R.id.fragmentPager, contentFragment);
                    break;
                case R.id.view_regist:
                    mRegistView.setBigIcon(R.drawable.regist_big);
                    mRegistView.setSmallIcon(R.drawable.regist_small);
                    contentFragment = new RegistViewFragment();
                    transaction.replace(R.id.fragmentPager, contentFragment);
                    break;
                case R.id.view_found:
                    mFoundView.setBigIcon(R.drawable.found);
                    contentFragment = new FoundViewFragment();
                    transaction.replace(R.id.fragmentPager, contentFragment);
                    break;
                case R.id.view_manage:
                    mManageView.setBigIcon(R.drawable.manage);
                    contentFragment = new ManageViewFragment();
                    transaction.replace(R.id.fragmentPager, contentFragment);
                    break;
            }
            transaction.commit();

        }
    };


    private void resetIcon() {
        mHomePageView.setBigIcon(R.drawable.pre_homepage_big);
        mHomePageView.setSmallIcon(R.drawable.pre_homepage_small);

        mRegistView.setBigIcon(R.drawable.pre_regist_big);
        mRegistView.setSmallIcon(R.drawable.pre_regist_small);

        mFoundView.setBigIcon(R.drawable.pre_found_big);
        mFoundView.setSmallIcon(R.drawable.pre_found_small);

        mManageView.setBigIcon(R.drawable.pre_manage);
    }
}
