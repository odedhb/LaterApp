package com.blogspot.odedhb.laterapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;

/**
 * Created by oded on 4/29/14.
 */
public abstract class GeneralDialog extends Dialog {
    private final Activity activity;
    private final Item item;

    public GeneralDialog(Activity activity, Item item) {
        super(activity);
        this.activity = activity;
        this.item = item;
//        setTitle(item.content);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setViews();
    }

    private void setViews() {

        LinearLayout root = new LinearLayout(activity);
        activity.getLayoutInflater().inflate(R.layout.dialog_header, root);
        root.setOrientation(LinearLayout.VERTICAL);

        final TextView time = (TextView) root.findViewById(R.id.item_time_desc);
        time.setText(item.getFullFormattedTime());

        final View bullet = root.findViewById(R.id.rect_bullet);
        bullet.setBackgroundColor(item.getColor());

        final EditText descEdit = (EditText) root.findViewById(R.id.item_desc_edit);

        final TextView desc = (TextView) root.findViewById(R.id.item_desc);
        desc.setText(item.content);

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desc.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                descEdit.setVisibility(View.VISIBLE);
                descEdit.setText(item.content);

                descEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        item.content = editable.toString();
                        Items.createOrReplace(item,false);
                        App.refreshItemViews();
                        bullet.setBackgroundColor(item.getColor());
                    }
                });

            }
        });



        root.addView(getBody(activity, item));
        setContentView(root);
    }

    abstract ViewGroup getBody(final Activity activity, final Item item);

}
