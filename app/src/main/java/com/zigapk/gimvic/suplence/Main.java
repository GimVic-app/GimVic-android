package com.zigapk.gimvic.suplence;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class Main extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    public static Boolean mRefreshing = false;
    public static SwipeRefreshLayout mSwipeRefreshLayout1;
    public static SwipeRefreshLayout mSwipeRefreshLayout2;
    public static SwipeRefreshLayout mSwipeRefreshLayout3;
    public static SwipeRefreshLayout mSwipeRefreshLayout4;
    public static SwipeRefreshLayout mSwipeRefreshLayout5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        ActionBar actionBar = getActionBar();


        actionBar.setBackgroundDrawable(new ColorDrawable(0x259b24));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //scrool to today's tab
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2;
        mViewPager.setCurrentItem(dayOfWeek, true);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 7 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Calendar calendar = Calendar.getInstance();

            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (position) {
                case 0:
                    return getString(R.string.monday).toUpperCase();
                case 1:
                    return getString(R.string.tuesday).toUpperCase();
                case 2:
                    return getString(R.string.wednesday).toUpperCase();
                case 3:
                    return getString(R.string.thursday).toUpperCase();
                case 4:
                    return getString(R.string.friday).toUpperCase();
                case 5:
                    return getString(R.string.saturday).toUpperCase();
                case 6:
                    return getString(R.string.sunday).toUpperCase();

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_day1, container, false);

            int position = getArguments().getInt(ARG_SECTION_NUMBER);

            if(position==2){
                rootView = inflater.inflate(R.layout.fragment_day2, container, false);
            }else if(position==3){
                rootView = inflater.inflate(R.layout.fragment_day3, container, false);
            }else if(position==4){
                rootView = inflater.inflate(R.layout.fragment_day4, container, false);
            }else if(position==5){
                rootView = inflater.inflate(R.layout.fragment_day5, container, false);
            }else if(position==6){
                rootView = inflater.inflate(R.layout.fragment_day6, container, false);
            }else if(position==7){
                rootView = inflater.inflate(R.layout.fragment_day7, container, false);
            }

            setOnRefreshListeners(position, rootView);

            return rootView;
        }

    }


    //sets onRefreshListeners and color schemes for swipe to refresh
    public static void setOnRefreshListeners(int position, View rootView){


        if(position==1){
            Main.mSwipeRefreshLayout1 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container1);

            Main.mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh();
                }
            });
            Main.mSwipeRefreshLayout1.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);

        }else if(position==2){
            Main.mSwipeRefreshLayout2 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container2);

            Main.mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh();
                }
            });
            Main.mSwipeRefreshLayout2.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }else if(position==3){
            Main.mSwipeRefreshLayout3 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container3);

            Main.mSwipeRefreshLayout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh();
                }
            });
            Main.mSwipeRefreshLayout3.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }else if(position==4){
            Main.mSwipeRefreshLayout4 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container4);

            Main.mSwipeRefreshLayout4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh();
                }
            });
            Main.mSwipeRefreshLayout4.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }else if(position==5){
            Main.mSwipeRefreshLayout5 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container5);

            Main.mSwipeRefreshLayout5.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh();
                }
            });
            Main.mSwipeRefreshLayout5.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }

    }


}
