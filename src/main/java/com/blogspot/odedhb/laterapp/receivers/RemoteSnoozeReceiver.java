package com.blogspot.odedhb.laterapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.LaterNotifications;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snooze;
import com.blogspot.odedhb.laterapp.model.SnoozeService;
import com.blogspot.odedhb.laterapp.model.Snoozes;

import java.util.List;

/**
 * Created by oded on 3/26/14.
 */
public class RemoteSnoozeReceiver extends BroadcastReceiver {

    public static String SNOOZE_ID = "SNOOZE_ID";
    public static String ITEM_ID = "ITEM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {

        String snoozeId = intent.getStringExtra(SNOOZE_ID);
        if (snoozeId == null) return;

        String itemId = intent.getStringExtra(ITEM_ID);
        if (itemId == null) return;

        Item item = Items.ITEM_MAP.get(itemId);

        LaterNotifications.dismissNotification(item);

        if (SnoozeService.itemDoneSnooze().id().equals(snoozeId)) {
            SnoozeService.itemDoneSnooze().run(item);
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
