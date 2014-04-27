package com.zigapk.gimvic.suplence;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class FragmentOne extends Fragment {
    static SwipeRefreshLayout mSwipeRefreshLayoutOne;

    public FragmentOne() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;

        Calendar cal = Calendar.getInstance();


        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        month++;
        Integer day_2 = cal.get(Calendar.DAY_OF_MONTH);


        String firstdate = year.toString() + "-" + month.toString() + "-" + day_2.toString();


        String tag = methods.getTagForDay(Main.tags, firstdate);


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(tag.equals("pr")){
            rootView = inflater.inflate(R.layout.fragment_feast, container, false);
            TextView tv=(TextView) rootView.findViewById(R.id.text);
            tv.setText(methods.getTextForDay(Main.tags, firstdate));

        }else if(tag.equals("poc")){
            rootView = inflater.inflate(R.layout.fragment_holidays, container, false);
        }else{
            if(day==7||day==1){

                if(day==1){
                    //TODO hardcoded string
                    rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
                    TextView tv=(TextView) rootView.findViewById(R.id.text);
                    tv.setText("Nedelja");
                }else{

                    if(tag.equals("ds")){
                        rootView = inflater.inflate(R.layout.fragment_working_saturday, container, false);
                    }else {
                        //TODO hardcoded string
                        rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
                        TextView tv=(TextView) rootView.findViewById(R.id.text);
                        tv.setText("Sobota");
                    }
                }

            }else {
                rootView = inflater.inflate(R.layout.main_page1, container, false);
                mSwipeRefreshLayoutOne = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
                mSwipeRefreshLayoutOne.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ((Main) getActivity()).refresh(1);
                    }
                });
                mSwipeRefreshLayoutOne.setColorScheme(R.color.greenOne,
                        R.color.greenTwo,
                        R.color.greenThree,
                        R.color.greenFour);
            }
        }



        return rootView;
    }
}


