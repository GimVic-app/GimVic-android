package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ziga on 26/02/15.
 */
public class Jedilnik {

    public static boolean jedilnikDownloading = false;
    private static int downloadedCounter = 0;

    public static void render(final Context context) {
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day = day - 1;
        if (day == 0) day = 7;

        for (int i = 0; i < 6; i++) {
            int sum = i + day;

            if (sum != 6 && sum != 7) {

                int temp;
                if (sum > 7) {
                    temp = sum % 7;
                } else {
                    temp = sum;
                }

                final int tempI = temp;

                final Malica malica = getMalicaForDate(date, context);

                if (malica != null) {
                    if (Settings.getMalicaMode(context) == JedilnikModes.MALICA_NAVADNA) {
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    Main.jedlinikTextViews[tempI - 1][0].setText(convertLinesToString(malica.Navadna, context));
                                }catch (Exception e){}
                            }
                        });
                    } else if (Settings.getMalicaMode(context) == JedilnikModes.MALICA_VEGSPERUTNINO) {
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    Main.jedlinikTextViews[tempI - 1][0].setText(convertLinesToString(malica.VegSPerutnino, context));
                                }catch (Exception e){}
                            }
                        });
                    } else if (Settings.getMalicaMode(context) == JedilnikModes.MALICA_VEGETARIJANSKA) {
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    Main.jedlinikTextViews[tempI - 1][0].setText(convertLinesToString(malica.Vegetarijanska, context));
                                }catch (Exception e){}
                            }
                        });
                    } else if (Settings.getMalicaMode(context) == JedilnikModes.MALICA_SADNOZELENJAVNA){
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    Main.jedlinikTextViews[tempI - 1][0].setText(convertLinesToString(malica.SadnoZelenjavna, context));
                                }catch (Exception e){}
                            }
                        });
                    }
                }

                final Kosilo kosilo = getKosiloForDate(date, context);

                if (kosilo != null) {
                    if (Settings.getKosiloMode(context) == JedilnikModes.KOSILO_NAVADNO) {
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Main.jedlinikTextViews[tempI - 1][1].setText(convertLinesToString(kosilo.Navadno, context));
                            }
                        });
                    } else {
                        //run on ui thread
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Main.jedlinikTextViews[tempI - 1][1].setText(convertLinesToString(kosilo.Vegetarijansko, context));
                            }
                        });
                    }
                }
            }
            date = Other.plus1Day(date);
        }
    }

    private static String convertLinesToString(String[] lines, final Context context) {
        String result = "";
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                result += lines[i];
                if (i + 1 != lines.length) result += "\n";
            }
        } else {
            return context.getResources().getString(R.string.no_data);
        }
        return result;
    }

    private static Malica getMalicaForDate(final Date date, final Context context) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String dateString = df.format(date);

        String file = Files.getFileValue("malica_" + dateString + ".json", context);
        try {
            if (file != null && file.contains("{")) return new Gson().fromJson(file, Malica.class);
            else return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Kosilo getKosiloForDate(final Date date, final Context context) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final String dateString = df.format(date);

        String file = Files.getFileValue("kosilo_" + dateString + ".json", context);
        try {
            if (file != null && file.contains("{")) return new Gson().fromJson(file, Kosilo.class);
            else return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static void download(final Context context, final int days) {
        jedilnikDownloading = true;

        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < days; i++) {
            final String dateString = df.format(date);

            new Thread() {
                @Override
                public void run() {

                    String tempDateString = dateString;
                    String url = "http://app.gimvic.org/APIv2/jedilnikAPI/getJedilnikForDate.php?date=" + tempDateString + "&type=malica";
                    String json = Internet.getTextFromUrl(url);
                    Files.writeToFile("malica_" + tempDateString + ".json", json, context);
                    downloadedCounter++;
                    if (downloadedCounter == (2 * days)) {
                        jedilnikDownloading = false;
                        downloadedCounter = 0;
                    }

                }
            }.start();

            new Thread() {
                @Override
                public void run() {
                    String tempDateString = dateString;
                    String url = "http://app.gimvic.org/APIv2/jedilnikAPI/getJedilnikForDate.php?date=" + tempDateString + "&type=kosilo";
                    String json = Internet.getTextFromUrl(url);
                    Files.writeToFile("kosilo_" + tempDateString + ".json", json, context);
                    downloadedCounter++;
                    if (downloadedCounter == (2 * days)) {
                        jedilnikDownloading = false;
                        downloadedCounter = 0;
                    }
                }
            }.start();

            date = Other.plus1Day(date);

        }
    }
}

class Malica {
    String[] Navadna;
    String[] VegSPerutnino;
    String[] Vegetarijanska;
    String[] SadnoZelenjavna;
}

class Kosilo {
    String[] Navadno;
    String[] Vegetarijansko;
}

