package com.nankai.clubmanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.nankai.clubmanager.R;
import com.nankai.clubmanager.activity.updateMaterialActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MaterialFragment extends Fragment {

    private ListView listView;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private List<Integer> mid=new ArrayList<>();
    private List<Integer> count=new ArrayList<>();
    private List<String> name=new ArrayList<>();
    private List<String> extra=new ArrayList<>();  //备注
    private List<String> orgName=new ArrayList<>();
    private TextView edit;
    private TextView deleteM;
    SimpleAdapter simpleAdapter;
    OkHttpClient okHttpClient = new OkHttpClient();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if (msg.what == 1) {
                String[] keys={"name","count","extra","orgName"};
                int[] ids={R.id.manage_material_name,R.id.manage_material_count,R.id.manage_materail_extra,R.id.manage_material_org};
                for(int i=0;i<name.size();i++){
                    Map<String,Object> map=new HashMap<>();
                    map.put("name",name.get(i));
                    map.put("count",count.get(i));
                    map.put("extra",extra.get(i));
                    map.put("orgName",orgName.get(i));
                    lists.add(map);
                }
                simpleAdapter=new SimpleAdapter(MaterialFragment.this.getActivity(),lists,R.layout.list_item_material,keys,ids);
                listView.setAdapter(simpleAdapter);
            }
            if(msg.what==2){
                if(result.equals("success")){
                    Toast.makeText(MaterialFragment.this.getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.material_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.manage_material_listView);
        com.daimajia.swipe.SwipeLayout lay= (SwipeLayout) view.findViewById(R.id.lay);

        //在这里给后端发请求得到所有物资的名称数量所在的组织和备注
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.70:8080/PClubManager/material_searchForAndroid")
                .post(formBody)
                .build();
        exec(request,1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                edit = (TextView) view.findViewById(R.id.material_item_view);
                deleteM = (TextView) view.findViewById(R.id.material_item_delete);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MaterialFragment.this.getActivity(), updateMaterialActivity.class);
                        intent.putExtra("name", name.get(position));
                        intent.putExtra("count", count.get(position));
                        intent.putExtra("orgName", orgName.get(position));
                        intent.putExtra("extra", extra.get(position));
                        startActivity(intent);
                    }
                });
                deleteM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(MaterialFragment.this.getActivity());
                        builder2.setTitle("啊哈");
                        builder2.setIcon(R.drawable.ah);
                        builder2.setMessage("确定要删除吗？");
                        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FormBody.Builder builder1 = new FormBody.Builder();
                                FormBody formBody = builder1
                                        .add("materialId", String.valueOf(mid.get(position)))
                                        .build();
                                Request.Builder builder = new Request.Builder();
                                Request request = builder.url("http://192.168.40.70:8080/PClubManager/material_deleteForAndroid")
                                        .post(formBody)
                                        .build();
                                exec(request, 2);
                            }
                        });
                        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder2.show();
                    }
                });
            }
        });
        return view;
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
                            lists.clear();
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
