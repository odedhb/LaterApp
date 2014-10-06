package com.blogspot.odedhb.laterapp.model;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.PendingIntentFactory;
import com.blogspot.odedhb.laterapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oded on 4/8/14.
 */
public class StoredNotification {

    private static long autoDismissTimeOut = 15000;
    public String text;
    public Long time;
    public String originalId;


    final public static int SUGGESTED_SNOOZE_NOTIFICATION_ID = 198798;

    final public static String BROADCAST_STRING = "com.oded.Item";

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void suggestSnooze(StatusBarNotification sbn) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        final Context context = App.getContext();
        Snoozes snoozes = new Snoozes(context);


        String subText = "Dismissed, tap to snooze...";


        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE);

//        String ticker = extras.getString(Notification.EXTRA_INFO_TEXT)

//        String suggestedTitle = extras.getString(Notification.EXTRA_TITLE);

        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
/*

        int notificationIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap notificationLargeIcon =
                ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
*/


        String itemText;
        if (text == null) {
            text = "";
        }

//        if(ticker !=null)

        if (title == null) {
            return;
        }

        itemText = title + " : " + text;

        //don't show empty notifications
        if (itemText.length() < 5) return;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSubText(subText)
                .setAutoCancel(true)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(sbn.getNotification().when)
                .setContentIntent(PendingIntentFactory.createAndSnoozePendingIntent(context, itemText, "in 5 minutes"))

                .setSmallIcon(R.drawable.transparent);

        setIcon(context, sbn, notificationBuilder);

        List<Snooze> possibleSnoozes = new ArrayList<Snooze>();
        possibleSnoozes.addAll(snoozes.getAll(false).subList(0, 3));
        possibleSnoozes = snoozes.sortForDisplay(possibleSnoozes);
        Collections.reverse(possibleSnoozes);


        for (Snooze snooze : possibleSnoozes) {

            String snoozeId = snooze.id();


            notificationBuilder.addAction(snooze.getIconResource(), snooze.shortDesc(),
                    PendingIntentFactory.createAndSnoozePendingIntent(context, itemText, snoozeId));
        }


        notificationBuilder.setDeleteIntent(PendingIntentFactory.dismissPendingIntent(context, itemText));


//        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Ignore", swipePendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);


        Notification notification = notificationBuilder.build();
//        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(SUGGESTED_SNOOZE_NOTIFICATION_ID, notification);


        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(autoDismissTimeOut);
                NotificationManagerCompat.from(context).cancel(SUGGESTED_SNOOZE_NOTIFICATION_ID);
            }
        }).run();


        App.notificationWithIntentsMap().put(itemText, sbn);

    }

    private static void setIconOld(Context context, StatusBarNotification sbn, NotificationCompat.Builder notificationBuilder) {
        Bitmap bmp = sbn.getNotification().largeIcon;
        notificationBuilder.setLargeIcon(toGrayScale(bmp));
    }

    private static void setIcon(Context context, StatusBarNotification sbn, NotificationCompat.Builder notificationBuilder) {
        try {
//            Drawable icon = sbn.get
            Drawable icon = context.getPackageManager().getApplicationIcon(sbn.getPackageName());
            Bitmap bmp = drawableToBitmap(icon);
            notificationBuilder.setLargeIcon(toGrayScale(bmp));
        } catch (PackageManager.NameNotFoundException e) {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.notifications_swiped));
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}




