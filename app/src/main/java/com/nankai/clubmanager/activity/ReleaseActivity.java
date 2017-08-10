package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.extra.OkHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@ContentView(R.layout.release)
public class ReleaseActivity extends Activity {

    @ViewInject(R.id.rich_edit)
    private RichEditor mEditor;
    @ViewInject(R.id.rich_view)
    private TextView mPreview;
    @ViewInject(R.id.rich_edit_head)
    private RichEditor editorHead;
    @ViewInject(R.id.font_edit)
    private View fontEdit;
    @ViewInject(R.id.align_edit)
    private View alignEdit;

    private SubmitActivity submitActivity;//弹出框
    private Spinner activityHoldOrganization;
    private TextView activityConfirmButton;
    private TextView activityCancelButton;
    private EditText activityTime;
    private EditText activityIntroduction;

    private ChangeImageActivity imageActivity = new ChangeImageActivity();
    private File tempFile;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private SharedPreferences sp;
    private String urlForRich;
    //用于判断字体栏是否显示
    private boolean fontAble = false;
    private boolean alignAble = false;
    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();
    //记录的是返回的所有的部门信息
    private List<String> departmentData = new ArrayList<String>();
    //封面
    private ImageView coverPic;
    private String coverPicName = "a";
    private TextView coverName;

    //handler，在回调函数里监听，照片有没有传给服务器，要是传了，就在富文本编辑器里面显示出来
    final Handler handler = new Handler(){          // handle
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    mEditor.insertImage(msg.obj.toString(),"dashund");
                case 2: //返回的是所有的部门
                    String result = (String) msg.obj;
                    try {
                        JSONArray array=new JSONArray(result);
                        //把返回的json转化成list
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            departmentData.add(obj.getInt("departmentId")+"  "+obj.getString("departmentOrg")+obj.getString("departmentName"));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(ReleaseActivity.this,android.R.layout.simple_list_item_single_choice,departmentData);
                        activityHoldOrganization.setAdapter(adapter);


                        //activityHoldOrganization.setBackgroundColor(R.color.colorRichEdit);
                       activityHoldOrganization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                           @Override
                           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                               Log.i("position------",""+position);
                               TextView tv = (TextView) view;
                               String org = ((TextView) view).getText().toString().substring(3);
                               tv.setText(org);
                               tv.setTextColor(Color.BLACK);
                               tv.setTextSize(16);
                           }

                           @Override
                           public void onNothingSelected(AdapterView<?> parent) {

                           }
                       });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release);
        x.view().inject(this);

        //对话框对象
        submitActivity = new SubmitActivity(ReleaseActivity.this,R.style.Base_Theme_AppCompat_Dialog_Alert, new SubmitActivity.LeaveMyDialogListener() {
            //监听对话框点击事件
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.activity_confirmButton:
                        submit();
                        Intent intent1 = new Intent(ReleaseActivity.this,MainActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.activity_cancelButton:
                        submitActivity.dismiss();
                        break;
                    case R.id.cover_pic:
                        // 激活系统图库，选择一张图片
                        Intent intent2 = new Intent(Intent.ACTION_PICK);
                        intent2.setType("image/*");
                        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                        startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
                        break;
                    default:
                        break;
                }
            }
        });
        submitActivity.setCanceledOnTouchOutside(true);
            View view = submitActivity.getCustomView();
        //获取对象
        activityHoldOrganization = (Spinner) view.findViewById(R.id.activity_hold_organization);
        coverPic = (ImageView) view.findViewById(R.id.cover_pic);
        coverName = (TextView) view.findViewById(R.id.textView2);
        //activityConfirmButton = (TextView) view.findViewById(R.id.activity_confirmButton);
        //activityCancelButton;
        activityTime = (EditText) view.findViewById(R.id.activity_time);
        activityIntroduction = (EditText) view.findViewById(R.id.activity_introduction);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setTextColor(Color.RED);

        mEditor.setPadding(10, 10, 10, 10);

        editorHead.setAlignLeft();
        editorHead.setEditorFontSize(40);
        editorHead.setHeading(1);

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_cam).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 激活相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                // 判断存储卡是否可以用，可用进行存储
                if (imageActivity.hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), "activityPic");
                    // 从文件中创建uri
                    Uri uri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
                startActivityForResult(intent, PHOTO_REQUEST_CAREMA);

                //String url = imageActivity.picForRichEdit(0);
                //mEditor.insertImage(urlForRich,"dachshund");
            }
        });



        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 激活系统图库，选择一张图片
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        findViewById(R.id.action_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fontAble){
                    fontEdit.setVisibility(View.GONE);
                    fontEdit.setBackgroundResource(R.drawable.font);
                    fontAble = false;
                }
                else {
                    fontEdit.setVisibility(View.VISIBLE);
                    fontEdit.setBackgroundResource(R.drawable.font2);
                    fontAble = true;
                }

            }
        });

        findViewById(R.id.action_align).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alignAble){
                    alignEdit.setVisibility(View.GONE);
                    alignEdit.setBackgroundResource(R.drawable.aligan);
                    alignAble = false;
                }
                else {
                    alignEdit.setVisibility(View.VISIBLE);
                    alignEdit.setBackgroundResource(R.drawable.aligan2);
                    alignAble = true;
                }


                //mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });


        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });
        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });
        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });
        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                //剪裁
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (imageActivity.hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(ReleaseActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                /**
                 * 获得图片
                 */
                coverPic.setImageBitmap(bitmap);
                coverName.setText("head/"+coverPicName+".png");
                //保存到SharedPreferences
                saveBitmapToSharedPreferences(bitmap);
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，746：404
        intent.putExtra("aspectX", 746);
        intent.putExtra("aspectY", 404);
        // 裁剪后输出图片的尺寸大小
        //intent.putExtra("outputX", 1024*1);
        //intent.putExtra("outputY", 1024*1.375);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码根据调用者确定
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //保存图片到SharedPreferences
    public void saveBitmapToSharedPreferences(Bitmap bitmap) {
        // Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步:将String保持至SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("testSP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", imageString);
        editor.commit();

        //上传头像
        setImgByStr(imageString,"");
    }

    /**
     * 上传头像       此处使用用的OKHttp post请求上传的图片
     * @param imgStr
     * @param imgName
     */
    public  void setImgByStr(String imgStr, String imgName) {
        String url = "http://192.168.40.72:8080/PClubManager/loadImageForSwain_GenerateImageForActivity";
        Map<String, String> params = new HashMap<String, String>();
        Long time = System.currentTimeMillis();
        imgName = time.toString();
        //给富文本返回的url
        urlForRich = "http://192.168.40.72:8080/PClubManager/images/head/" + imgName + ".png";
        coverPicName = imgName;

        sp = getSharedPreferences("loginInfor", MODE_PRIVATE);
        String username = sp.getString("username", "");
        params.put("id", imgName);//照片id，使用时间
        params.put("data", imgStr);
        OkHttp.postAsync(url, params, new OkHttp.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.i("上传失败", "失败" + request.toString() + e.toString());
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.i("上传成功", result);
                //图片上传成功了，发个msg
                Message m = new Message();
                m.what = 1;
                m.obj = urlForRich;
                handler.sendMessage(m);
            }
        });
    }

    public void release(View view){
        //发请求获取全部的部门，不需要传递什么参数就用get方式
        Request request=new Request.Builder()
                .url("http://192.168.40.72:8080/PClubManager/Department_findAllForAndroid")
                .get()
                .build();
        exec(request,1);
        showPopupWindow(view);

    }

    //向后端提交数据
     public void submit()
     {
         String ActivityName = editorHead.getHtml();
        String ActivityContent = mEditor.getHtml();
         String ActivityOrganization = (String) activityHoldOrganization.getSelectedItem();
         String ActivityTime = activityTime.getText().toString();
         String ActivityIntroduction = activityIntroduction.getText().toString();
         String ActivityCover = coverName.getText().toString();

         if(ActivityName == null)
             ActivityName = "空标题";
         if(ActivityContent == null)
             ActivityContent = "空内容";
        //使用post的方式，向后端action发起传输请求
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .add("ActivityName", ActivityName)
                .add("ActivityContent",ActivityContent)
                .add("ActivityOrganization",ActivityOrganization)
                .add("ActivityTime",ActivityTime)
                .add("ActivityIntroduction",ActivityIntroduction)
                .add("ActivityCover","head/"+coverPicName+".png")
                .add("ActivityLocation","计控15号楼")
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://192.168.40.72:8080/PClubManager/Act_addActivityForAndroid")
                .post(formBody)
                .build();
        exec(request,0);
     }

    //将发送request的过程和回调函数的定义封装成一个方法
    private void exec(Request request,int backType) {
        final int back = backType;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("失败：","-----"+e);
                final String error = e.toString();
                ReleaseActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //chatroomContent.setText(error);
                        Log.i("error--------",error);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","-----");
                //从服务器传回来的json字符串
                String msg = response.body().string();
                switch (back)
                {
                    case 0:
                        break;
                    case 1:
                        Message message = new Message();
                        message.what = 2;
                        message.obj = msg;
                        handler.sendMessage(message);
                }
            }
        });
    }



    //显示popupWindow
    private void showPopupWindow(View view){
        //动态加载布局？！！  布局填充器？
        //将弹框的布局加载到View控件中
        //View contentview = LayoutInflater.from(ReleaseActivity.this).inflate(R.layout.submit,null);
        //创建一个弹出Dialog控件   直接在java类里面创建
        Window window = submitActivity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        window.setAttributes(lp);
        submitActivity.show();
    }

    @Event(value = {R.id.release_back},type = View.OnClickListener.class)
    private void myfinish()
    {
        finish();
    }
}
