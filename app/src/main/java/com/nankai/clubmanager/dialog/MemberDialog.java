package com.nankai.clubmanager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.activity.MemberDetailActivity;
import com.nankai.clubmanager.fragment.MemberFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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

import static android.content.Context.MODE_PRIVATE;

public class MemberDialog extends Dialog implements View.OnClickListener {

    Context context;
    IOnClickListener iOnclicklistener;
    private TextView left, right;
    private SharedPreferences sp;
    OkHttpClient okHttpClient = new OkHttpClient();
    MemberFragment mf=new MemberFragment();//这里想引用它里面的组件
    private List<Map<String,Object>> lists=new ArrayList<>();
    private List<Drawable> imgIds=new ArrayList<>();
    private List<String> name=new ArrayList<>();
    private List<Integer> number=new ArrayList<>();
    private ListView listView;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if (msg.what == 1) {
                try {
                    JSONObject obj=new JSONObject(result);
                    Intent intent = new Intent(context, MemberDetailActivity.class);
                    intent.putExtra("number", obj.getInt("memberId"));
                    intent.putExtra("name",obj.getString("memberName"));
                    intent.putExtra("gender",obj.getString("memberGender"));
                    intent.putExtra("major",obj.getString("memberMajor"));
                    intent.putExtra("hometown",obj.getString("memberHometown"));
                    intent.putExtra("phone",obj.getString("memberPhone"));
                    intent.putExtra("position",obj.getString("memberPosition"));
                    intent.putExtra("birthday",obj.getString("memberBirthday"));
                    intent.putExtra("department",obj.getString("memberDepartment"));
                    intent.putExtra("picture",obj.getString("memberPicture"));
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(msg.what==2){
                if(result.equals("success")){
                    sp=context.getSharedPreferences("loginInfor",MODE_PRIVATE);
                    String username=sp.getString("username","");
                    Toast.makeText(context,"删除成功,挥手告别",Toast.LENGTH_LONG).show();
                    FormBody.Builder builder1 = new FormBody.Builder();
                    FormBody formBody = builder1
                            .add("username",username)
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url("http://192.168.40.70:8080/PClubManager/Member_managerSearchForAndroid")
                            .post(formBody)
                            .build();
                    exec(request,3);
                }else{
                    Toast.makeText(context,"不好意思哦，您的权限不够",Toast.LENGTH_LONG).show();
//                    Intent intent=new Intent(context, MemberFragment.class);
//                    context.startActivity(intent);
                }
            }
            if(msg.what==3){
                String[] keys={"img","content","number"};
                int[] ids={R.id.item_img_manage,R.id.item_content_manage,R.id.item_number_manage};
                for(int i=0;i<name.size();i++){
                    Map<String,Object> map=new HashMap<>();
                    map.put("img",imgIds.get(i));
                    map.put("content",name.get(i));
                    map.put("number",number.get(i));
                    lists.add(map);
                }
                SimpleAdapter simpleAdapter=new SimpleAdapter(context,lists,R.layout.list_item,keys,ids);
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
                listView.setAdapter(simpleAdapter);
            }
        }
    };
    public MemberDialog(Context context,int theme) {
        super(context,theme);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_dialog);
        left = (TextView) findViewById(R.id.left);
        left.setOnClickListener(this);
        right = (TextView) findViewById(R.id.right);
        right.setOnClickListener(this);
        String s=mf.memId;
        listView = MemberFragment.listView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1
                        .add("memberNumber",mf.memId)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://192.168.40.70:8080/PClubManager/Member_findMemberByIdForAndroid")
                        .post(formBody)
                        .build();
                exec(request,1);
                break;
            case R.id.right:
                AlertDialog.Builder builder2=new AlertDialog.Builder(context);
                builder2.setTitle("啊哈");
                builder2.setIcon(R.drawable.ah);
                builder2.setMessage("确定要删除吗？");
                builder2.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FormBody.Builder builder1 = new FormBody.Builder();
                        FormBody formBody = builder1
                                .add("memberNumber",mf.memId)
                                .build();
                        Request.Builder builder = new Request.Builder();
                        Request request = builder.url("http://192.168.40.70:8080/PClubManager/Member_deleteForAndroid")
                                .post(formBody)
                                .build();
                        exec(request,2);
                    }
                });
                builder2.setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
                break;
            default:
                break;
        }

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
                Message message1 = new Message();
                if(which==1){
                    message1.what = 1;
                    message1.obj = s;
                    handler.sendMessage(message1);
                }
                if(which==2){
                    message1.what = 2;
                    message1.obj = s;
                    handler.sendMessage(message1);
                }
                if(which==3){
                    try {
                        JSONArray array = new JSONArray(s);
                        String imgPath, IMAGE_URL;
                        Drawable drawable;
                        imgIds.clear();
                        name.clear();
                        number.clear();
                        lists.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            imgPath = obj.getString("memberPicture");
                            IMAGE_URL = "http://192.168.40.70:8080/PClubManager/images/" + imgPath;
                            drawable = loadImageFromNetwork(IMAGE_URL);
                            imgIds.add(drawable);
                            name.add(obj.getString("memberName"));
                            number.add(obj.getInt("memberId"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = 3;
                    message.obj = s;
                    handler.sendMessage(message);
                }
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
    public interface IOnClickListener {
        public void leftOnClick();

        public void rightOnClick();

    }
}
