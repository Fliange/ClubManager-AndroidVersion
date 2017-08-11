package com.nankai.clubmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MySwipeAdapter extends BaseSwipeAdapter {
    List<MySwipeBean> items;
    Context context;
    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if(result.equals("success")){
                Toast.makeText(context,"删除成功",Toast.LENGTH_LONG).show();
            }
        }
    };

    public MySwipeAdapter(Context context, List<MySwipeBean> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.sl_content;
    }


    @Override
    public View generateView(final int i, ViewGroup viewGroup) {


        View view = View.inflate(context, R.layout.adapter, null);

        return view;
    }


    @Override
    public void fillValues(final int i, View view) {
        TextView name = (TextView) view.findViewById(R.id.manage_material_name);
        TextView org = (TextView) view.findViewById(R.id.manage_material_org);
        TextView count = (TextView) view.findViewById(R.id.manage_material_count);
        TextView extra = (TextView) view.findViewById(R.id.manage_materail_extra);

        final CheckBox cb_swipe_tag1 = (CheckBox) view.findViewById(R.id.cb_swipe_tag1);


        TextView tv_swipe_delect1 = (TextView) view.findViewById(R.id.tv_swipe_delect1);


        TextView tv_swipe_top1 = (TextView) view.findViewById(R.id.tv_swipe_top1);


        final SwipeLayout sl_content = (SwipeLayout) view.findViewById(R.id.sl_content);


        sl_content.setShowMode(SwipeLayout.ShowMode.PullOut);


//        BitmapUtils bitmapUtils=new BitmapUtils(context);
//        bitmapUtils.display(iv_myice,items.get(i).getIce());


        tv_swipe_delect1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, items.get(i).getContent() + i, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("啊哈");
                builder2.setIcon(R.drawable.ah);
                builder2.setMessage("确定要删除吗？");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FormBody.Builder builder1 = new FormBody.Builder();
                        FormBody formBody = builder1
                                .add("materialId", String.valueOf(items.get(i).getId()))
                                .build();
                        Request.Builder builder = new Request.Builder();
                        Request request = builder.url("http://192.168.40.70:8080/PClubManager/material_deleteForAndroid")
                                .post(formBody)
                                .build();
                        exec(request);
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
                items.remove(i);
                notifyDataSetChanged();
                sl_content.close();
            }
        });


        cb_swipe_tag1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,items.get(i).getContent()+i,Toast.LENGTH_SHORT).show();
                if (cb_swipe_tag1.isChecked()) {
                    items.get(i).setTag(true);
                    notifyDataSetChanged();
                    sl_content.close();
                } else {
                    items.get(i).setTag(false);
                    notifyDataSetChanged();
                    sl_content.close();
                }


            }
        });


        tv_swipe_top1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,items.get(i).getContent()+i,Toast.LENGTH_SHORT).show();
//                items.add(items.get(position));
                items.add(0, items.get(i));
                items.remove(i + 1);
                notifyDataSetChanged();
                sl_content.close();
            }
        });


        name.setText(items.get(i).getContent());
        org.setText(items.get(i).getOrg());
        count.setText(items.get(i).getCount().toString());
        extra.setText(items.get(i).getExtra());
        if (items.get(i).isTag()) {
            name.setTextColor(Color.parseColor("#000000"));
            name.setBackground(view.getResources().getDrawable(R.drawable.material_bg));
            cb_swipe_tag1.setText("取消标记");
        } else {
            name.setTextColor(Color.parseColor("#ffffff"));
            name.setBackground(view.getResources().getDrawable(R.drawable.material_name_bg));
            cb_swipe_tag1.setText("标记");
        }
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder{
        TextView name;
        TextView org;
        TextView count;
        TextView extra;
    }
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
                Log.i("异常：", "--->" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Log.i("成功：", "--->");
                String s = response.body().string();
                Log.i("结果：", "--->" + s);
                Message message1 = new Message();
                message1.what = 2;
                message1.obj = s;
                handler.sendMessage(message1);
            }
        });
    }
}