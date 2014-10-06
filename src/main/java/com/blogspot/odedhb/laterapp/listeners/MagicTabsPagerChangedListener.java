package com.blogspot.odedhb.laterapp.listeners;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.blogspot.odedhb.laterapp.MainActivity;
import com.blogspot.odedhb.laterapp.R;

/**
 * Created by oded on 4/12/14.
 */
public class MagicTabsPagerChangedListener implements ViewPager.OnPageChangeListener {

    private final LinearLayout magicBarIndicatorHolder;
    final static int tabCount = 3;
    //    List<View> indicators;
    float staticWidth;
    float expandedWidth;
    View tabIndicator;
    View tabPusher;
    LinearLayout.LayoutParams paramsIndicator;
    LinearLayout.LayoutParams paramsPusher;


    public MagicTabsPagerChangedListener(MainActivity mainActivity) {
        this.magicBarIndicatorHolder = (LinearLayout) mainActivity.findViewById(R.id.magic_bar_indicator_holder);

        staticWidth = 1f / (float) tabCount;
        expandedWidth = 2f * staticWidth;

//        indicators = new ArrayList<View>();

        tabIndicator = new View(mainActivity);
        tabPusher = new View(mainActivity);

        paramsIndicator = new LinearLayout.LayoutParams(0, 4);
        paramsPusher = new LinearLayout.LayoutParams(0, 4);

        tabPusher.setBackgroundColor(Color.TRANSPARENT);
        magicBarIndicatorHolder.addView(tabPusher);

//        LinearLayout.LayoutParams paramsIndicator = new LinearLayout.LayoutParams(0, 1);
        paramsIndicator.weight = staticWidth;
        tabIndicator.setLayoutParams(paramsIndicator);
        tabIndicator.setBackgroundColor(Color.BLACK);
        magicBarIndicatorHolder.addView(tabIndicator);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

//        int currentPositionRounded = Math.round((float) position + positionOffset);
//        Log.d("magic", "curr:" + currentPositionRounded + " pos:" + position + " off:" + positionOffset);


        paramsPusher.weight = (position + positionOffset) * staticWidth;
        tabPusher.setLayoutParams(paramsPusher);

        float delta = Math.abs(positionOffset - 0.5f);
        paramsIndicator.weight = (0.5f + delta) * staticWidth;
//        paramsIndicator.height = (int) (4 - delta * 4);
        tabIndicator.setLayoutParams(paramsIndicator);


    }

    @Override
    public void onPageSelected(int position) {
//        App.refreshItemViews();


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
