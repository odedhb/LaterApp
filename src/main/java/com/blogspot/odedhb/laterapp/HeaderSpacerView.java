package com.blogspot.odedhb.laterapp;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by oded on 4/17/14.
 */
public class HeaderSpacerView extends RelativeLayout{
    public HeaderSpacerView(Context context) {
        super(context);
        initView();
    }


    private void initView() {
        View view = inflate(getContext(), R.layout.header_spacer, null);
        addView(view);
    }
}
