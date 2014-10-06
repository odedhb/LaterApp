package com.blogspot.odedhb.laterapp.listeners;

import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by oded on 5/18/14.
 */
public abstract class ButtonDragListener implements View.OnDragListener {

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {

        TextView button = (TextView) view;

        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                button.setBackgroundColor(Color.parseColor("#33000000"));
                button.setTextColor(Color.BLACK);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //todo use color of the dragged item
                button.setBackgroundColor(Color.parseColor("#99000000"));
                button.setTextColor(Color.WHITE);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                button.setBackgroundColor(Color.parseColor("#33000000"));
                button.setTextColor(Color.BLACK);
                break;
            case DragEvent.ACTION_DROP:
                onDropEvent(dragEvent);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTextColor(Color.TRANSPARENT);
            default:
                break;

        }
        return true;
    }

    abstract public void onDropEvent(DragEvent dragEvent);
}
