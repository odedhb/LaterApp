package com.blogspot.odedhb.laterapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.odedhb.laterapp.model.Item;

/**
 * Created by oded on 4/16/14.
 */
public class Toaster {

    //give a man a toast, and he will eat once, give a man a toaster and he will french-toast for life.

    public static void show(CharSequence message, Item item) {

        if (message == null) {
            message = item.getTimeDelta();
        }

        LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.french_toast, null);
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(item.content);
//        text.setTextColor(item.getTextColor());
        TextView action = (TextView) layout.findViewById(R.id.toast_action);
        action.setText(message);
        ViewGroup root = (ViewGroup) layout.findViewById(R.id.toast_root);
        View div = layout.findViewById(R.id.toast_divider);
        View bg = layout.findViewById(R.id.toast_bg);
//        bg.setBackgroundColor(item.getColor());
        Toast toast = new Toast(App.getContext());
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showMessage(String text) {
        Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void show(Item item) {
        show(null, item);
    }
}
