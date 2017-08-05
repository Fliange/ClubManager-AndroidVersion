package com.nankai.clubmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.nankai.library.NaviView;

public class MainActivity extends Activity {

    private Fragment contentFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private NaviView mHomePageView;//主页
    private NaviView mFoundView;//发现
    private NaviView mManageView;//管理
    private NaviView mRegistView;//报名


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi(){
        mHomePageView = (NaviView) findViewById(R.id.view_homepage);
        mHomePageView.setOnClickListener(itemClick);

        mRegistView = (NaviView) findViewById(R.id.view_regist);
        mRegistView.setOnClickListener(itemClick);

        mFoundView = (NaviView) findViewById(R.id.view_found);
        mFoundView.setOnClickListener(itemClick);

        mManageView = (NaviView)findViewById(R.id.view_manage);
        mManageView.setOnClickListener(itemClick);

        mHomePageView.setBigIcon(R.drawable.home_page_big);
        mHomePageView.setSmallIcon(R.drawable.home_page_small);
        //fragment管理者
        fragmentManager = getFragmentManager();
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
