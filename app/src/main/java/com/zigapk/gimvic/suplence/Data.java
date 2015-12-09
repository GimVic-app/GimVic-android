package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.zigapk.gimvic.suplence.exceptions.CouldNotReachServerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zigapk on 8.12.2015.
 */
public class Data {
    public Day[] days;
    public String validUntil;

    public boolean isValid() {
        try {
            Date valid = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(validUntil + " 00:00:00");
            return (new Date()).before(valid);
        } catch (ParseException e) {
            return false;
        }
    }

    public Data download(Context context) throws CouldNotReachServerException {
        try {
            String json = Internet.getTextFromUrl(buildUrl(context));
            Data result = new Gson().fromJson(json, Data.class);
            Files.writeToFile("schedule.json", json, context);
            return result;
        } catch (Exception e) {
            throw new CouldNotReachServerException();
        }
    }

    public Data fromFile(Context context) {
        String json = Files.getFileValue("schedule.json", context);
        return new Gson().fromJson(json, Data.class);
    }

    public void render(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Main.textViews[4][7][2] == null || Main.lessons[4][7] == null || Main.menuTvs[4][1] == null) {
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < days.length; i++) {
                            for (int j = 0; j < days[i].lessons.length; j++) {
                                Main.textViews[i][j][0].setText(days[i].lessons[j].subjectsStr());
                                Main.textViews[i][j][1].setText(days[i].lessons[j].teachersStr());
                                Main.textViews[i][j][2].setText(days[i].lessons[j].classroomsStr());

                                if (days[i].lessons[j].substitution)
                                    Main.lessons[i][j].setCardBackgroundColor(context.getResources().getColor(R.color.green400));
                                else Main.lessons[i][j].setCardBackgroundColor(Color.WHITE);
                            }
                            Main.menuTvs[i][0].setText(days[i].snack());
                            Main.menuTvs[i][1].setText(days[i].lunch());
                        }
                    }
                });
            }
        }).start();
    }

    private String buildUrl(Context context) {
        return "http://192.168.1.107:8080/data?addSubstitutions=true&classes[]=3B&classes[]=3GEO1&snackType=navadna&lunchType=navadno";
    }
}

class Day {
    public Lesson[] lessons;
    public String[] snackLines;
    public String[] lunchLines;

    public String snack() {
        if (snackLines == null || snackLines.length == 0) return "/";
        String result = "";
        for (String line : snackLines) {
            if (line != "") {
                if (result != "") result += "\n";
                result += line;
            }
        }
        if (result == "") return "/";
        return result;
    }

    public String lunch() {
        if (lunchLines == null || lunchLines.length == 0) return "/";
        String result = "";
        for (String line : lunchLines) {
            if (line != "") {
                if (result != "") result += "\n";
                result += line;
            }
        }
        if (result == "") return "/";
        return result;
    }
}

class Lesson {
    public String[] subjects;
    public String[] teachers;
    public String[] classrooms;
    public String[] classes;
    public boolean substitution;

    public String subjectsStr() {
        return arrToStr(subjects);
    }

    public String teachersStr() {
        return arrToStr(teachers);
    }

    public String classroomsStr() {
        return arrToStr(classrooms);
    }

    public String classesStr() {
        return arrToStr(classes);
    }

    private static String arrToStr(String[] arr) {
        if (arr == null || arr.length == 0) return "/";
        String result = "";
        for (String item : arr) {
            if (item != "") {
                if (result != "") result += " / ";
                result += item;
            }
        }
        return result;
    }
}
