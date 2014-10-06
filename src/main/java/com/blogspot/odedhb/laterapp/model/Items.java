package com.blogspot.odedhb.laterapp.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.LaterNotifications;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.Toaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Items {

    static final SharedPreferences itemsTimes;
    static final SharedPreferences itemsDescs;
    static final SharedPreferences itemsCreationTimes;

    static final SharedPreferences itemsDeletedTimes;
    static final SharedPreferences itemsDeletedDescs;

    public static List<Item> ITEMS = new ArrayList<Item>();

    public static Map<String, Item> ITEM_MAP = new HashMap<String, Item>();

    static {
        itemsTimes = App.getContext().getSharedPreferences("items_times", Context.MODE_PRIVATE);
        itemsDescs = App.getContext().getSharedPreferences("items_descs", Context.MODE_PRIVATE);
        itemsCreationTimes = App.getContext().getSharedPreferences("items_creation_times", Context.MODE_PRIVATE);

        itemsDeletedTimes = App.getContext().getSharedPreferences("items_deleted_times", Context.MODE_PRIVATE);
        itemsDeletedDescs = App.getContext().getSharedPreferences("items_deleted_descs", Context.MODE_PRIVATE);

        //get items from storage
        Map<String, ?> descs = itemsDescs.getAll();

/*
        //temp data fix
        for(String i:descs.keySet()){

            itemsDescs.edit().putString(i,i).commit();

        }
*/

        Map<String, String> allDescs = (Map<String, String>) descs;
        for (String itemId : allDescs.keySet()) {

            Item i = new Item(itemId, allDescs.get(itemId).toString());
            i.time = itemsTimes.getLong(itemId, 0);
            i.creationTime = itemsCreationTimes.getLong(itemId, 0);

            ITEMS.add(i);
            ITEM_MAP.put(itemId, i);

        }

        sortList();

    }

    static void sortList() {

        Collections.sort(ITEMS, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {

                if (item.time < item2.time) return -1;

                if (item.time > item2.time) return 1;

                return item.id.compareTo(item2.id);
            }
        });
    }

    public static void createOrReplace(Item item) {
        createOrReplace(item, true);
    }


    public static void createOrReplace(Item item, boolean showToast) {

        if (ITEM_MAP.get(item.id) == null) {
            item.creationTime = System.currentTimeMillis();
            ITEMS.add(item);
        } else if (showToast) {
            Toaster.show(item);
        }
        ITEM_MAP.put(item.id, item);
        itemsTimes.edit().putLong(item.id, item.time).commit();
        itemsDescs.edit().putString(item.id, item.content).commit();
        itemsCreationTimes.edit().putLong(item.id, item.creationTime).commit();

        LaterNotifications.dismissNotification(item);

        sortList();

    }

    public static Item getItemById(String id) {
        return ITEM_MAP.get(id);
    }


    public static void removeItem(Item item) {
        ITEMS.remove(item);
        if (item.id == null) return;

        ITEM_MAP.remove(item.id);
        itemsTimes.edit().remove(item.id).commit();
        itemsDescs.edit().remove(item.id).commit();
        sortList();
        LaterNotifications.dismissNotification(item);

        //put in recycle bin
        itemsDeletedTimes.edit().putLong(item.id, System.currentTimeMillis()).commit();
        itemsDeletedDescs.edit().putString(item.id, item.content).commit();

        Toaster.show("Complete", item);
    }

    public static List<Item> getDeletedItems(int howMany) {
        List<Item> items = new ArrayList<Item>();
        //get items from storage
        Map<String, String> allDescs = (Map<String, String>) itemsDeletedDescs.getAll();
        for (String itemId : allDescs.keySet()) {

            Item i = new Item(itemId, allDescs.get(itemId).toString());
            i.time = itemsDeletedTimes.getLong(itemId, 0);
            i.icon = R.drawable.tick;
            items.add(i);
        }

        Items.sortList(items);

        int count = items.size();
        return items.subList(0, howMany > count ? count : howMany);
    }

    public static void sortList(List<Item> suggestionList) {
        Collections.sort(suggestionList, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
//                return Long.compare(item2.time, item.time);

                Long timeLong2 = item2.time;
                return timeLong2.compareTo(item.time);
            }
        });
    }
}
