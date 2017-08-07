package com.nankai.clubmanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nankai.clubmanager.R;

public class MemberDialog extends Dialog implements View.OnClickListener {

    Context context;
    IOnClickListener iOnclicklistener;
    private TextView left, right;

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
    }

    @Override
    public void onClick(View v) {

        iOnclicklistener = (IOnClickListener) context;
        switch (v.getId()) {
            case R.id.left:
                iOnclicklistener.leftOnClick();
                break;
            case R.id.right:
                iOnclicklistener.rightOnClick();
                break;
            default:
                break;
        }

    }
    public interface IOnClickListener {
        public void leftOnClick();

        public void rightOnClick();

    }
}
