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
public class FragmentTwo extends Fragment {
    static SwipeRefreshLayout mSwipeRefreshLayoutTwo;

    public FragmentTwo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day==6||day==7){
            rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
            TextView tv=(TextView) rootView.findViewById(R.id.text);
            if(day==7){
                //TODO hardcoded string
                tv.setText("Yes, still weekend");
            }else{
                //TODO hardcoded string
                tv.setText("Tomorrow is no school!");
            }

        }else {
            rootView = inflater.inflate(R.layout.main_page2, container, false);
            mSwipeRefreshLayoutTwo = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
            mSwipeRefreshLayoutTwo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ((Main) getActivity()).refresh(1);
                }
            });
            mSwipeRefreshLayoutTwo.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }
        return rootView;
    }
}
