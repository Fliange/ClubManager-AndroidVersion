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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFragment extends Fragment {
    private ListView listView;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private int[] photo={R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,
            R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,R.drawable.test_pic,
            R.drawable.test_pic};  //照片

    private String[] detail={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};  //详情

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.activity_listview);
        String[] keys={"photos","details"};
        int[] ids={R.id.item_img,R.id.item_text};

        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.activity_manage_list_item,keys,ids);
        listView.setAdapter(simpleAdapter);

        for(int i=0;i<photo.length;i++){
            Map<String,Object> map=new HashMap<>();

            map.put("photos",photo[i]);
            map.put("details",detail[i]);
            lists.add(map);
        }
        return view;
    }
}
