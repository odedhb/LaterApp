package com.blogspot.odedhb.laterapp.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.odedhb.laterapp.MainActivity;

/**
 * Created by oded on 4/12/14.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private final MainActivity mainActivity;

    public ViewPagerAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (position == 0) {
            container.addView(mainActivity.itemsOverdueListView);
            return mainActivity.itemsOverdueListView;
        } else if (position == 1) {
            container.addView(mainActivity.itemsListView);
            return mainActivity.itemsListView;
        }

        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public float getPageWidth(int position) {
        if(position==0)return 0.98f;
        if(position==1)return 1;
        if(position==2)return 1;

        return 1;
    }
}
