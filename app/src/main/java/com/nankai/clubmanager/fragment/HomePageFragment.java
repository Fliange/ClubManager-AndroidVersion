package com.nankai.clubmanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.activity.LoginActivity;
import com.nankai.clubmanager.activity.MyCollectionActivity;
import com.nankai.clubmanager.activity.PasswordUpdateActivity;
import com.nankai.clubmanager.extra.OkHttp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhangjin on 2017/8/5.
 */

public class HomePageFragment extends Fragment{

    private ImageView imageView;
    private TextView changePassword;
    private TextView personalInfo;
    private TextView myDepartments;
    private TextView myCollect;
    private TextView exit;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    private SharedPreferences sp;//专门记录照片的
    private SharedPreferences sp1;//存了登录人的身份信息的

    OkHttpClient okHttpClient = new OkHttpClient();

    private void initView() {
        //从SharedPreferences获取图片
        getBitmapFromSharedPreferences();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homepage_fragment, null);
        changePassword= (TextView) view.findViewById(R.id.modify_password);
        personalInfo=(TextView) view.findViewById(R.id.modify_information);
        myDepartments=(TextView) view.findViewById(R.id.myapartment);
        myCollect=(TextView) view.findViewById(R.id.mycollection);
        exit=(TextView) view.findViewById(R.id.logout);

        sp1=getActivity().getSharedPreferences("loginInfor",MODE_PRIVATE);

        //跳转到收藏的活动的页面
        view.findViewById(R.id.mycollection).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomePageFragment.this.getActivity(),MyCollectionActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //点击头像实现更换头像的功能
        imageView= (ImageView) view.findViewById(R.id.myphoto);
        initView();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(HomePageFragment.this.getActivity());
                String[] msg={"拍照","从相册中选择"};
                builder.setItems(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                // 激活相机
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                // 判断存储卡是否可以用，可用进行存储
                                if (hasSdcard()) {
                                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                                    // 从文件中创建uri
                                    Uri uri = Uri.fromFile(tempFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                }
                                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
                                startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
                                break;
                            case 1:
                                // 激活系统图库，选择一张图片
                                Intent intent1 = new Intent(Intent.ACTION_PICK);
                                intent1.setType("image/*");
                                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                                startActivityForResult(intent1, PHOTO_REQUEST_GALLERY);
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
        return view;
    }
    public void my(View view){
        switch(view.getId()){
            case R.id.modify_password:
                Intent intent=new Intent(HomePageFragment.this.getActivity(),PasswordUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_information:
                break;
            case R.id.myapartment:
                //sp1.getInt("department",0);//这里要把部门的id传给部门首页那个activity
                break;
            case R.id.mycollection:
                break;
            case R.id.logout:
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("status",false);//登录状态改为未登录
                editor.commit();
                Intent intent1=new Intent(HomePageFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent1);//退出当前账号后跳转到登录界面
        }
    }
    /*
* 判断sdcard是否被挂载
*/
    public boolean hasSdcard() {
        //判断ＳＤ卡手否是安装好的　　　media_mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 剪切图片,user指代调用者:0是等永恒原生调用，1是富文本调用
     */
    public void crop(Uri uri,int user) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码根据调用者确定
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri,0);
            }
        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile),0);
            } else {
                Toast.makeText(HomePageFragment.this.getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                /**
                 * 获得图片
                 */
                imageView.setImageBitmap(bitmap);
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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("testSP", MODE_PRIVATE);
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
        String url = "http://192.168.40.70:8080/PClubManager/loadImage";
        Map<String, String> params = new HashMap<String, String>();

        sp=getActivity().getSharedPreferences("loginInfor",MODE_PRIVATE);
        String username = sp.getString("username","");
        params.put("id",username);//因为存照片时需要用到登录人的学号所以一块传过去；
        params.put("data", imgStr);
        params.put("time",imgName);
        OkHttp.postAsync(url, params, new OkHttp.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.i("上传失败", "失败" + request.toString() + e.toString());
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.i("上传成功", result);
            }
        });
    }

    //从SharedPreferences获取图片
    public void getBitmapFromSharedPreferences(){
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("testSP", MODE_PRIVATE);
        //第一步:取出字符串形式的Bitmap
        String imageString=sharedPreferences.getString("image", "");
        //第二步:利用Base64将字符串转换为ByteArrayInputStream
        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
        if(byteArray.length==0){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);

            //第三步:利用ByteArrayInputStream生成Bitmap
            Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);
            imageView.setImageBitmap(bitmap);
        }

    }

}

