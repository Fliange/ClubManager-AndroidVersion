package com.nankai.clubmanager.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nankai.clubmanager.R;


public class SubmitActivity extends Dialog implements android.view.View.OnClickListener{
    private Context context;
    private View customView;
    private Spinner activityHoldOrganization;
    private TextView activityConfirmButton;
    private TextView activityCancelButton;
    private EditText activityTime;
    private EditText activityIntroduction;

    //实现了一个LeaveMyDialogListener接口，用来实现onclick的点击事件
    private LeaveMyDialogListener listener;
    public interface LeaveMyDialogListener{
        public void onClick(View view);
    }

    //构造函数
    public SubmitActivity(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    public SubmitActivity(Context context, int theme){
        super(context, theme);
        this.context = context;
        LayoutInflater inflater= LayoutInflater.from(context);
        customView = inflater.inflate(R.layout.submit, null);
    }
    public SubmitActivity(Context context,int theme,LeaveMyDialogListener listener) {
        super(context,theme);
        this.context = context;
        this.listener = listener;
        LayoutInflater inflater= LayoutInflater.from(context);
        customView = inflater.inflate(R.layout.submit, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(customView);

        //对组件进行注解
        activityHoldOrganization = (Spinner) findViewById(R.id.activity_hold_organization);
        activityConfirmButton = (TextView) findViewById(R.id.activity_confirmButton);
        activityCancelButton = (TextView) findViewById(R.id.activity_cancelButton);
        activityTime = (EditText) findViewById(R.id.activity_time);
        activityIntroduction = (EditText) findViewById(R.id.activity_introduction);

        //按钮监听
        activityConfirmButton.setOnClickListener(this);
        activityCancelButton.setOnClickListener(this);
        /*//全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.submit);*/
    }
    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }
    public View getCustomView() {
        return customView;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
}
