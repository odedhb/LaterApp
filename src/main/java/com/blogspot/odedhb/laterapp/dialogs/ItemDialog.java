package com.blogspot.odedhb.laterapp.dialogs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import android.service.notification.StatusBarNotification;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.Toaster;
import com.blogspot.odedhb.laterapp.model.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by oded on 5/1/14.
 */
public class ItemDialog extends GeneralDialog {
    public ItemDialog(Activity activity, Item item) {
        super(activity, item);
    }


    @Override
    ViewGroup getBody(final Activity activity, final Item item) {

        RelativeLayout body = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.dialog_open, null);

        final List<Notification.Action> actions = new ArrayList<Notification.Action>();

        final List<CharSequence> actionTitles = new ArrayList<CharSequence>();


        addPendingIntentActions(item, actionTitles, actions);


        final CharSequence[] suggestions = actionTitles.toArray(
                new CharSequence[actionTitles.size()]);


        ListView notificationsListView = (ListView) body.findViewById(R.id.item_actions);
        ArrayAdapter<CharSequence> notificationAdapter = new ArrayAdapter<CharSequence>(activity,
                R.layout.dialog_open_list_item, suggestions);
        notificationsListView.setAdapter(notificationAdapter);
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int which, long l) {
                runPendingIntent(actions, which, activity, actionTitles);
                dismiss();
            }
        });


        TextView copy = (TextView) body.findViewById(R.id.item_copy);
        TextView share = (TextView) body.findViewById(R.id.item_share);
        TextView event = (TextView) body.findViewById(R.id.item_event);
        copy.setTypeface(App.fontAwesome());
        share.setTypeface(App.fontAwesome());
        event.setTypeface(App.fontAwesome());

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = ClipData.newPlainText("Copied Text", item.content);
                clipboard.setPrimaryClip(clip);
                Toaster.show("Copied", item);
                dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share intent

                String text = item.content;

                text = text + "\n" + item.getFullFormattedTime();
                text = text + "\n\n\n" + activity.getResources().getString(R.string.app_signature);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.content);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                activity.startActivity(Intent.createChooser(sharingIntent, "Send text to"));
                dismiss();
            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, item.time)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, item.time + DateUtils.MINUTE_IN_MILLIS * 5)
                        .putExtra(CalendarContract.Events.TITLE, item.content)
                        .putExtra(CalendarContract.Events.DESCRIPTION, item.content)
//                            .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
//                            .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
                activity.startActivity(Intent.createChooser(calIntent, "Create event in"));
                dismiss();
            }
        });

        return body;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void runPendingIntent(List<Notification.Action> actions, int which, Activity activity, List<CharSequence> actionTitles) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        try {
            actions.get(which).actionIntent.send();
            Toaster.showMessage(actionTitles.toString());
        } catch (PendingIntent.CanceledException e) {
            Toaster.showMessage("Action expired");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void addPendingIntentActions(Item item, List<CharSequence> actionTitles, List<Notification.Action> actions) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        final StatusBarNotification sbn = App.itemsIntentsMap().get(item.id);
        if (sbn != null
                && sbn.getNotification() != null) {

            if (sbn.getNotification().actions != null) {
                actions.addAll(Arrays.asList(sbn.getNotification().actions));

            }

            if (sbn.getNotification().contentIntent != null) {

                String appName = "app";

                Notification.Action openAction = new Notification.Action(
                        android.R.drawable.ic_menu_set_as, "Open " + appName, sbn.getNotification().contentIntent);
                actions.add(openAction);
            }

        }
        if (item.actions != null) {
            actions.addAll(item.actions);
        }

        for (Notification.Action action : actions) {
            actionTitles.add(action.title);
        }
    }
}
