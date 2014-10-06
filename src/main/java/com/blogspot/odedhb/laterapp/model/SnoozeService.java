package com.blogspot.odedhb.laterapp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by oded on 4/1/14.
 */
public class SnoozeService {

    public static Snooze itemDoneSnooze() {
        return new Snooze() {

            @Override
            public int iconFA() {
                return R.drawable.tick;
            }

            @Override
            public String desc() {
                return "Done";
            }

            @Override
            public Boolean run(Item item) {
                Items.removeItem(item);
                return true;
            }

        };
    }

    public static Snooze specificDate(final Context context) {
        return new Snooze() {
            @Override
            public int iconFA() {
                return android.R.drawable.ic_menu_today;
            }

            @Override
            public String desc() {
                return "Specific Date";
            }

            @Override
            public Boolean run(final Item item) {
                final View dialogView = View.inflate(context, R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        item.time = calendar.getTimeInMillis();
                        Items.createOrReplace(item);
                        alertDialog.dismiss();
                        App.refreshItemViews();

                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();

                return true;
            }


        };
    }


}
