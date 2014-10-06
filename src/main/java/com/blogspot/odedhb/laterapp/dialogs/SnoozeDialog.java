package com.blogspot.odedhb.laterapp.dialogs;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.adapters.FullSnoozeListAdapter;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Snooze;
import com.blogspot.odedhb.laterapp.model.SnoozeService;
import com.blogspot.odedhb.laterapp.model.Snoozes;

import java.util.List;

/**
 * Created by oded on 5/1/14.
 */
public class SnoozeDialog extends GeneralDialog {

    public SnoozeDialog(Activity activity, Item item) {
        super(activity, item);
    }


    @Override
    ViewGroup getBody(final Activity activity, final Item item) {
        final Snoozes snoozes = new Snoozes(activity);

        RelativeLayout body = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.dialog_snooze, null);
        final List<Snooze> possibleSnoozes = snoozes.getAll(false);


        ListView notificationsListView = (ListView) body.findViewById(R.id.snooze_list);
        /*ArrayAdapter<CharSequence> notificationAdapter = new ArrayAdapter<CharSequence>(activity,
                R.layout.simple_snooze_item, dialogItems);*/

        FullSnoozeListAdapter adapter = new FullSnoozeListAdapter(activity, R.layout.dialog_snooze_list_item, possibleSnoozes, item);

        notificationsListView.setAdapter(adapter);

        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int which, long l) {
                Snooze snooze = possibleSnoozes.get(which);
                snooze.run(item);
                snoozes.countSnooze(snooze);
                App.resetSnoozeButtons();
                App.refreshItemViews();
                dismiss();
            }
        });

        Button specificDate = (Button) body.findViewById(R.id.specific_date);
        specificDate.setTypeface(App.fontAwesome());
        specificDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnoozeService.specificDate(activity).run(item);
                dismiss();
            }
        });


        return body;
    }
}
