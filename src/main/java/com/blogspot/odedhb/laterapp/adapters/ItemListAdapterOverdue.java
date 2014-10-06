package com.blogspot.odedhb.laterapp.adapters;

import android.app.Activity;

import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snoozes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oded on 4/13/14.
 */
public class ItemListAdapterOverdue extends ItemListAdapter {
    public ItemListAdapterOverdue(Activity activity,Snoozes snoozes) {
        super(activity, snoozes);
    }

    @Override
    public List<Item> getItems() {

        List<Item> items = new ArrayList<Item>();

        int count = 0;

        for (Item item : Items.ITEMS) {
            if (item.isForNow()) {
                count++;
                if (count > 3) break;
                items.add(item);
            } else {
                break;
            }
        }


        return items;
    }
}
