package com.zigapk.gimvic.suplence;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public static Context context;

    public static final int CURRENT_APP_VERSION = 48;
    public static final int ALARM_UNIQUE_ID = 2358;

    public static Activity activity;
    public static View[] dayFragments = new View[5];
    public static CardView[][] lessons = new CardView[5][8];
    public static TextView[][][] textViews = new TextView[5][8][4]; //subject, teacher, classroom
    public static TextView[][] menuTvs = new TextView[5][2];
    public static TextView[] lastUpdateTvs = new TextView[5];
    public static SwipeRefreshLayout[] swipeRefreshLayouts = new SwipeRefreshLayout[5];
    private static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        activity = this;

        if (Settings.getLastAppVersionNumber(context) < 48)
            Settings.clearAllData(context);
        Settings.setLastAppVersionNumber(CURRENT_APP_VERSION, context);

        if (!Settings.isDataConfigured(context)) {
            startActivity(new Intent(this, FirstActivity.class));
            finish();
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setOffscreenPageLimit(5);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(mViewPager);

            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
            if (day < 0 || day > 4) day = 0;
            mViewPager.setCurrentItem(day);

            //(re)sets the alarm
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setAlarm();
                }
            }).start();

            /*AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);*/
        }
    }

    @Override
    public void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (context == null){}
                Looper.prepare();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (Settings.isDataConfigured(context)) {
                            boolean alreadyRefreshed = false;
                            if (Settings.isScheduleDownloaded(context)) {
                                Data data = new Data().fromFile(context);
                                if (data.isValid()) data.render(context);
                                else {
                                    if (Internet.isOnline(context)) refresh(true);
                                    else showDataOutOfDateDialog(false);
                                }
                            }
                            if (!alreadyRefreshed && Internet.isOnline(context)) refresh(false);
                        }
                        refreshLastUpdate();
                    }
                });
            }
        }).start();
        super.onResume();
    }

    private static void refreshLastUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (lastUpdateTvs[4] == null) {
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Date lastUpdate = Settings.getLastUpdate(context);
                        SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy HH:mm");

                        for (TextView tv : lastUpdateTvs) {
                            tv.setText(context.getResources().getString(R.string.last_update) + " " + format.format(lastUpdate));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(Settings.getAdmin(context));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (Internet.isOnline(context))
                startActivity(new Intent(Main.this, SettingsActivity.class));
            else showNoInternetForSettingsDialog();
            return true;
        } else if (id == R.id.action_search) {
            if (Internet.isOnline(context))
                startActivity(new Intent(Main.this, ClassChooserActivity.class));
            else showNoInternetForSettingsDialog();
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
                ((TextView) lessonToInsert.findViewById(R.id.lesson)).setText((i + 1) + ".");

                lessons[position][i] = (CardView) lessonToInsert.findViewById(R.id.card);
                textViews[position][i][0] = ((TextView) lessonToInsert.findViewById(R.id.subject));
                textViews[position][i][1] = ((TextView) lessonToInsert.findViewById(R.id.teacher));
                textViews[position][i][2] = ((TextView) lessonToInsert.findViewById(R.id.classroom));
                textViews[position][i][3] = ((TextView) lessonToInsert.findViewById(R.id.note));
                insertLayout.addView(lessonToInsert);
            }

            dayFragments[position] = rootView;
            menuTvs[position][0] = (TextView) rootView.findViewById(R.id.snack);
            menuTvs[position][1] = (TextView) rootView.findViewById(R.id.lunch);
            lastUpdateTvs[position] = (TextView) rootView.findViewById(R.id.lastUpdate);

            swipeRefreshLayouts[position] = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
            swipeRefreshLayouts[position].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Main.refresh(false);
                }
            });

            swipeRefreshLayouts[position].setColorSchemeColors(R.color.greenOne,
                    R.color.greenTwo,
                    R.color.greenThree,
                    R.color.greenFour);

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

    private static void refresh(final boolean dataOutOfDate) {
        setRefreshingGuiState(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Data newData = new Data().download(context);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            newData.render(context);
                            setRefreshingGuiState(false);
                            refreshLastUpdate();
                        }
                    });
                } catch (CouldNotReachServerException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            showCouldNotReachServerDialog(!dataOutOfDate);
                            setRefreshingGuiState(false);
                            refreshLastUpdate();
                        }
                    });
                }
            }
        }).start();
    }

    public static void showNoInternetForSettingsDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.could_not_reach_server_dialog_title)
                .setMessage(R.string.could_not_reach_server_for_settings_dialog_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        dialog.show();
    }

    public static void showCouldNotReachServerDialog(final boolean canclable) {
        DialogInterface.OnClickListener negativeListener;
        if (canclable) {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        } else {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            };
        }

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.could_not_reach_server_dialog_title)
                .setMessage(R.string.could_not_reach_server_dialog_message)
                .setCancelable(canclable)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refresh(!canclable);
                    }
                })
                .setNegativeButton(R.string.cancel, negativeListener)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        dialog.show();
    }

    public static void showDataOutOfDateDialog(final boolean canclable) {
        DialogInterface.OnClickListener negativeListener;
        if (canclable) {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        } else {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            };
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.data_out_of_date_dialog_title)
                .setMessage(R.string.data_out_of_date_dialog_message)
                .setCancelable(canclable)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refresh(!canclable);
                    }
                })
                .setNegativeButton(R.string.cancel, negativeListener)
                .create()
                .show();
    }

    //can be set again and will only reset original alarm
    private void setAlarm() {
        Intent intent = new Intent(this, NetworkReceiver.class);
        PendingIntent pi = PendingIntent.getActivity(this, ALARM_UNIQUE_ID, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 600 * 1000, pi);
    }


    private static void setRefreshingGuiState(final boolean state) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (swipeRefreshLayouts[4] == null) {
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (SwipeRefreshLayout current : swipeRefreshLayouts) {
                            current.setRefreshing(state);
                        }
                    }
                });
            }
        }).start();
    }
}
