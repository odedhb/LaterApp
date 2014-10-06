package com.blogspot.odedhb.laterapp.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.App;
import com.blogspot.odedhb.laterapp.R;
import com.blogspot.odedhb.laterapp.listeners.TrayNotificationListener;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.receivers.UserCalendarReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oded on 4/4/14.
 */
public class CreateItemDialog {

    Dialog dialog;

    public CreateItemDialog(Activity activity) {
        List<Item> suggestionList = new ArrayList<Item>();

        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);

        ClipData primeClip = clipboard.getPrimaryClip();

        if (primeClip != null) {

            for (int i = 0; i < primeClip.getItemCount(); i++) {

                ClipData.Item clipItem = primeClip.getItemAt(i);
                if (clipItem == null) return;

                String text = clipItem.getText().toString();
                Item item = new Item(text);
                item.time = System.currentTimeMillis() - 60000;
                item.icon = R.drawable.ic_action_copy;
                suggestionList.add(item);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            suggestionList.addAll(TrayNotificationListener.getStoredNotificationsAsItems(500));
        }

        suggestionList.addAll(Items.getDeletedItems(500));

        suggestionList.addAll(UserCalendarReceiver.getCalendarEventsAsItems());

        Items.sortList(suggestionList);

        ArrayList<String> suggestionListStrings = new ArrayList<String>();

        for (Item item : suggestionList) {
            suggestionListStrings.add(item.content);
        }

/*
        final CharSequence[] suggestions = suggestionListStrings.toArray(
                new CharSequence[suggestionListStrings.size()]);
*/


        this.dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_create);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        final AutoCompleteTextView input = (AutoCompleteTextView) dialog.findViewById(R.id.item_edit);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setSingleLine();
        input.setThreshold(0);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                createOrReplace(textView.getText().toString());
                return true;
            }
        });

        Button addButton = (Button) dialog.findViewById(R.id.done_create_item);
        addButton.setTypeface(App.fontAwesome());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrReplace(input.getText().toString());
            }
        });

        /*ListView suggestionsListView = (ListView) dialog.findViewById(R.id.item_suggestions);
        SuggestedItemListAdapter adapter = new SuggestedItemListAdapter(dialog, activity, R.layout.dialog_create_list_item, suggestionList);
        suggestionsListView.setAdapter(adapter);*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, suggestionListStrings);
        input.setAdapter(adapter);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                View bullet = dialog.findViewById(R.id.rect_bullet);
                bullet.setVisibility(View.VISIBLE);
                bullet.setBackgroundColor(Item.calculateColor(editable.toString()));
            }
        });

    }


    public void show() {
        dialog.show();
    }

    void createOrReplace(String text) {
        Items.createOrReplace(new Item(text));
        dialog.dismiss();
        App.refreshItemViews();
    }
}
