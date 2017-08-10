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

/**
 * Created by zhangjin on 2017/8/5.
 */

public class FoundViewFragment extends Fragment{
    private ListView listView;
    private List<Map<String,Object>> lists=new ArrayList<>();
    private String content="这里是发布的公告,邓永恒大佬说太丑，所以也多一点的字，看能不能更漂亮一点但是一个公告，差不多也就这么多字了吧。。。";  //显示的内容

    private String[] issuer={"张三","张三","张三",
            "张三","张三","张三","张三","张三","张三"};  //发布者的名字
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.found_fragment, container,false);
        listView = (ListView)view.findViewById(R.id.found_listView);
        String[] keys={"contents","issuers"};
        int[] ids={R.id.issue_content,R.id.issuer};

        SimpleAdapter simpleAdapter=new SimpleAdapter(getActivity(),lists,R.layout.issue_tips,keys,ids);
        listView.setAdapter(simpleAdapter);

        for(int i=0;i<issuer.length;i++){
            Map<String,Object> map=new HashMap<>();

            map.put("contents",content+i);
            map.put("issuers",issuer[i]);
            lists.add(map);
        }
        return view;
    }
}
