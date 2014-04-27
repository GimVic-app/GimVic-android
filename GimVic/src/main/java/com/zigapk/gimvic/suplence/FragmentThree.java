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

        Calendar cal = Calendar.getInstance();


        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        month++;
        Integer day_2 = cal.get(Calendar.DAY_OF_MONTH);
        day_2++;

        if (!methods.isValidDate(month.toString() + "/" + day_2.toString() + "/" + year.toString())) {
            if (month == 12) {
                month = 1;
                year++;

            } else {
                month++;
            }
            day_2 = 1;

        }
        day_2++;
        if (!methods.isValidDate(month.toString() + "/" + day_2.toString() + "/" + year.toString())) {
            if (month == 12) {
                month = 1;
                year++;

            } else {
                month++;
            }
            day_2 = 1;

        }
        String thirddate = year.toString() + "-" + month.toString() + "-" + day_2.toString();

        String tag = methods.getTagForDay(Main.tags, thirddate);


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(tag.equals("pr")){
            rootView = inflater.inflate(R.layout.fragment_feast, container, false);
            TextView tv=(TextView) rootView.findViewById(R.id.text);
            tv.setText(methods.getTextForDay(Main.tags, thirddate));

        }else if(tag.equals("poc")){
            rootView = inflater.inflate(R.layout.fragment_holidays, container, false);
        }else {
            if(day==5||day==6){

                if(day==6){
                    rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
                    TextView tv=(TextView) rootView.findViewById(R.id.text);
                    //TODO hardcoded string
                    tv.setText("Še vedno nedelja");
                }else{



                    if(tag.equals("ds")){
                        rootView = inflater.inflate(R.layout.fragment_working_saturday, container, false);
                    }else {
                        //TODO hardcoded string
                        rootView = inflater.inflate(R.layout.fragment_weekend, container, false);
                        TextView tv=(TextView) rootView.findViewById(R.id.text);
                        tv.setText("Končno konec tedna");
                    }
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

        }

        return rootView;
    }
}
