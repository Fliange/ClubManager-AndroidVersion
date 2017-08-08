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
    private int[] count={100,100,200,100,200,100,200,300,100};
    private String[] name={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};
    private String[] extra={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};  //备注


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.material_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.manage_material_listView);
        String[] keys={"content","counts","extrax"};
        int[] ids={R.id.manage_material_name,R.id.manage_material_count,R.id.manage_materail_extra};

        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.list_item_material,keys,ids);
        listView.setAdapter(simpleAdapter);

        for(int i=0;i<count.length;i++){
            Map<String,Object> map=new HashMap<>();

            map.put("content",name[i]);
            map.put("counts",count[i]);
            map.put("extrax",extra[i]);
            lists.add(map);
        }
        return view;
    }
}
