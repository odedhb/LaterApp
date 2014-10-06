package com.blogspot.odedhb.laterapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.service.notification.StatusBarNotification;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snooze;
import com.blogspot.odedhb.laterapp.model.Snoozes;
import com.blogspot.odedhb.laterapp.model.StoredNotification;

import java.util.List;


public class RemoteItemReceiver extends BroadcastReceiver {

    public static String ITEM_TEXT = "ITEM_TEXT";

    @Override
    public void onReceive(Context context, Intent intent) {

        String snoozeId = intent.getStringExtra(RemoteSnoozeReceiver.SNOOZE_ID);
        if (snoozeId == null) return;

        //notification item suggestion request
        fromNotification(context, intent, snoozeId);
    }

    private void fromNotification(Context context, Intent intent, String snoozeId) {
        String itemText = intent.getStringExtra(ITEM_TEXT);
        if (itemText == null) return;

        Item item = new Item(itemText);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.cancel(StoredNotification.SUGGESTED_SNOOZE_NOTIFICATION_ID);

        Items.createOrReplace(item);

        StatusBarNotification sbn = App.notificationWithIntentsMap().get(itemText);
        if (sbn != null) {
//            App.notificationWithIntentsMap().remove(itemText);
            App.itemsIntentsMap().put(item.id, sbn);
        }


        Snoozes snoozes = new Snoozes(context);
        List<Snooze> all = snoozes.getAll(false);
        for (Snooze snooze : all) {
            if (snoozeId.equals(snooze.id())) {
                snooze.run(item);
                break;
            }
        }

        App.refreshItemViews();
    }
}
