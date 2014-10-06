package com.blogspot.odedhb.laterapp;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by oded on 4/17/14.
 */
public class HeaderView extends RelativeLayout {

    private final int position;

    public HeaderView(Context context, int position) {
        super(context);
        this.position = position;
        initView();
    }


    private void initView() {
        View view;
        if (position == 0) {
            view = inflate(getContext(), R.layout.header_now, null);
        } else {
            view = inflate(getContext(), R.layout.header_later, null);
        }
        addView(view);
    }
}
