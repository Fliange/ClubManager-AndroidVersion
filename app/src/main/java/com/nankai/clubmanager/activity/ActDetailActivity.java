package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;

@ContentView(R.layout.act_detail)
public class ActDetailActivity extends Activity {
    @ViewInject(R.id.activity_detail_Name)
    private TextView activityDetailName;
    @ViewInject(R.id.activity_detail_content)
    private TextView activityDetailContent;
    @ViewInject(R.id.activity_detail_Picture)
    private ImageView activityDetailPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //获取intent中的bundle对象
        Bundle bundle = this.getIntent().getExtras();
        //获取数据
        String ActivityName = bundle.getString("ActivityName");
        String ActivityPicture = bundle.getString("ActivityPicture");
        String ActivityContent = bundle.getString("ActivityContent");
        String ActivityOrganization = bundle.getString("ActivityOrganization");

        activityDetailName.setText(ActivityName);
        activityDetailContent.setText(ActivityContent);

        //通过图片名字，将图片对象搞进去,新开线程
        final String IMAGE_URL = "http://192.168.40.72:8080/PClubManager/images/"+ActivityPicture;
        int activityId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从服务器拿图片
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                //拿到图片就显示
                ActDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityDetailPicture.setImageDrawable(drawable);
                    }
                });
            }
        }).start();


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
