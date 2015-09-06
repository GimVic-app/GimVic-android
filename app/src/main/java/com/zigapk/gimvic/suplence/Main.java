package com.zigapk.gimvic.suplence;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


public class Main extends Activity implements ActionBar.TabListener {

    //TODO: UPDATE!!!
    public static int currentAppVersionNumber = 38;

    public static Context context;
    public static Boolean mRefreshing = false;
    public static SwipeRefreshLayout mSwipeRefreshLayout1;
    public static SwipeRefreshLayout mSwipeRefreshLayout2;
    public static SwipeRefreshLayout mSwipeRefreshLayout3;
    public static SwipeRefreshLayout mSwipeRefreshLayout4;
    public static SwipeRefreshLayout mSwipeRefreshLayout5;

    //textviews [dan][ura][predmet, profesor, ucilnica, opomba]
    public static TextView[][][] textViews = new TextView[5][9][4];
    public static TextView[][] jedlinikTextViews = new TextView[5][2]; //jedlinikTextViews[x][0] = malica, jedlinikTextViews[x][1] = kosilo
    public static LinearLayout[][] classItems = new LinearLayout[5][9];
    public static ImageView[] checkmarks = new ImageView[5];
    public static String packageName;

    public static boolean isDataRendered = false;


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
    public static View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43A047")));
        bar.setIcon(R.drawable.ic_logo_white);

        bar.setTitle(Html.fromHtml("<font color='#ffffff'>" + getString(R.string.gimvic) + "</font>"));
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        ActionBar actionBar = getActionBar();

        //set context
        context = getApplicationContext();

        if (Settings.getLastAppVersionNumber(context) < 20) {
            Data.clearAllData(context);
        }

        if (Settings.getLastAppVersionNumber(context) < 27) {
            Settings.setUrnikParsed(false, context);
            Settings.setTrueUrnikParsed(false, context);
            Urnik.parseUrnik(context);
        }

        if (Settings.isFirstOpened(context)) {
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setOffscreenPageLimit(5);
            mViewPager.setAdapter(mSectionsPagerAdapter);


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

            packageName = getPackageName();

            initializeContent();

            new Thread() {
                @Override
                public void run() {

                    ExternalData.syncExternalBackup(context, false);
                    Handler handler = new Handler(Looper.getMainLooper());
                    try {
                        Thread.sleep(30);
                    } catch (Exception e) {
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            Data.setRefreshingGuiState(true);
                            Data.renderData(context);
                        }
                    });
                }
            }.start();

            Data.refresh(context, true);

            Settings.setlastAppVersion(currentAppVersionNumber, context);
        }
    }

    private void showAnketa() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();
        c.set(Calendar.YEAR, 2015);
        c.set(Calendar.MONTH, 5);
        c.set(Calendar.DAY_OF_MONTH, 19);
        Date dateSpecified = c.getTime();


        if (!Settings.getAnketa2015Done(getApplicationContext()) && today.before(dateSpecified)) {
            new AlertDialog.Builder(this)
                    .setMessage("Lepo prosimo, da rešite anketo o šolski prehrani do 18. 6. 2015. Hvala za sodelovanje.")
                    .setPositiveButton("Anketa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.1ka.si/a/68175"));
                            startActivity(browserIntent);
                            Settings.setAnketa2015Done(true, getApplicationContext());
                        }
                    })
                    .setNegativeButton("Prekliči", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    }).create().show();
        }
    }

    public void onResume() {
        super.onResume();
        if (isDataRendered) Data.renderData(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (Settings.getAdmin(context)) {
            MenuItem adminSwitch = menu.findItem(R.id.admin_switch);
            adminSwitch.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Main.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.admin_switch) {
            //launch switcher
            startActivity(new Intent(context
                    , SwitcherActivity.class));
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_day, container, false);
            int position = getArguments().getInt(ARG_SECTION_NUMBER);

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            day = day - 1;
            if (day == 0) day = 1;
            if (day > 5) day = day % 5;
            day = day + position - 1;

            if (day > 5) day = day % 5;

            if (day <= 5) {

                setOnRefreshListeners(day, rootView);

                for (int i = 1; i <= 9; i++) {
                    textViews[day - 1][i - 1][0] = (TextView) rootView.findViewById(getResources().getIdentifier("predmet" + i, "id", packageName));
                    textViews[day - 1][i - 1][1] = (TextView) rootView.findViewById(getResources().getIdentifier("profesor" + i, "id", packageName));
                    textViews[day - 1][i - 1][2] = (TextView) rootView.findViewById(getResources().getIdentifier("ucilnica" + i, "id", packageName));
                    textViews[day - 1][i - 1][3] = (TextView) rootView.findViewById(getResources().getIdentifier("opomba" + i, "id", packageName));

                    classItems[day - 1][i - 1] = (LinearLayout) rootView.findViewById(getResources().getIdentifier("ura" + i, "id", packageName));

                    checkmarks[day - 1] = (ImageView) rootView.findViewById(R.id.checkmark);

                    jedlinikTextViews[day - 1][0] = (TextView) rootView.findViewById(R.id.malica);
                    jedlinikTextViews[day - 1][1] = (TextView) rootView.findViewById(R.id.kosilo);

                }
            }
            return rootView;
        }
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
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            day = day - 1;
            if (day == 0) day = 1;
            if (day > 5) day = day % 5;
            day = day + position;

            if (day > 5) day = day % 5;

            switch (day) {
                case 1:
                    return getString(R.string.monday).toUpperCase();
                case 2:
                    return getString(R.string.tuesday).toUpperCase();
                case 3:
                    return getString(R.string.wednesday).toUpperCase();
                case 4:
                    return getString(R.string.thursday).toUpperCase();
                case 5:
                    return getString(R.string.friday).toUpperCase();
                case 6:
                    return getString(R.string.saturday).toUpperCase();
                case 7:
                    return getString(R.string.sunday).toUpperCase();

            }
            return null;
        }
    }

    public void initializeContent() {

        if (Other.holidays()) {
            Data.clearAllData(context);
            startActivity(new Intent(context, SummerActivity.class));
            Main.this.finish();

        } else {
            Data.refresh(context, true);
        }
    }


    //sets onRefreshListeners and color schemes for swipe to refresh
    public static void setOnRefreshListeners(int day, View rootView) {


        if (day == 1) {
            Main.mSwipeRefreshLayout1 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

            Main.mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh(context, true);
                }
            });
            Main.mSwipeRefreshLayout1.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);

        } else if (day == 2) {
            Main.mSwipeRefreshLayout2 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

            Main.mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh(context, true);
                }
            });
            Main.mSwipeRefreshLayout2.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        } else if (day == 3) {
            Main.mSwipeRefreshLayout3 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

            Main.mSwipeRefreshLayout3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh(context, true);
                }
            });
            Main.mSwipeRefreshLayout3.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        } else if (day == 4) {
            Main.mSwipeRefreshLayout4 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

            Main.mSwipeRefreshLayout4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh(context, true);
                }
            });
            Main.mSwipeRefreshLayout4.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        } else if (day == 5) {
            Main.mSwipeRefreshLayout5 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

            Main.mSwipeRefreshLayout5.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Data.refresh(context, true);
                }
            });
            Main.mSwipeRefreshLayout5.setColorScheme(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);
        }
    }

    private float getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return (float) displaymetrics.heightPixels;
    }

}