package com.nankai.clubmanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.nankai.clubmanager.R;

/**
 * Created by zhangjin on 2017/8/5.
 */

public class RegistViewFragment extends Fragment{

    private TextView choosedate;
    private TextView choosebirthday;
    private DatePicker datetoday;
    private DatePicker birthday;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.regist_fragment, null);
        choosedate = (TextView) view.findViewById(R.id.choosedate);
        choosebirthday = (TextView) view.findViewById(R.id.choosebirthday);
        datetoday = (DatePicker) view.findViewById(R.id.date);
        birthday = (DatePicker) view.findViewById(R.id.birthday);

        choosedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetoday.setVisibility(View.VISIBLE);
                datetoday.init(2017, 8, 06, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int year1 = datetoday.getYear();
                        int day = datetoday.getDayOfMonth();
                        int month = datetoday.getMonth();
                        choosedate.setText(year1+"年"+(month+1)+"月"+day+"日");
                    }
                });
            }
        });
        choosebirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthday.setVisibility(View.VISIBLE);
                birthday.init(2017, 8, 06, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int year2 = birthday.getYear();
                        int day1 = birthday.getDayOfMonth();
                        int month1 = birthday.getMonth();
                        choosebirthday.setText(year2+"/"+(month1+1)+"/"+day1);
                    }
                });
            }
        });
        return view;
    }
}
