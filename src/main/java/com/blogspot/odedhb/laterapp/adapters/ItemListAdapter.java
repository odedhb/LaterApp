package com.blogspot.odedhb.laterapp.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.Toaster;
import com.blogspot.odedhb.laterapp.dialogs.ItemDialog;
import com.blogspot.odedhb.laterapp.dialogs.SnoozeDialog;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snooze;
import com.blogspot.odedhb.laterapp.model.SnoozeService;
import com.blogspot.odedhb.laterapp.model.Snoozes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by oded on 3/18/14.
 */
public class ItemListAdapter extends BaseAdapter {

    private final Activity activity;
    private final Snoozes snoozes;
    List<Snooze> possibleSnoozes;
    LayoutInflater inflater;
    LinearLayout.LayoutParams p;

    public ItemListAdapter(Activity activity, Snoozes snoozes) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.snoozes = snoozes;

        resetSnoozeList();

        p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        p.weight = 2;
    }

    @Override
    public int getCount() {
        return getItems().size();
    }

    @Override
    public Item getItem(int i) {
        return getItems().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final Item item = getItem(i);

        final ViewHolder viewHolder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_row_white, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.time = ((TextView) convertView.findViewById(R.id.item_time));
            viewHolder.timeDelta = ((TextView) convertView.findViewById(R.id.item_time_delta));
            viewHolder.snoozeButtonsHolder = (LinearLayout) convertView.findViewById(R.id.snooze_buttons);
//            viewHolder.snoozeButtonsHolderOverlay = convertView.findViewById(R.id.snooze_buttons_overlay);
            viewHolder.itemDesc = ((TextView) convertView.findViewById(R.id.item_desc));
            viewHolder.doneColorLabelTickBottom = (TextView) convertView.findViewById(R.id.item_done_color_label_bottom);
            viewHolder.doneColorLabelTickBottom.setTypeface(App.fontAwesome());
            viewHolder.doneColorLabelTickTop = (TextView) convertView.findViewById(R.id.item_done_color_label_top);
            viewHolder.doneColorLabelTickTop.setTypeface(App.fontAwesome());

            buildSnoozeButtons(viewHolder);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        /**
         * here starts the unique-to-row stuff:
         */

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                (new OpenItemDialog(activity, item)).show();
                (new ItemDialog(activity, item)).show();
            }
        });

        viewHolder.itemDesc.setText(item.content);
//        viewHolder.itemDesc.setTextColor(item.getTextColor());


        /*if (item.isOverdue()) {
            viewHolder.snoozeButtonsHolderOverlay.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            viewHolder.snoozeButtonsHolderOverlay.setBackgroundColor(Color.parseColor("#bbffffff"));
        }*/


        viewHolder.time.setText(
                item.getFullFormattedTime()
        );
//        viewHolder.time.setTextColor(item.getTextColor());

        viewHolder.timeDelta.setText(
                item.getTimeDelta()
        );
        viewHolder.timeDelta.setTextColor(item.getColor());

        viewHolder.time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                snoozes.resetSnoozeStats();
                Toaster.showMessage("Snooze history was deleted");
                return true;
            }
        });

//                android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", item.time));
        viewHolder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new SnoozeDialog(activity, item)).show();

            }
        });


        viewHolder.doneColorLabelTickBottom.setTextColor(item.getColor());
        if (item.getOldnessIndicator() < 1) {
            viewHolder.doneColorLabelTickTop.setTextColor(item.getColor());
//            viewHolder.doneColorLabelTickTop.setShadowLayer(1, 1, 4, Color.parseColor("#99000000"));
        } else {
            viewHolder.doneColorLabelTickTop.setTextColor(item.getColor());
//            viewHolder.doneColorLabelTickTop.setShadowLayer(item.getOldnessIndicator(), 1, 4, Color.parseColor("#99000000"));
        }
        viewHolder.doneColorLabelTickTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SnoozeService.itemDoneSnooze().run(item);
                App.refreshItemViews();
            }
        });

//        createSnoozeButtons(viewHolder, item);
        buildSnoozes(viewHolder, item);

        return convertView;
    }

    private void buildSnoozeButtons(final ViewHolder viewHolder) {
        for (final Snooze snooze : possibleSnoozes) {
            Button button = new Button(activity);

            button.setTextSize(12);
            button.setLines(2);
            button.setTypeface(App.fontAwesome());

            button.setLayoutParams(p);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setPadding(0, 0, 0, 9);

            Drawable buttonSelector = activity.getResources().getDrawable(R.drawable.generic_button_selector);
            button.setBackgroundDrawable(buttonSelector);

            viewHolder.snoozeButtonsHolder.addView(button);

        }
    }

    private void buildSnoozes(ViewHolder viewHolder, final Item item) {
        int snoozeIndex = 0;
        int snoozeButtonsHolderChildCount = viewHolder.snoozeButtonsHolder.getChildCount();
        for (int childViewIndex = 0; childViewIndex < snoozeButtonsHolderChildCount; childViewIndex++) {
            View v = viewHolder.snoozeButtonsHolder.getChildAt(childViewIndex);
            if (!(v instanceof Button)
                    && !(v instanceof ImageButton)) continue;

            if (v == null) continue;

            if (snoozeIndex > 4) {
                Toaster.showMessage("bug! please report.");
            }

            final Snooze snooze = possibleSnoozes.get(snoozeIndex);
            snoozeIndex++;
            ((Button) v).setText(snooze.shortDescIcon());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snooze.run(item);
                    notifyDataSetChanged();
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toaster.showMessage(snooze.desc());
                    return true;
                }
            });


            if (item.isOverdue()) {
                ((Button) v).setTextColor(Color.parseColor("#333333"));
            } else {
                ((Button) v).setTextColor(Color.parseColor("#66333333"));
            }
        }
    }




    static class ViewHolder {
        TextView itemDesc;
        LinearLayout snoozeButtonsHolder;
//        View snoozeButtonsHolderOverlay;
        TextView time;
        TextView timeDelta;
        TextView doneColorLabelTickBottom;
        TextView doneColorLabelTickTop;
    }


    public List<Item> getItems() {

        List<Item> items = new ArrayList<Item>();

        int count = 0;

        for (Item item : Items.ITEMS) {
            if (item.isForNow() && count < 3) {
                count++;
                continue;
            }
            items.add(item);
        }


        return items;

    }

    public void resetSnoozeList() {
        if (possibleSnoozes == null) {
            possibleSnoozes = new ArrayList<Snooze>();
        } else {
            possibleSnoozes.clear();
        }

        possibleSnoozes.addAll(snoozes.getAll(true).subList(0, 4));
        possibleSnoozes = snoozes.sortForDisplay(possibleSnoozes);

        Collections.reverse(possibleSnoozes);
    }

}
