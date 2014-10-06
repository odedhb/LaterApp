package com.blogspot.odedhb.laterapp;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.dialogs.CreateItemDialog;

/**
 * Created by oded on 4/17/14.
 */
public class FooterView extends RelativeLayout {
    private final Activity activity;

    public FooterView(Activity activity) {
        super(activity);
        this.activity = activity;
        initView();
    }


    private void initView() {
        View view = inflate(getContext(), R.layout.footer, null);


        TextView textViewAdd = (TextView) view.findViewById(R.id.new_item_button);
        textViewAdd.setTypeface(App.fontAwesome());
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new CreateItemDialog(activity)).show();
            }
        });

        addView(view);

    }
}
