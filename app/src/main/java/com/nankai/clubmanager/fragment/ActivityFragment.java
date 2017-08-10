package com.nankai.clubmanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityFragment extends Fragment {
    private ListView listView;
    private List<Map<String,Object>> lists=new ArrayList<>();

    private int[] photo={R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,
            R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,
            R.drawable.test_pic};  //照片

    private String[] detail={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};  //详情

    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();
    //收取所有活动的信息
    private List<Map<String, Object>> activityList = new ArrayList<>();


    //handler，在回调函数里监听,如果已经拿到了所有的活动的数据，就在listview显示出来
    final Handler handler = new Handler(){          // handle
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    String[] keys={"icon","name"};
                    int[] ids={R.id.item_img,R.id.item_text};

                    SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.activity_manage_list_item,keys,ids);
                    listView.setAdapter(simpleAdapter);

                    lists.clear();
                    for(int i=0; i < activityList.size(); i++){
                        Map<String,Object> map=new HashMap<>();
                        map.put("icon",activityList.get(i).get("ActivityName").toString().substring(0,1));
                        map.put("name",activityList.get(i).get("ActivityName").toString());
                        lists.add(map);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.activity_listview);
        setHomePage();
        return view;
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","-----");

                //从服务器传回来的json字符串
                final String msg = response.body().string();

                /**将json字符串转换为List<Map<String,Object>>*/
                if(!activityList.isEmpty())
                {//如果非空，先清空
                    activityList.clear();
                }
                activityList = JSON.parseObject(msg,new TypeReference<List<Map<String, Object>>>() {});

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
               /* list_map.clear();

                final SimpleAdapter simpleAdapter = new SimpleAdapter(
                        MainActivity.this,*//*传入一个上下文作为参数*//*
                        list_map,         *//*传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。*//*
                        R.layout.homepage_list_item, *//*设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局*//*
                        new String[]{"pic","ActivityName","ActivityOrganization","ActivityIntroduction"}, *//*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*//*
                        new int[]{R.id.item_img,R.id.item_title,R.id.item_author,R.id.item_text});*//*传入items布局文件中需要指定传入的控件，这里直接上id即可*//*
                //加载页面
                try
                {
                    Thread.currentThread().sleep(1000);//毫秒
                }
                catch(Exception e){}
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AVLoadingIndicatorView avi;
                        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
                        avi.hide();
                    }
                });

                // or avi.smoothToHide();
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
                        homepageListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                *//*TextView activityDetailName = (TextView) view.findViewById(R.id.activity_detail_Name);
                                String s = activityDetailName.getText().toString();*//*
                                Map<String,Object> map = list_map.get(position);
                                int actId = (int) map.get("ActivityId");
                                Log.i("活动ID-----",""+actId);
                                Intent intent = new Intent(MainActivity.this,ActDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ActivityId", actId);
                                bundle.putString("ActivityPicture", (String) map.get("ActivityPicture"));
                                bundle.putString("ActivityName", (String) map.get("ActivityName"));
                                bundle.putString("ActivityContent", (String) map.get("ActivityContent"));
                                bundle.putString("ActivityOrganization", (String) map.get("ActivityOrganization"));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }
                });*/
            }
        });
    }
}
