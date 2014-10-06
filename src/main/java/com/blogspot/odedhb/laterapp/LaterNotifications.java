package com.blogspot.odedhb.laterapp;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;

import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snooze;
import com.blogspot.odedhb.laterapp.model.SnoozeService;
import com.blogspot.odedhb.laterapp.model.Snoozes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by oded on 3/23/14.
 */
public class LaterNotifications {

    private final Snoozes snoozes;
    final public static String BROADCAST_STRING = "com.oded.Broadcast";
    Context context;

    LaterNotifications(Context context) {
        this.context = context;
        this.snoozes = new Snoozes(context);
    }

    public void runNotifications() {

        long triggerTime = System.currentTimeMillis();// + 60000;

        for (Item item : Items.ITEMS) {
            if (item.time < triggerTime) {
                showNotification(item);
            }
        }
    }

    public static void dismissNotification(Item item) {

        Context context = App.getContext();

        if (item == null || item.id == null) return;

        int notificationId = item.id.hashCode();
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);

        App.refreshItemViews();
    }

    public void showNotification(Item item) {

        int notificationId = item.id.hashCode();
        String title = item.content;

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.tick)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setLights(item.getColor(), 30000, 100)
                        .setSound(getDefSound())
                        .setLargeIcon(item.getIconBitmap())
                        .setContentText(title)
                        .setSubText("Snooze...")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setWhen(item.time)
                        .setContentIntent(PendingIntentFactory.viewPendingIntent(context));

        List<Snooze> possibleSnoozes = new ArrayList<Snooze>();
        possibleSnoozes.addAll(snoozes.getAll(false).subList(0, 2));
        possibleSnoozes = snoozes.sortForDisplay(possibleSnoozes);
        Collections.reverse(possibleSnoozes);

        possibleSnoozes.add(SnoozeService.itemDoneSnooze());

        String itemId = item.id;
        for (Snooze snooze : possibleSnoozes) {

            String snoozeId = snooze.id();

            notificationBuilder.addAction(snooze.getIconResource(), snooze.shortDesc(), PendingIntentFactory.snoozePendingIntent(context, itemId, snoozeId));
        }


        //snooze randomly to spread the notifications
        notificationBuilder.setDeleteIntent(PendingIntentFactory.swipePendingIntent(context, itemId, Snoozes.getRandomSnooze()));


        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        /*NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(
                notificationBuilder);
        big.setSummaryText("Snooze again");*/

        // Build the notification and issues it with notification manager.
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_SHOW_LIGHTS;
        notificationManager.notify(notificationId, notification);

        App.refreshItemViews();
    }

    public static Uri getDefSound() {
//        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return Uri.parse("android.resource://"
                + App.getContext().getPackageName() + "/" + R.raw.woman_clears_throat);
    }

}
