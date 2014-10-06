package com.blogspot.odedhb.laterapp.receivers;

import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oded on 5/8/14.
 */
public class UserCalendarReceiver {

    static String ALREADY_IMPORTED_CALENDAR = "ALREADY_IMPORTED_CALENDAR";
    static SharedPreferences CALENDAR_PREFS = App.getContext()
            .getSharedPreferences("CALENDAR_PREFS", App.MODE_PRIVATE);

    public static List<Item> getCalendarEventsAsItems() {
        List<Item> items = new ArrayList<Item>();


        long startDate = System.currentTimeMillis();
        long endDate = System.currentTimeMillis() + DateUtils.WEEK_IN_MILLIS;
        String[] columns = new String[]{CalendarContract.Instances.TITLE,
                CalendarContract.Instances.EVENT_LOCATION,
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END};

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
                .buildUpon();
        ContentUris.appendId(eventsUriBuilder, startDate);
        ContentUris.appendId(eventsUriBuilder, endDate);
        Uri eventsUri = eventsUriBuilder.build();
        Cursor cur = null;
        cur = App.getContext().getContentResolver().query(eventsUri, columns, null, null, CalendarContract.Instances.BEGIN + " ASC");

        while (cur.moveToNext()) {
            String location = cur.getString(cur.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION));
            String name = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));
            Long date = cur.getLong(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
            String id = String.valueOf(cur.getLong(cur.getColumnIndex(CalendarContract.Instances.EVENT_ID)));

            if (name == null) continue;

            if (name.contains(location)) {
                location = null;
            }
            Item item = new Item(id, name);
            item.time = date - DateUtils.HOUR_IN_MILLIS;
            items.add(item);

        }

        cur.close();

        return items;
    }

    public static void importFromCalendarOnlyOnce() {
        boolean alreadyImportedCalendar = CALENDAR_PREFS.getBoolean(ALREADY_IMPORTED_CALENDAR, false);

        if (!alreadyImportedCalendar) {
            CALENDAR_PREFS.edit().putBoolean(
                    ALREADY_IMPORTED_CALENDAR,
                    true).commit();
            for (Item item : getCalendarEventsAsItems()) {
                Items.createOrReplace(item);
            }
        }

    }
}
