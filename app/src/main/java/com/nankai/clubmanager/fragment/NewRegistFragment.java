package com.nankai.clubmanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nankai.clubmanager.R;
import com.nankai.clubmanager.dialog.MemberDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRegistFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.new_regist_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.regist_listView);
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
        return  view;
    }
}
