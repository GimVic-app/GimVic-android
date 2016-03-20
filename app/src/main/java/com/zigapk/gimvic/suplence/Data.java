package com.zigapk.gimvic.suplence;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;

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
            ChosenOptions chosen = Settings.getChosenOptions(context);
            String json = Internet.getTextFromUrl(buildUrl(chosen));
            Data result = new Gson().fromJson(json, Data.class);
            Files.writeToFile("schedule.json", json, context);
            Settings.setScheduleDownloaded(true, context);
            Settings.setLastUpdate(new Date(), context);
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
                final ChosenOptions chosen = Settings.getChosenOptions(context);
                while (Main.textViews[4][7][3] == null || Main.lessons[4][7] == null || Main.menuTvs[4][1] == null) {
                }

                Main.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < days.length; i++) {
                            for (int j = 0; j < days[i].lessons.length; j++) {
                                if (!days[i].lessons[j].isEmpty()) {
                                    Main.lessons[i][j].setVisibility(View.VISIBLE);

                                    if (!chosen.teacherMode) {
                                        Main.textViews[i][j][0].setText(days[i].lessons[j].subjectsStr());
                                        Main.textViews[i][j][1].setText(days[i].lessons[j].teachersStr());
                                        Main.textViews[i][j][2].setText(days[i].lessons[j].classroomsStr());
                                    } else {
                                        Main.textViews[i][j][0].setText(days[i].lessons[j].subjectsStr());
                                        Main.textViews[i][j][1].setText(days[i].lessons[j].classesStr());
                                        Main.textViews[i][j][2].setText(days[i].lessons[j].classroomsStr());
                                        if (days[i].lessons[j].substitution && !days[i].lessons[j].containsTeacher(chosen.teacher) && !days[i].lessons[j].note.contains("UČITELJ")) {
                                            if (days[i].lessons[j].note != "")
                                                days[i].lessons[j].note += "\n";
                                            if (days[i].lessons[j].teachers == null || days[i].lessons[j].teachers.length == 0)
                                                days[i].lessons[j].note += "<b>NI UČITELJA.</b>";
                                            else if (days[i].lessons[j].teachers.length == 1)
                                                days[i].lessons[j].note += "<b>UČITELJ: </b>" + days[i].lessons[j].teachersStr();
                                            else if (days[i].lessons[j].teachers.length == 0)
                                                days[i].lessons[j].note += "<b>UČITELJI: </b>" + days[i].lessons[j].teachersStr();

                                        }
                                    }

                                    if (days[i].lessons[j].note != null && days[i].lessons[j].note != "") {
                                        Main.textViews[i][j][3].setText(Html.fromHtml("<b>OPOMBA:</b> " + days[i].lessons[j].note));
                                        Main.textViews[i][j][3].setVisibility(View.VISIBLE);
                                    } else Main.textViews[i][j][3].setVisibility(View.GONE);

                                    if (days[i].lessons[j].substitution)
                                        Main.lessons[i][j].setCardBackgroundColor(context.getResources().getColor(R.color.green400));
                                    else Main.lessons[i][j].setCardBackgroundColor(Color.WHITE);
                                } else {
                                    Main.lessons[i][j].setVisibility(View.GONE);
                                }
                            }
                            Main.menuTvs[i][0].setText(days[i].snack());
                            Main.menuTvs[i][1].setText(days[i].lunch());
                        }
                    }
                });
            }
        }).start();
    }

    private String buildUrl(ChosenOptions chosen) {
        if (chosen.teacherMode) {
            String result = Configuration.server + "/teacherData?addSubstitutions=" + chosen.addSubstitutions +
                    "&teacher=" + chosen.teacher.replace(" ", "%20") + "&snackType=" + chosen.snack +
                    "&lunchType=" + chosen.lunch;
            return result;
        } else {
            String result = Configuration.server + "/data?addSubstitutions=" + chosen.addSubstitutions +
                    classesToUrl(chosen.classes.toArray(new String[chosen.classes.size()])) + "&snackType=" + chosen.snack +
                    "&lunchType=" + chosen.lunch;
            return result;
        }
    }

    private static String classesToUrl(String[] classes) {
        String result = "";
        for (String strClass : classes) {
            result += "&classes[]=" + strClass;
        }
        return result;
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
    public String note = "";
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
                if (result != "") result += "/";
                result += item;
            }
        }
        return result;
    }

    public boolean containsTeacher(String teacher) {
        if (teachers == null || teachers.length == 0) return false;
        for (String item : teachers) {
            if (item.equals(teacher)) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        if (subjects != null || classrooms != null ||
                teachers != null || classes != null ||
                note != "") return false;
        return true;
    }
}
