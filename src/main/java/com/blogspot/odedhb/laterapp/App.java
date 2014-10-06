package com.blogspot.odedhb.laterapp;

import android.content.Context;
import android.graphics.Typeface;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class App extends android.app.Application {

    private static App instance;

    public static MainActivity mainActivity;


    static LaterNotifications laterNotifications;
    static Timer timer;
    static TimerTask task;

    private static Map<String, StatusBarNotification> pendingIntentMap;
    private static Map<String, StatusBarNotification> itemsIntentsMap;

    public App() {
        instance = this;
    }

    public static void refreshItemViews() {
        if (mainActivity == null) return;
        mainActivity.refreshItemsViews();
    }

    public static void resetSnoozeButtons() {
        mainActivity.resetSnoozeButtons();
    }

    public static Context getContext() {
        return instance;
    }

    public static Map<String, StatusBarNotification> notificationWithIntentsMap() {
        if (pendingIntentMap == null) {
            pendingIntentMap = new HashMap<String, StatusBarNotification>();
        }
        return pendingIntentMap;
    }
    public static Map<String, StatusBarNotification> itemsIntentsMap() {
        if (itemsIntentsMap == null) {
            itemsIntentsMap = new HashMap<String, StatusBarNotification>();
        }
        Log.d("itemsIntentsMap",itemsIntentsMap.entrySet().toString());
        return itemsIntentsMap;
    }

    public static Typeface fontAwesome() {

        return Typeface.createFromAsset(App.getContext().getAssets(), "fontawesome-webfont.ttf");

    }

    public static void timerTask() {

        if (timer != null) {
            timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
            timer.purge();   // Removes all cancelled tasks from this timer's task queue.
        }

        laterNotifications = new LaterNotifications(getContext());
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                laterNotifications.runNotifications();
            }
        };

        timer.schedule(task, 1000 * 30, 1000 * 30);
    }

    public static void timerTaskOld() {

        if (timer != null) {
            timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
            timer.purge();   // Removes all cancelled tasks from this timer's task queue.
        }

        laterNotifications = new LaterNotifications(mainActivity);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                App.mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        laterNotifications.runNotifications();
                    }
                });
            }
        };

        timer.schedule(task, 1000 * 30, 1000 * 30);
    }
}