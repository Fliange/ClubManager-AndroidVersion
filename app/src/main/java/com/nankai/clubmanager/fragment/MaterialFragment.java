package com.nankai.clubmanager.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.nankai.clubmanager.MySwipeAdapter;
import com.nankai.clubmanager.MySwipeBean;
import com.nankai.clubmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MaterialFragment extends Fragment {
    private ListView lv_myswipe;

    private List<Integer> mid=new ArrayList<>();
    private List<Integer> count=new ArrayList<>();
    private List<String> name=new ArrayList<>();
    private List<String> extra=new ArrayList<>();  //备注
    private List<String> orgName=new ArrayList<>();
    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if (msg.what == 1) {
                init();
            }
            if(msg.what==2){
                if(result.equals("success")){
                    Toast.makeText(MaterialFragment.this.getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_my_swipe, container,false);
        lv_myswipe= (ListView) view.findViewById(R.id.lv_myswipe);
        //在这里给后端发请求得到所有物资的名称数量所在的组织和备注
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.70:8080/PClubManager/material_searchForAndroid")
                .post(formBody)
                .build();
        exec(request,1);
        return view;
    }

    private void init() {

        List<MySwipeBean> lists=new ArrayList<MySwipeBean>();
//        lists.add(new MySwipeBean("桌子","科协","10","这是科协",false));
        for (int i=0;i<mid.size();i++){
            lists.add(new MySwipeBean(mid.get(i),name.get(i),orgName.get(i),count.get(i),extra.get(i),false));
        }
        MySwipeAdapter adapter=new MySwipeAdapter(MaterialFragment.this.getActivity(),lists);
        lv_myswipe.setAdapter(adapter);


        adapter.setMode(Attributes.Mode.Single);


    }
    private void exec(Request request, final int which) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
                Log.i("异常：","--->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Log.i("成功：","--->");
                String s = response.body().string();
                Log.i("结果：","--->"+s);
                switch (which) {
                    case 1:
                        try {
                            JSONArray array = new JSONArray(s);
                            String imgPath, IMAGE_URL;
                            Drawable drawable;
                            mid.clear();
                            count.clear();
                            name.clear();
                            orgName.clear();
                            extra.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                mid.add(obj.getInt("materialId"));
                                name.add(obj.getString("materialName"));
                                count.add(obj.getInt("materialCount"));
                                orgName.add(obj.getString("materialOrg"));
                                extra.add(obj.getString("materialExtra"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 1;
                        message.obj = s;
                        handler.sendMessage(message);
                        break;
                    case 2:
                        Message message1 = new Message();
                        message1.what = 2;
                        message1.obj = s;
                        handler.sendMessage(message1);
                        break;
                }
            }
        });
    }
}
