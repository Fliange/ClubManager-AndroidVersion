package com.nankai.clubmanager.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.dialog.MemberDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberFragment extends Fragment {

    private ListView listView;
    private View currentView;
    private int currentPosition;
    MemberDialog memberDialog;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private int[] imgIds={R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,
            R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,
            R.drawable.head_image,R.drawable.head_image};
    private String[] name={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.member_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.manage_listView);
        String[] keys={"img","content"};
        int[] ids={R.id.item_img_manage,R.id.item_content_manage};

        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.list_item,keys,ids);
        listView.setAdapter(simpleAdapter);

        for(int i=0;i<imgIds.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("img",imgIds[i]);
            map.put("content",name[i]);
            lists.add(map);
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                memberDialog=new MemberDialog(getActivity(),R.style.MemberDialogStyle);
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

                return false;
            }
        });
        return view;
    }
}
