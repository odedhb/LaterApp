package com.blogspot.odedhb.laterapp.listeners;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.StoredNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by oded on 1/16/14.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class TrayNotificationListener extends NotificationListenerService {


    static SharedPreferences storedNotifications(Context context) {
        return context.getSharedPreferences("NOTIFICATIONS_DATA", MODE_PRIVATE);
    }

    static SharedPreferences storedNotificationTimes(Context context) {
        return context.getSharedPreferences("NOTIFICATIONS_TIME", MODE_PRIVATE);
    }

    static List<StoredNotification> getNotifications(int howMany) {
//        return new ArrayList<String>((Collection<? extends String>) storedNotifications(App.getContext()).getAll().values());

        List<StoredNotification> allNotifications = new ArrayList<StoredNotification>();

        Map<String, Long> storedNotificationTimes = (Map<String, Long>) storedNotificationTimes(App.getContext()).getAll();

        for (Map.Entry<String, Long> entry : storedNotificationTimes.entrySet()) {

            StoredNotification storedNotification = new StoredNotification();
            storedNotification.originalId = entry.getKey();
            storedNotification.time = entry.getValue();
            storedNotification.text = storedNotifications(App.getContext()).getString(storedNotification.originalId, null);
            Log.d("notification_text", storedNotification.text);
            allNotifications.add(storedNotification);
        }

        Collections.sort(allNotifications, new Comparator<StoredNotification>() {
            @Override
            public int compare(StoredNotification storedNotification, StoredNotification storedNotification2) {
                return storedNotification2.time.compareTo(storedNotification.time);
            }
        });

        int count = allNotifications.size();
        return allNotifications.subList(0, howMany > count ? count : howMany);
    }

    public static List<Item> getStoredNotificationsAsItems(int howMany) {
        List<Item> items = new ArrayList<Item>();
        for (StoredNotification storedNotification : getNotifications(howMany)) {
            Item item = new Item(storedNotification.originalId, storedNotification.text);
            item.time = storedNotification.time;
            item.icon = R.drawable.notifications_swiped;
            items.add(item);
        }

        return items;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        documentNotification(sbn);
//        logRaw(sbn);
    }

    private void logRaw(StatusBarNotification sbn) {

        Bundle bundle = sbn.getNotification().extras;
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Log.d("josn_bundle", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));

            JSONObject j = new JSONObject();
            try {
                j.put(key, value);
            } catch (JSONException e) {
                Log.e("josn_bundle_error", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;


        if (packageShouldBeIgnored(sbn)) {
            return;
        }

        documentNotification(sbn);
        StoredNotification.suggestSnooze(sbn);
//        logRaw(sbn);
    }

    void documentNotification(StatusBarNotification sbn) {

        if (packageShouldBeIgnored(sbn)) return;

        String appName = getAppLabel(sbn.getPackageName());


        Bundle extras = sbn.getNotification().extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence[] notificationLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
//        CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

        String fullNotificationText = appName + " : " + title;

        if (notificationText != null) {
            fullNotificationText += " : " + notificationText;
        }

        if (notificationLines != null) {
            String lines = "";
            for (CharSequence line : notificationLines) {
                lines += line + ", ";
            }

            fullNotificationText += " : " + lines;
        }

        storedNotifications(getApplicationContext()).edit().putString("" + sbn.getId(), fullNotificationText).commit();
        storedNotificationTimes(getApplicationContext()).edit().putLong("" + sbn.getId(), System.currentTimeMillis()).commit();

        Log.d("notification_test", "notification_test_" + fullNotificationText);
    }

    private boolean packageShouldBeIgnored(StatusBarNotification sbn) {
        for (String pkg : blockedPackages()) {
            if (sbn.getPackageName().equals(pkg)) return true;
        }
        return false;
    }

    private List<String> blockedPackages() {
        ArrayList<String> b = new ArrayList<String>();
        b.add("android");
        b.add(App.getContext().getPackageName());
        return b;
    }


    String getAppLabel(String packageName) {
        PackageManager pm = getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "System");
        return applicationName;
    }

    /*public static List<String> getAllNotificationsAsStrings(int howMany) {

        List<String> strings = new ArrayList<String>();

        for (StoredNotification storedNotification : getNotifications(howMany)) {
            strings.add(storedNotification.text);
        }

        return strings;
    }*/
}
