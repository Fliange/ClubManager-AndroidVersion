package com.nankai.clubmanager.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nankai.clubmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialFragment extends Fragment {

    private ListView listView;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private int[] imgIds={R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,
            R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,R.drawable.head_image,
            R.drawable.head_image,R.drawable.head_image};
    private String[] name={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.material_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.manage_material_listView);
        String[] keys={"img","content"};
        int[] ids={R.id.item_img_manage_material,R.id.item_content_manage_material};

        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.list_item_material,keys,ids);
        listView.setAdapter(simpleAdapter);

        for(int i=0;i<imgIds.length;i++){
            Map<String,Object> map=new HashMap<>();
            map.put("img",imgIds[i]);
            map.put("content",name[i]);
            lists.add(map);
        }
        return view;
    }
}
