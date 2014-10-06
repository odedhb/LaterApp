package com.blogspot.odedhb.laterapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.LaterNotifications;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Oded on 3/18/14.
 */
public class Snoozes {
    private final Context context;

    SharedPreferences snoozeStats;
    SharedPreferences snoozeStatsLastUsed;

    public Snoozes(Context context) {
        this.context = context;
        snoozeStats = App.getContext().getSharedPreferences("snooze_stats", Context.MODE_PRIVATE);
        snoozeStatsLastUsed = App.getContext().getSharedPreferences("snooze_stats_last_used", Context.MODE_PRIVATE);
    }

    //possible snooze types:
    //when I have a free calendar
    //when Danielle calls me
    //when the weather is sunny
    //when it's a rainy day
    //when I play candy crush
    //when I'm driving
    //when I get home
    //when I'm near a seven-eleven
    //before I go to sleep, when I'm setting my alarm clock
    //when I plug the phone to the wall / usb
    //when I have wifi
    //when I search something on google / go to a website
    //when i talk to my mom
    //when another/different LaterTask is done

    public List<Snooze> getAll(boolean sortByUsage) {

//        if (App.possibleSnoozesList != null) return App.possibleSnoozesList;


        ArrayList<Snooze> list = new ArrayList<Snooze>();

        List<String> snoozeStrings;

        if (sortByUsage) {
            snoozeStrings = getSnoozeStringsSortedByRecency();
        } else {
            snoozeStrings = getHardCodedSnoozeStrings();
        }


        for (final String usedSnooze : snoozeStrings) {

            final long parsedTimeForSort = parseTimeFromText(usedSnooze);

            if (sortByUsage && parsedTimeForSort < System.currentTimeMillis()) continue;

            list.add(new Snooze() {

                @Override
                public String desc() {
                    return usedSnooze;
                }

                @Override
                public Boolean run(Item item) {
                    if (item == null) return false;

                    long parsedTime = parseTimeFromText(usedSnooze);

                    //adjust time so there will never be 2 tasks at the exact same time
                    long range = DateUtils.MINUTE_IN_MILLIS - 10;
                    if (parsedTime > (System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS * 6)) {
                        range = DateUtils.MINUTE_IN_MILLIS * 10;
                    }
                    Random r = new Random();
                    long number = (long) (r.nextDouble() * range);

                    parsedTime = parsedTime + number;

                    item.time = parsedTime;
                    Items.createOrReplace(item);

                    LaterNotifications.dismissNotification(item);

                    return true;
                }

            });
        }

//        list.add(SnoozeService.specificDate(context));

//        App.possibleSnoozesList = list;

        return list;
    }

    public static long parseTimeFromText(String when) {
        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(when);
        long target = 0;
        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();
            target = dates.get(0).getTime() + 15000;
        }
        return target;
    }


    List<String> getSnoozeStringsSortedByRecency() {

        Map<String, Long> snoozeUses = (Map<String, Long>) snoozeStatsLastUsed.getAll();

        if (snoozeUses.isEmpty()) {
            setDefaultSnoozes();
            snoozeUses = (Map<String, Long>) snoozeStatsLastUsed.getAll();
        }

        final Map<String, Long> finalSnoozeUses = snoozeUses;
        TreeSet<String> sortedSet = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {
                return finalSnoozeUses.get(s2).compareTo(finalSnoozeUses.get(s));
            }
        });

        sortedSet.addAll(finalSnoozeUses.keySet());

        ArrayList<String> list = new ArrayList<String>(sortedSet);

        return list;
    }

    List<String> getSnoozeStringsSortedByPopularity() {

        Map<String, String> snoozeUses = (Map<String, String>) snoozeStats.getAll();

        if (snoozeUses.isEmpty()) {
            setDefaultSnoozes();
            snoozeUses = (Map<String, String>) snoozeStats.getAll();
        }

        final Map<String, Integer> countMap = new HashMap<String, Integer>();

        for (Map.Entry<String, String> entry : snoozeUses.entrySet()) {
            String snoozeText = entry.getValue();
            Integer newCount = countMap.get(snoozeText);
            if (newCount == null) {
                newCount = 1;
            } else {
                newCount++;
            }
            countMap.put(snoozeText, newCount);
        }

        ArrayList<String> list = new ArrayList<String>();
        for (String snooze : countMap.keySet()) {
            list.add(snooze);
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String s2) {
                return countMap.get(s).compareTo(countMap.get(s2));
            }
        });


        return list;
    }

    private void setDefaultSnoozes() {

        List<String> list = new ArrayList<String>();
        list.add("monday morning");
        list.add("saturday noon");
        list.add("tomorrow morning");
        list.add("this evening");
        list.add("in 3 hours");
        list.add("in 15 minutes");

        for (String s : list) {
            snoozeStats.edit().putString(Long.toString(System.currentTimeMillis()), s).commit();
            snoozeStatsLastUsed.edit().putLong(s, System.currentTimeMillis()).commit();
        }
    }

    private static List<String> getHardCodedSnoozeStrings() {

        ArrayList<String> days = new ArrayList<String>();
        days.add("this");
        days.add("tomorrow");
        days.add("sunday");
        days.add("monday");
        days.add("tuesday");
        days.add("wednesday");
        days.add("thursday");
        days.add("friday");
        days.add("saturday");

        ArrayList<String> partsOfDay = new ArrayList<String>();
        partsOfDay.add("morning");
        partsOfDay.add("noon");
        partsOfDay.add("evening");
//        partsOfDay.add("night");


        ArrayList<String> hardcodedList = new ArrayList<String>();
//        hardcodedList.add("now");
        hardcodedList.add("in 5 minutes");
        hardcodedList.add("in 15 minutes");
        hardcodedList.add("in 30 minutes");
        hardcodedList.add("in 1 hour");
        hardcodedList.add("in 2 hours");
        hardcodedList.add("in 3 hours");
        hardcodedList.add("in 4 hours");
        hardcodedList.add("in 5 hours");
        hardcodedList.add("in 6 hours");
        hardcodedList.add("in 8 hours");
        hardcodedList.add("in 10 hours");
        hardcodedList.add("in 12 hours");
        hardcodedList.add("tonight");
        hardcodedList.add("in 18 hours");

        //add all day of week alternatives
        for (String dayName : days) {
            for (String pod : partsOfDay) {
                hardcodedList.add(dayName + " " + pod);
            }
        }

        hardcodedList.add("in 2 days");
        hardcodedList.add("in 3 days");
        hardcodedList.add("weekend");
        hardcodedList.add("in a week");

        return hardcodedList;
    }

    public void countSnooze(Snooze snooze) {
        snoozeStats.edit().putString(Long.toString(System.currentTimeMillis()), snooze.desc()).commit();
        snoozeStatsLastUsed.edit().putLong(snooze.desc(), System.currentTimeMillis()).commit();
    }

    public void resetSnoozeStats() {
        snoozeStats.edit().clear().commit();
        snoozeStatsLastUsed.edit().clear().commit();
    }

    public List<Snooze> sortForDisplay(List<Snooze> snoozes) {

        Collections.sort(snoozes, new Comparator<Snooze>() {
            @Override
            public int compare(Snooze snooze, Snooze snooze2) {
                return snooze.toTimeStamp().compareTo(snooze2.toTimeStamp());
            }
        });

        return snoozes;
    }

    public static String getRandomSnooze() {
        Random r = new Random();
        int min = 0;//inclusive
        int max = 4;//exclusive
        int randInt = r.nextInt(max - min) + min;
        return getHardCodedSnoozeStrings().get(randInt);
    }
}
