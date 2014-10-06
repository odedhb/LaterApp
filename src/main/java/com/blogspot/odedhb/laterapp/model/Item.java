package com.blogspot.odedhb.laterapp.model;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.text.format.DateUtils;
import android.view.View;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.views.DrawView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by oded on 3/18/14.
 */

public class Item {
    public String id;
    public String content;
    public long time;
    public long creationTime;

    public int icon;
    public List<Notification.Action> actions;

    final static boolean safeMode = false;

    public Item(String content) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.content = content;
        //this is default time value:
        time = System.currentTimeMillis();
    }

    public Item(String id, String content) {
        this.id = id;
        this.content = content;
        //this is default time value:
        time = System.currentTimeMillis();
    }

    public boolean isOverdue() {
        if (safeMode) return true;

        if (time < System.currentTimeMillis()) return true;
        return false;
    }

    public boolean isForToday() {
        if (safeMode) return true;

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 23); //anything 0 - 23
        c.set(Calendar.MINUTE, 59);
//        c.set(Calendar.SECOND, 0);

        if (time < c.getTimeInMillis()) return true;
        return false;
    }

    public int hourOfDay() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public int minute() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.get(Calendar.MINUTE);
    }

    public boolean isForNow() {
        if (safeMode) return true;

        if (time < System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS) return true;
        return false;
    }

    @Override
    public String toString() {
        if (safeMode) return "";

        return content;
    }


    public int getLightColor() {
        if (safeMode) return 0xFFFFFF;

//        return content.hashCode();// + 11000000;

        String anyString = content;
        String opacity = "#11"; //opacity between 00-ff
        String hexColor = String.format(
                opacity + "%06X", (0xFFFFFF & anyString.hashCode()));

        return Color.parseColor(hexColor);
    }

    private int getPastelColorByString() {
        if (safeMode) return 0;

        String[] colors = new String[]{
                "#F7977A",
                "#F9AD81",
                "#FDC68A",
                "#FFF79A",
                "#C4DF9B",
                "#A2D39C",
                "#82CA9D",
                "#7BCDC8",
                "#6ECFF6",
                "#7EA7D8",
                "#8493CA",
                "#8882BE",
                "#A187BE",
                "#BC8DBF",
                "#F49AC2",
                "#F6989D"
        };

        int hashCode = content.hashCode();

        int maxInt = 1000000;
        int div = maxInt / hashCode;

        String hex = colors[div];

        return Color.parseColor(hex);


    }

    public ShapeDrawable getIcon() {
        if (safeMode) return null;

        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.getPaint().setColor(getColor());
        circle.getPaint().setStyle(Paint.Style.FILL);
        circle.setIntrinsicHeight(128);
        circle.setIntrinsicWidth(128);

        return circle;
    }

    public Bitmap getIconBitmap() {
        if (safeMode) return null;


        Drawable drawable = getRectIcon();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Drawable getRectIcon() {
        if (safeMode) return null;


        ShapeDrawable circle = new ShapeDrawable(new RectShape());
        circle.getPaint().setColor(getColor());
        circle.getPaint().setStyle(Paint.Style.FILL);
        circle.setIntrinsicHeight(64);
        circle.setIntrinsicWidth(128);

        return circle;
    }

    private Drawable getCircleIcon() {
        if (safeMode) return null;


        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.getPaint().setColor(getColor());
        circle.getPaint().setStyle(Paint.Style.FILL);
        circle.setIntrinsicHeight(64);
        circle.setIntrinsicWidth(64);

        return circle;
    }

    public int getOpaqueColorByAge() {
        if (safeMode) return 0xFFFFFF;

        String opacity = "#ff"; //opacity between 00-ff
        String hexColor = String.format(
                opacity + "%06X", (0xFFFFFF & ((Long) time / DateUtils.SECOND_IN_MILLIS)));

        return Color.parseColor(hexColor);
    }

    public int getColor() {
        if (safeMode) return 0xFFFFFF;
        return calculateColor(content);
    }

    public static int calculateColor(String anyString) {
//        String anyString = content.substring(1,content.length()-1);
        String opacity = "#ff"; //opacity between 00-ff
        String hexColor = String.format(
                opacity + "%06X", (0xeeeeee & anyString.hashCode()));

        return Color.parseColor(hexColor);
    }

    public int getTextColorOld() {
        int red = Color.red(getColor());
        int green = Color.green(getColor());
        int blue = Color.blue(getColor());

        int avg = (red + green + blue) / 3;

        if (avg > 120) {
            return Color.parseColor("#111111");
        }

        return Color.WHITE;
    }

    boolean isBright() {
        int color = getColor();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int avg = (red + red + green + green + green + blue) / 6;
        if (avg > 130) {
            return true;
        }
        return false;
    }

    public int getTextColor() {
//        if (isBright()) return Color.parseColor("#111111");

        return Color.WHITE;
    }

    public int getInvertTextColor() {
        int color = getColor();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.rgb(255 - Color.red(red),
                255 - Color.green(green),
                255 - Color.blue(blue));

    }


    public Drawable getLightIcon() {
        if (safeMode) return null;

        ShapeDrawable circleLight = new ShapeDrawable(new OvalShape());
        circleLight.getPaint().setColor(getLightColor());
        circleLight.getPaint().setStyle(Paint.Style.FILL);
        return circleLight;
    }

    int getContentLength() {
        if (safeMode) return 0;
        return content.length();
    }

    public int getTextSize() {
        if (safeMode) return 0;

        if (getContentLength() > 50) return 16;
        if (getContentLength() > 30) return 18;
        if (getContentLength() > 20) return 20;
        return 24;
    }

    public int getOldnessIndicator() {
        if (safeMode) return 0;

        long delta = time - System.currentTimeMillis();
        int intDelta = (int) (delta / DateUtils.HOUR_IN_MILLIS);
        if (intDelta > 25) intDelta = 25;
        return intDelta;
    }

    public String getFormattedTime() {
        if (safeMode) return "";

        if (time < System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS) {
            return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_TIME);
        } else if (time < System.currentTimeMillis() + DateUtils.WEEK_IN_MILLIS) {
            return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_WEEKDAY);
        } else {
            return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_DATE);
        }

    }

    public String getFullFormattedTime() {
        if (safeMode) return "";

        String timeStr = DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_TIME);

        if (time < System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS) {
            return timeStr;
        } else if (time < System.currentTimeMillis() + DateUtils.WEEK_IN_MILLIS) {
            return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_WEEKDAY)
                    + " - " + timeStr;
        } else {
            return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_DATE)
                    + " - " + timeStr;
        }

    }

    public String getWeekDayTime() {
        if (safeMode) return "";

        return android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_WEEKDAY)
                + " " + android.text.format.DateUtils.formatDateTime(App.getContext(), time, DateUtils.FORMAT_SHOW_TIME);
    }

    public CharSequence getTimeDelta() {
        if (safeMode) return "";

        return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), 60000L, DateUtils.FORMAT_ABBREV_ALL);
    }

    public void addAction(Notification.Action action) {
        if (actions == null) {
            actions = new ArrayList<Notification.Action>();
        }
        actions.add(action);
    }

    public long timeFromNow() {
        return time - System.currentTimeMillis();
    }

    public int daysFromNow() {

        if (safeMode) return 0;

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 23); //anything 0 - 23
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        int daysFromNow = 0;

        long timeToCheck = c.getTimeInMillis();

        while (timeToCheck < time) {
            timeToCheck += DateUtils.DAY_IN_MILLIS;
            daysFromNow++;
        }

        return daysFromNow;

    }

    public int daysInMillisFromNow() {
        return (int) (timeFromNow() / DateUtils.DAY_IN_MILLIS);
    }

    public int minuteOfDay() {
        return hourOfDay() * 60 + minute();
    }

    public View getBgPhoto() {
        DrawView d = new DrawView(App.getContext());
        return d;
    }
}
