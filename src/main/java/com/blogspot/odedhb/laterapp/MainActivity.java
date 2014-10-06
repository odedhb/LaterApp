package com.blogspot.odedhb.laterapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import com.blogspot.odedhb.laterapp.adapters.ItemListAdapter;
import com.blogspot.odedhb.laterapp.adapters.ItemListAdapterOverdue;
import com.blogspot.odedhb.laterapp.adapters.ViewPagerAdapter;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;
import com.blogspot.odedhb.laterapp.model.Snoozes;
import com.blogspot.odedhb.laterapp.receivers.UserCalendarReceiver;

public class MainActivity extends Activity {

    LaterNotifications laterNotifications;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    PagerChangedListenerCrazyGraph pagerChangedListenerCrazyGraph;
    //    MagicTabsPagerChangedListener magicTabsPagerChangedListener;
    ItemListAdapter itemsAdapter;
    ItemListAdapterOverdue itemsOverdueAdapter;
    public ListView itemsListView;
    public ListView itemsOverdueListView;
    //    public ListView itemsOverdueSnoozeListView;
//        private HeaderSpacerView headerSpacerView;
//    private FooterView footerView;
    public Snoozes snoozes;
//    private ScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        snoozes = new Snoozes(this);

        App.mainActivity = this;

        createItemsViews();

        checkNotificationAccess();

        laterNotifications = new LaterNotifications(this);

        App.timerTask();

        UserCalendarReceiver.importFromCalendarOnlyOnce();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void checkNotificationAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");//TODO: fix when android 4.4.3 or 4.5 comes out
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app the Notification access permission
            String str = "Enable notification access in settings to make Later better:";
            Item item = new Item(str);
            Intent intent = new Intent(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            item.addAction(new Notification.Action(android.R.drawable.ic_menu_preferences, "Notification Settings", pendingIntent));
            Items.createOrReplace(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.refreshItemViews();
    }


    public void createItemsViews() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        pagerChangedListenerCrazyGraph = new PagerChangedListenerCrazyGraph(this);
//        magicTabsPagerChangedListener = new MagicTabsPagerChangedListener(this);
        viewPager.setOnPageChangeListener(pagerChangedListenerCrazyGraph);
//        viewPager.setOnPageChangeListener(magicTabsPagerChangedListener);
        viewPager.setAdapter(viewPagerAdapter);

        itemsAdapter = new ItemListAdapter(this, snoozes);

        itemsOverdueAdapter = new ItemListAdapterOverdue(this, snoozes);


//        scrollListener = new ScrollListener(this);
//        itemsListView.setOnScrollListener(scrollListener);
//        itemsOverdueListView.setOnScrollListener(scrollListener);


        itemsOverdueListView = new ListView(this);
        itemsOverdueListView.addHeaderView(new HeaderSpacerView(this));
        itemsOverdueListView.addFooterView(new FooterView(this));
        itemsOverdueListView.setDividerHeight(0);
        itemsOverdueListView.setAdapter(itemsOverdueAdapter);

        itemsListView = new ListView(this);
        itemsListView.addHeaderView(new HeaderSpacerView(this));
        itemsListView.addFooterView(new HeaderSpacerView(this));
        itemsListView.setDividerHeight(0);
        itemsListView.setAdapter(itemsAdapter);

    }

    public void refreshItemsViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemsAdapter.notifyDataSetChanged();
                itemsOverdueAdapter.notifyDataSetChanged();
            }
        });
    }

    public void resetSnoozeButtons() {
        itemsAdapter.resetSnoozeList();
        itemsOverdueAdapter.resetSnoozeList();
    }

    public void setPage0(View view) {
        setPage(0);
    }

    public void setPage1(View view) {
        setPage(1);
    }

    public void setPage2(View view) {
        setPage(2);
    }


    void setPage(int pageIndex) {
        viewPager.setCurrentItem(pageIndex);
    }

}
