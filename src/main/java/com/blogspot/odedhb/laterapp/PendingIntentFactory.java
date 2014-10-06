package com.blogspot.odedhb.laterapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.blogspot.odedhb.laterapp.model.StoredNotification;
import com.blogspot.odedhb.laterapp.receivers.RemoteItemReceiver;
import com.blogspot.odedhb.laterapp.receivers.RemoteSnoozeReceiver;

/**
 * Created by oded on 4/12/14.
 */
public class PendingIntentFactory {

    public static PendingIntent viewPendingIntent(Context context) {

        Intent intent = getIntent();
        intent.setClass(context, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);
        return viewPendingIntent;
    }


/*    public static PendingIntent viewAndCreatePendingIntent(Context context, String itemText) {

        Intent intent = getIntent();
        intent.setClass(context, MainActivity.class);
        intent.setAction(StoredNotification.BROADCAST_STRING);
        intent.putExtra(RemoteItemReceiver.ITEM_TEXT, itemText);
        intent.putExtra(RemoteSnoozeReceiver.SNOOZE_ID, "now");
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);
        return viewPendingIntent;

    }*/

    public static PendingIntent createAndSnoozePendingIntent(Context context, String itemText, String snoozeId) {
        Intent intent = getIntent();
        intent.setAction(StoredNotification.BROADCAST_STRING);
        intent.putExtra(RemoteItemReceiver.ITEM_TEXT, itemText);
        intent.putExtra(RemoteSnoozeReceiver.SNOOZE_ID, snoozeId);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, (itemText + snoozeId).hashCode(), intent, 0);
        return snoozePendingIntent;
    }

    public static PendingIntent dismissPendingIntent(Context context, String itemText) {
        Intent intent = getIntent();

        intent.setAction(StoredNotification.BROADCAST_STRING);
        PendingIntent dismissPendingIntent =
                PendingIntent.getBroadcast(context, (itemText + "dismiss").hashCode(), intent, 0);
        return dismissPendingIntent;
    }

    public static PendingIntent snoozePendingIntent(Context context, String itemId, String snoozeId) {
        Intent intent = getIntent();

        intent.setAction(LaterNotifications.BROADCAST_STRING);
        intent.putExtra(RemoteSnoozeReceiver.ITEM_ID, itemId);
        intent.putExtra(RemoteSnoozeReceiver.SNOOZE_ID, snoozeId);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, (itemId + snoozeId).hashCode(), intent, 0);
        return snoozePendingIntent;
    }

    public static PendingIntent swipePendingIntent(Context context, String itemId, String snoozeId) {
        Intent intent = getIntent();

        intent.setAction(LaterNotifications.BROADCAST_STRING);
        intent.putExtra(RemoteSnoozeReceiver.ITEM_ID, itemId);
        intent.putExtra(RemoteSnoozeReceiver.SNOOZE_ID, snoozeId);
        PendingIntent swipePendingIntent =
                PendingIntent.getBroadcast(context, (itemId + snoozeId).hashCode(), intent, 0);
        return swipePendingIntent;
    }

    static Intent getIntent() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return intent;
    }
}
