package com.blogspot.odedhb.laterapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blogspot.odedhb.laterapp.App;


public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        App.timerTask();
    }
}
