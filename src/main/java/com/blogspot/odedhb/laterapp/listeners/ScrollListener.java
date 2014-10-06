package com.blogspot.odedhb.laterapp.listeners;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;

import com.blogspot.odedhb.laterapp.MainActivity;
import com.blogspot.odedhb.laterapp.R;

/**
 * Created by oded on 4/17/14.
 */
public class ScrollListener implements android.widget.AbsListView.OnScrollListener {

    private final View magicBar;
    private Animation disAppear;
    private AlphaAnimation appear;

    public ScrollListener(MainActivity mainActivity) {
        this.magicBar = mainActivity.findViewById(R.id.magic_bar);


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {
            appear = new AlphaAnimation(0, 1);
            appear.setDuration(300);
            magicBar.setAnimation(appear);
            magicBar.setVisibility(View.VISIBLE);
        } else {

            /*AlphaAnimation alphaAnimation = (AlphaAnimation) magicBar.getAnimation();
            alphaAnimation.hasEnded()*/

            disAppear = new AlphaAnimation(1, 0);
            disAppear.setDuration(100);
            magicBar.setAnimation(disAppear);
            magicBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /*if (firstVisibleItem > 0) {
            magicBar.setVisibility(View.GONE);
        } else {
            magicBar.setVisibility(View.VISIBLE);

        }*/
    }
}
