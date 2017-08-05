package com.nankai.clubmanager.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.chat_room)
public class ChatRoomActivity extends AppCompatActivity {

    //注册组件
    @ViewInject(R.id.chatroom_content)
    private TextView chatroomContent;
    @ViewInject(R.id.chatroom_input)
    private EditText chatroomInput;
    @ViewInject(R.id.chatroom_btm)
    private Button chatroomBtm;
    @ViewInject(R.id.chatroom_img)
    private ImageView chatroomImg;



    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        x.view().inject(this);
    }

    //监听按钮，按钮按下时，发送输入框里面的数据，同时从数据库查询，将查询的结果显示出来
    @Event(value = {R.id.chatroom_btm},type = View.OnClickListener.class)
    private void doEvent(View view)
    {
        //使用post的方式，向后端action发起传输请求
        FormBody.Builder builder1 = new FormBody.Builder();
        String text = chatroomInput.getText().toString();
        FormBody formBody = builder1.add("content", text)
                                    .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.72:8080/PClubManager/Act_showAllForList")
                                .post(formBody)
                                .build();
        exec(request);
    }

    //将发送request的过程和回调函数的定义封装成一个方法
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("失败：","-----"+e);
                final String error = e.toString();
                ChatRoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatroomContent.setText(error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","-----");
                //从服务器传回来的json字符串
                final String msg = response.body().string();
                //解析json
                /**将json字符串转换为List<Map<String,Object>>
                 * @param jsonString
                 * @return
                 */

                 List<Map<String, Object>> arrayList = JSON.parseObject(msg,
                            new TypeReference<List<Map<String, Object>>>() {
                            });
                //遍历json数组
                /*for (int i = 0; i < jsonArray.size(); i++) {

                }*/
                Map<String, Object> activity = arrayList.get(0);
                final String activityName = (String) activity.get("ActivityName");
                String activityPic = (String) activity.get("ActivityPicture");
                String IMAGE_URL = "http://192.168.40.72:8080/PClubManager/images/"+activityPic;
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);


                ChatRoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatroomContent.setText(activityName);
                            chatroomImg.setImageDrawable(drawable) ;
                        }
                    });
                }
        });
    }

    //获取图片的函数
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
