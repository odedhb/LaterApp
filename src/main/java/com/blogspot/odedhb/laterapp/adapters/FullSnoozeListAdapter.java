package com.blogspot.odedhb.laterapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Snooze;

import java.util.List;

/**
 * Created by oded on 4/15/14.
 */
public class FullSnoozeListAdapter extends ArrayAdapter<Snooze> {
    LayoutInflater inflater;
    Item item;
    List<Snooze> snoozes;

    public FullSnoozeListAdapter(Context context, int resource, List<Snooze> snoozes, Item item) {
        super(context, resource, snoozes);
        this.item = item;
        inflater = LayoutInflater.from(context);
        this.snoozes = snoozes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dialog_snooze_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.snoozeDesc = ((TextView) convertView);
            viewHolder.snoozeDesc.setTypeface(App.fontAwesome());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.snoozeDesc.setText(snoozes.get(position).descIcon());
        return convertView;
    }


    static class ViewHolder {
        TextView snoozeDesc;
    }

}
