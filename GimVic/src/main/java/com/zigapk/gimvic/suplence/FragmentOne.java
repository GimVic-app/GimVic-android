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
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day==7||day==1){
            rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
            TextView tv=(TextView) rootView.findViewById(R.id.text);
            if(day==1){
                //TODO hardcoded string
                tv.setText("Yea, it's Sunday");
            }else{
                //TODO hardcoded string
                tv.setText("Yea, it's Saturday");
            }

        }else {
            rootView = inflater.inflate(R.layout.main_page1, container, false);
            mSwipeRefreshLayoutOne = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
            mSwipeRefreshLayoutOne.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((Main)getActivity()).refresh(1);
                }
            });
            mSwipeRefreshLayoutOne.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }


        return rootView;
    }
}


