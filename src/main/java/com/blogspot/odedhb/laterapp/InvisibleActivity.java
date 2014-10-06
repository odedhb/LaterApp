package com.blogspot.odedhb.laterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;

/**
 * Created by oded on 4/16/14.
 */
public class InvisibleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //generic share text request
        Log.d("share_intent", getIntent().toUri(0));
        String completeText = "";
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String subjectText = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        if (sharedText == null && subjectText==null) return;

        if(subjectText!=null)completeText+=subjectText+" ";
        if(sharedText!=null)completeText+=sharedText;

        Item item = new Item(completeText);
        Items.createOrReplace(item);
        Toaster.show("Created",item);
        finish();
    }

}
