package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static Context context;

    public static View[] dayFragments = new View[5];
    public static View[][] lessons = new View[5][8];
    public static TextView[][][] textViews = new TextView[5][8][3]; //subject, teacher, classroom

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            int position = getArguments().getInt(ARG_SECTION_NUMBER);
            LinearLayout insertLayout = (LinearLayout) rootView.findViewById(R.id.lessons_container);

            LayoutInflater lessonsInflater = LayoutInflater.from(rootView.getContext());

            for (int i = 0; i < 8; i++) {
                LinearLayout lessonToInsert = (LinearLayout) lessonsInflater.inflate(R.layout.card, null, false);
                ((TextView) lessonToInsert.findViewById(R.id.lesson)).setText((i+1)+".");

                lessons[position][i] = lessonToInsert;
                textViews[position][i][0] = ((TextView) lessonToInsert.findViewById(R.id.subject));
                textViews[position][i][1] = ((TextView) lessonToInsert.findViewById(R.id.teacher));
                textViews[position][i][2] = ((TextView) lessonToInsert.findViewById(R.id.classroom));
                insertLayout.addView(lessonToInsert);
            }
            dayFragments[position] = rootView;
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PONEDELJEK";
                case 1:
                    return "TOREK";
                case 2:
                    return "SREDA";
                case 3:
                    return "ČETRTEK";
                case 4:
                    return "PETEK";
            }
            return null;
        }
    }
}
