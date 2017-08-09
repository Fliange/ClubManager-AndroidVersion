package com.nankai.clubmanager.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.activity.MemberDetailActivity;
import com.nankai.clubmanager.dialog.MemberDialog;

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

public class MemberFragment extends Fragment {

    public static ListView listView;
    private View currentView;
    private int currentPosition;
    MemberDialog memberDialog;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private List<Drawable> imgIds=new ArrayList<>();
    private List<String> name=new ArrayList<>();
    private List<Integer> number=new ArrayList<>();
    private EditText search;
    private ImageView big;
    private TextView memberName;
    public TextView memberNumber;
    private SharedPreferences sp;
    public static String memId;
    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if(msg.what == 1){
                    String[] keys={"img","content","number"};
                    int[] ids={R.id.item_img_manage,R.id.item_content_manage,R.id.item_number_manage};
                    for(int i=0;i<name.size();i++){
                        Map<String,Object> map=new HashMap<>();
                        map.put("img",imgIds.get(i));
                        map.put("content",name.get(i));
                        map.put("number",number.get(i));
                        lists.add(map);
                    }
                SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.list_item,keys,ids);
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
            if(msg.what==2){
                try {
                    //JSONArray array=new JSONArray(result);
                    String result1 = (String) msg.obj;
                    JSONObject obj=new JSONObject(result1);
                    Intent intent = new Intent(MemberFragment.this.getActivity(), MemberDetailActivity.class);
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
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.member_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.manage_listView);
        search= (EditText) view.findViewById(R.id.search_member);
        big= (ImageView) view.findViewById(R.id.search);
        big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=String.valueOf(sp.getInt("department",0));
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1
                        .add("memberName",search.getText().toString())
                        .add("departmentId", String.valueOf(sp.getInt("department",0)))
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://192.168.40.70:8080/PClubManager/Member_findMemberByNameForAndroid")
                        .post(formBody)
                        .build();
                exec(request,1);
            }
        });
        //先得到登录人的身份
        sp=getActivity().getSharedPreferences("loginInfor",MODE_PRIVATE);
        String username=sp.getString("username","");
        //在这里给后端发请求得到所有成员的头像和名字
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .add("username",username)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.70:8080/PClubManager/Member_managerSearchForAndroid")
                .post(formBody)
                .build();
        exec(request,1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memberName= (TextView) view.findViewById(R.id.item_content_manage);
                memberNumber= (TextView) view.findViewById(R.id.item_number_manage);
//                //实现的功能是一点击成员的名字会显示成员的详细信息
//                memberName.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
                        //先给后台发请求从数据库查询本人
                String s=memberNumber.getText().toString();
                String h=number.get(position).toString();
                FormBody.Builder builder3 = new FormBody.Builder();
                FormBody formBody1 = builder3
                        .add("memberNumber",number.get(position).toString())
                        .build();
                Request.Builder builder4 = new Request.Builder();
                Request request4 = builder4.url("http://192.168.40.70:8080/PClubManager/Member_findMemberByIdForAndroid")
                        .post(formBody1)
                        .build();
                exec(request4,2);
//                    }
//                });
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                memberDialog=new MemberDialog(getActivity(),R.style.MemberDialogStyle);
                memId=number.get(position).toString();
                int[] location = new int[2];
                currentView = view;
                currentPosition = position;
                currentView.setBackgroundColor(getResources().getColor(
                        android.R.color.darker_gray));
                currentView.getLocationOnScreen(location);// x为0，y根据点击位置不同而不同
                DisplayMetrics displayMetrics = new DisplayMetrics();
                Display display = getActivity().getWindowManager()
                        .getDefaultDisplay();
                display.getMetrics(displayMetrics);
                // 获取dialog的窗口
                WindowManager.LayoutParams params = memberDialog.getWindow()
                        .getAttributes();


                params.gravity = Gravity.BOTTOM;
                params.y = display.getHeight() - location[1];
                memberDialog.getWindow().setAttributes(params);
                memberDialog.setCanceledOnTouchOutside(true);
                memberDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        currentView.setBackgroundColor(getResources().getColor(
                                android.R.color.white));
                    }
                });
                memberDialog.show();

                return true;
            }
        });
        return view;
    }
    private void exec(Request request, final int which) {
        int s=which;
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
