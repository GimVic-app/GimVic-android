package com.zigapk.gimvic.suplence;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Markus on 30.03.2014.
 */
public  class FragmentThree extends Fragment {
    public static SwipeRefreshLayout mSwipeRefreshLayoutThree;
    public FragmentThree() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day==5||day==6){
            rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
            TextView tv=(TextView) rootView.findViewById(R.id.text);
            if(day==6){
                //TODO hardcoded string
                tv.setText("Still weekend, on Sunday");
            }else{
                //TODO hardcoded string
                tv.setText("Finally  weekend");
            }

        }else {
            rootView = inflater.inflate(R.layout.main_page3, container, false);
            mSwipeRefreshLayoutThree = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
            mSwipeRefreshLayoutThree.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((Main) getActivity()).refresh(1);
                }
            });
            mSwipeRefreshLayoutThree.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }
        return rootView;
    }
}
