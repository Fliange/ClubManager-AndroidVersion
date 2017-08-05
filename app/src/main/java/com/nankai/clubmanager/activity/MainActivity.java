package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;
import com.nankai.clubmanager.fragment.FoundViewFragment;
import com.nankai.clubmanager.fragment.HomePageFragment;
import com.nankai.clubmanager.fragment.ManageViewFragment;
import com.nankai.clubmanager.fragment.RegistViewFragment;
import com.nankai.library.NaviView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.activity_main)
public class MainActivity extends Activity {

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


    private ListView homepageListview;//主界面显示活动的

    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        initUi();
        setHomePage();
    }

    //初始化UI组件
    private void initUi(){

        mHomePageView.setOnClickListener(itemClick);

        mRegistView.setOnClickListener(itemClick);

        mFoundView.setOnClickListener(itemClick);

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

    //添加监听（监听底部按钮）
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

    //用于从数据库拿数据 并显示到首页
    private void setHomePage()
    {
        //从数据库读取活动信息
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1.build();
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://192.168.40.72:8080/PClubManager/Act_showAllForList")
                .post(formBody)
                .build();
        exec(request1);
    }

    //将发送request的过程和回调函数的定义封装成一个方法
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("失败：","-----"+e);
                final String error = e.toString();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //chatroomContent.setText(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","-----");

                //从服务器传回来的json字符串
                final String msg = response.body().string();

                /**将json字符串转换为List<Map<String,Object>>
                 * @param jsonString
                 * @return
                 */
                List<Map<String, Object>> arrayList = JSON.parseObject(msg,
                        new TypeReference<List<Map<String, Object>>>() {});
                List<Map<String,Object>> list_map = new ArrayList<Map<String,Object>>(); //定义一个适配器对象
                //遍历json数组,将图片对象搞进去

                String activityName, activityPic,IMAGE_URL;
                Drawable drawable;
                for (int i = 0; i < arrayList.size(); i++) {
                    Map<String, Object> activity = arrayList.get(i);
                    activityName = (String) activity.get("ActivityName");
                    activityPic = (String) activity.get("ActivityPicture");
                    IMAGE_URL = "http://192.168.40.72:8080/PClubManager/images/"+activityPic;
                    drawable = loadImageFromNetwork(IMAGE_URL);
                    activity.put("pic",drawable);
                    list_map.add(activity);
                }
                final SimpleAdapter simpleAdapter = new SimpleAdapter(
                        MainActivity.this,/*传入一个上下文作为参数*/
                        list_map,         /*传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。*/
                        R.layout.homepage_list_item, /*设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局*/
                        new String[]{"pic","ActivityName"}, /*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*/
                        new int[]{R.id.item_img,R.id.item_text});/*传入items布局文件中需要指定传入的控件，这里直接上id即可*/

                //ViewBinder该类可以帮助SimpleAdapter加载图片(Drawable)
                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if(view instanceof ImageView && data instanceof Drawable){
                            ImageView iv = (ImageView)view;
                            iv.setImageDrawable((Drawable)data);
                            return true;
                        }else{
                            return false;
                        }
                    }
                });


                //修改UI控件
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homepageListview = (ListView) findViewById(R.id.homepage_listview);
                        homepageListview.setAdapter(simpleAdapter);
                    }
                });
            }

        });
    }

    //获取图片的方法
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }
        return drawable ;
    }
}
