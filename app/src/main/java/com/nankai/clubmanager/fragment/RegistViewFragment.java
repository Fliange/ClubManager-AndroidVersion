package com.nankai.clubmanager.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangjin on 2017/8/5.
 */

public class RegistViewFragment extends Fragment{

    private TextView choosebirthday;
    private EditText number;
    private EditText name;
    private EditText telphone;
    private EditText introduction;
    private Spinner sex;
    private Spinner hometown;
    private Spinner major;
    private RadioGroup radioGroup;
    private String choice;//是否服从调剂
    private Spinner intent1;
    private Spinner intent2;
    private Button confirmRegist;
    private List<String> departmentData;//记录的是返回的所有的部门信息
    private Context context;
    final Calendar cal=Calendar.getInstance();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String result = (String) msg.obj;
                if(result.equals("success")){
                    Toast.makeText(RegistViewFragment.this.getActivity(),"报名成功",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegistViewFragment.this.getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
            if(msg.what == 2){ //返回的是所有的部门
                String result = (String) msg.obj;
                try {
                    JSONArray array=new JSONArray(result);
                    for(int i=0;i<array.length();i++){
                        JSONObject obj=array.getJSONObject(i);
                        departmentData.add(obj.getInt("departmentId")+"  "+obj.getString("departmentName"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,departmentData);
                    intent1.setAdapter(adapter);
                    intent2.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    OkHttpClient okHttpClient = new OkHttpClient();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.regist_fragment, null);
        context=this.getActivity();
        departmentData=new ArrayList<>();
        choosebirthday = (TextView) view.findViewById(R.id.choosebirthday);
        number= (EditText) view.findViewById(R.id.number);
        name=(EditText) view.findViewById(R.id.name);
        telphone=(EditText) view.findViewById(R.id.telphone);
        introduction=(EditText) view.findViewById(R.id.introduction);
        sex= (Spinner) view.findViewById(R.id.sex);
        hometown=(Spinner) view.findViewById(R.id.hometown);
        major=(Spinner) view.findViewById(R.id.major);
        radioGroup= (RadioGroup) view.findViewById(R.id.radioGroup);
        intent1=(Spinner) view.findViewById(R.id.intent1);
        intent2=(Spinner) view.findViewById(R.id.intent2);

        confirmRegist= (Button) view.findViewById(R.id.confirmButton);
        confirmRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//自动获取当前时间作为报名时
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1.add("number",number.getText().toString())
                        .add("name", name.getText().toString())
                        .add("telphone",telphone.getText().toString())
                        .add("date",df.format(new Date()))
                        .add("introduction",introduction.getText().toString())
                        .add("sex",sex.getSelectedItem().toString())
                        .add("hometown",hometown.getSelectedItem().toString())
                        .add("major",major.getSelectedItem().toString())
                        .add("birthday", choosebirthday.getText().toString())
                        .add("choice",choice)
                        .add("intent1",intent1.getSelectedItem().toString().substring(0,intent1.getSelectedItem().toString().indexOf(' ')))
                        .add("intent2",intent2.getSelectedItem().toString().substring(0,intent2.getSelectedItem().toString().indexOf(' ')))
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/register_addForAndroid")
                        .post(formBody)
                        .build();
                exec(request1);
            }
        });
        //发请求获取全部的部门，不需要传递什么参数就用get方式
        /*FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1.build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.70:8080/PClubManager/Department_findAllForAndroid")
                .post(formBody)
                .build();*/
       Request request=new Request.Builder()
                .url("http://192.168.40.70:8080/PClubManager/Department_findAllForAndroid")
                .get()
                .build();
        exec1(request);
        //Tread.
//        ArrayAdapter adapter = new ArrayAdapter(this.getActivity(),android.R.layout.simple_list_item_1,departmentData);
//        intent1.setAdapter(adapter);
//        intent2.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch(checkedId){
                    case R.id.yes:
                        choice="yes";
                        break;
                    case R.id.no:
                        choice="no";
                        break;
                }
            }
        });

        choosebirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(),
                        listener ,
                        cal .get(Calendar. YEAR ),
                        cal .get(Calendar. MONTH ),
                        cal .get(Calendar. DAY_OF_MONTH )
                ).show();
            }
        });
        return view;
    }

    // 日期选择对话框的 DateSet 事件监听器
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){  //
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            cal .set(Calendar. YEAR , arg1);
            cal .set(Calendar. MONTH , arg2);
            cal .set(Calendar. DAY_OF_MONTH , arg3);
            updateDate();
        }
    };

    // 当 DatePickerDialog 关闭，更新日期显示
    private void updateDate(){
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        choosebirthday .setText( df .format( cal .getTime()));
    }

    private void exec(Request request) {
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
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }
    //因为要发不同的请求接受不同的参数所以写了两个
    private void exec1(Request request) {
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
                Message message = new Message();
                message.what = 2;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }
}
