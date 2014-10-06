package com.blogspot.odedhb.laterapp.model;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;

/**
 * Created by oded on 3/18/14.
 */
public abstract class Snooze {

//    private String desc;

    public int getIconResource() {
        if (desc().contains("15 minutes")) {
            return R.drawable.fa_angle_double_right;
        } else if (desc().contains("5 minutes")) {
            return R.drawable.fa_angle_right;
        } else if (desc().contains("30 minutes")) {
            return R.drawable.fa_adjust;
        } else if (desc().contains("Done")) {
            return R.drawable.tick;
        }
        return 0;
    }


    public String id() {
        return desc();
    }

    public int iconFA() {
        int stringIcon = R.string.fa_clock_o;

        if (desc().contains("noon")) {
            stringIcon = R.string.fa_cutlery;
        } else if (desc().contains("morn")) {
            stringIcon = R.string.fa_sun_o;
        } else if (desc().contains("eve")) {
            stringIcon = R.string.fa_moon_o;
        } else if (desc().contains("night")) {
            stringIcon = R.string.fa_star_o;
        } else if (desc().contains("days")) {
            stringIcon = R.string.fa_calendar;
        } else if (desc().contains("day")) {
            stringIcon = R.string.fa_calendar_o;
        } else if (desc().contains("15 minutes")) {
            stringIcon = R.string.fa_angle_double_right;
        } else if (desc().contains("5 minutes")) {
            stringIcon = R.string.fa_angle_right;
        } else if (desc().contains("30 minutes")) {
            stringIcon = R.string.fa_adjust;
        } else if (desc().contains("1 hour")) {
            stringIcon = R.string.fa_circle;
        } else if (desc().contains("minutes")) {
            stringIcon = R.string.fa_circle_o;
        }
        return stringIcon;
    }

    String iconEmoji() {
        String stringIcon = "🕑";


        if (desc().contains("noon")) {
            stringIcon = "🍴";
        } else if (desc().contains("morn")) {
            stringIcon = "☀";
        } else if (desc().contains("eve")) {
            stringIcon = "🌃";
        }
        return stringIcon;
    }

    public abstract String desc();

    String shortDesc;

    public String shortDesc() {

        if (shortDesc != null) return shortDesc;

        return desc().replace("in ", "")
                .replace("this ", "")
                .replace("tomorrow", "tomo")
                .replace("sunday", "Sun")
                .replace("monday", "Mon")
                .replace("tuesday", "Tue")
                .replace("wednesday", "Wed")
                .replace("thursday", "Thu")
                .replace("friday", "Fri")
                .replace("saturday", "Sat")
                /*
                .replace("this morning", "morn")
                .replace("this noon", "noon")
                .replace("this evening", "eve")
                .replace("this night", "night")
                */
                .replace("morning", "morn")
                .replace("evening", "eve")

                .replace(" minutes", "m")
                .replace(" hours", "h")
                .replace(" hour", "h")
                .replace(" days", "d")

                ;
    }

    public abstract Boolean run(Item item);

//    public abstract boolean textOnly();

    Long toTimeStamp() {
        return Snoozes.parseTimeFromText(desc());
    }


    @Override
    public String toString() {
        return desc() + " | " + toTimeStamp();
    }


    String veryShortDesc() {
        String adjustedString = shortDesc();
        adjustedString = adjustedString.replace(" noon", "");
        adjustedString = adjustedString.replace(" morn", "");
        adjustedString = adjustedString.replace(" eve", "");
        adjustedString = adjustedString.replace(" night", "");
        return adjustedString;
    }

    public String shortDescIcon() {
//        return iconEmoji() + " " + veryShortDesc();
        return App.getContext().getResources().getString(iconFA()) + " " + veryShortDesc();
//        char smiley = new Character('55357');

//        return "😃";

//        char c = '\55357';

//        return "\u00F6";
//        return "\U00XXYYZZ";
//        return String.valueOf(c);
    }

    public String descIcon() {
        return App.getContext().getResources().getString(iconFA()) + " " + desc();
//        return iconEmoji() + " " + desc();
    }


}
