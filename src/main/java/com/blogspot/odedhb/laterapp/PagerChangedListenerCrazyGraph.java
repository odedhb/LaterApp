package com.blogspot.odedhb.laterapp;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.odedhb.laterapp.dialogs.SnoozeDialog;
import com.blogspot.odedhb.laterapp.listeners.ButtonDragListener;
import com.blogspot.odedhb.laterapp.listeners.MagicTabsPagerChangedListener;
import com.blogspot.odedhb.laterapp.model.Item;
import com.blogspot.odedhb.laterapp.model.Items;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by oded on 4/26/14.
 */
public class PagerChangedListenerCrazyGraph implements ViewPager.OnPageChangeListener {

    //    private final View bg;
    private final RelativeLayout graphHolder;
    View crazyGraphRoot;
    TextView itemDescAndHelp;
    View divider;
    private MainActivity mainActivity;
    static int topBound;
    static int squareWidth;
    static int squareHeight;

    static int horizontalSpacing;
    static int verticalSpacing;
    //    boolean dropTargetsPlaced = false;
    Vibrator vibrator;
    Item draggedItemToEdit;
    final static private int dayCountOnGraph = 7;
    //    private View bottomButtonsHolder;
    TextView remove;
    TextView someday;
//    MediaPlayer mp;

    static {

        topBound = dpToPixels((int)App.getContext().getResources().getDimension(R.dimen.crazy_graph_top_margin)-60);
        squareHeight = dpToPixels(22);
        squareWidth = squareHeight * 2;

        horizontalSpacing = dpToPixels(3);
        verticalSpacing = dpToPixels(3);

        /*topBound = dpToPixels(576);
        squareWidth = dpToPixels(112);
        squareHeight = dpToPixels(64);

        horizontalSpacing = dpToPixels(16);
        verticalSpacing = dpToPixels(16);*/
    }

    static int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getContext().getResources().getDisplayMetrics());
    }

    MagicTabsPagerChangedListener magicTabsPagerChangedListener;

    public PagerChangedListenerCrazyGraph(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        crazyGraphRoot = mainActivity.findViewById(R.id.crazy_graph_root);
//        bg = mainActivity.findViewById(R.id.graph_bg);
        graphHolder = (RelativeLayout) mainActivity.findViewById(R.id.graph_holder);
        itemDescAndHelp = (TextView) mainActivity.findViewById(R.id.text_desc);
        divider = mainActivity.findViewById(R.id.divider_colored);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(4, 4);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
//        mp = MediaPlayer.create(mainActivity, R.raw.tap);
        magicTabsPagerChangedListener = new MagicTabsPagerChangedListener(mainActivity);

        addDropButtons();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        magicTabsPagerChangedListener.onPageScrolled(position, positionOffset, positionOffsetPixels);

        if (position > 0 && positionOffset > 0.01) {
            crazyGraphRoot.setVisibility(View.VISIBLE);
//            bg.setAlpha(positionOffset * 5 - 0.05f);
        } else {

            itemDescAndHelp.setText(App.getContext().getResources().getString(R.string.drag_help));
            divider.setBackgroundColor(Color.TRANSPARENT);

            crazyGraphRoot.setVisibility(View.GONE);

            refreshGraph();
            return;
        }

        crazyGraphRoot.setAlpha(positionOffset * 10);

//        itemDescAndHelp.setAlpha(positionOffset);
//        divider.setAlpha(positionOffset);

        refreshGraph(positionOffset);
        refreshGraph(positionOffset);
    }

    private void addDropTargets(int count) {
        for (int i = 0; i < count; i++) {
            addDropTarget(i);
        }
    }


    void draw(float realPositionOffset, final Item item, int locationInDay) {
//        float animationSpeedFactor =(new Random()).nextFloat();

        if (item.daysFromNow() >= dayCountOnGraph) return;

        float animationSpeedFactor = realPositionOffset
                * (dayCountOnGraph - item.daysFromNow())
                * (locationInDay + 1);

        double positionOffset = realPositionOffset * animationSpeedFactor;
        if (positionOffset > 1) positionOffset = 1;


        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(squareWidth, squareHeight);

        int leftMargin = (squareWidth + horizontalSpacing) * item.daysFromNow();
        int topMargin = topBound + (squareHeight + verticalSpacing) * locationInDay;

//        Log.d("c_g", "\n\n" + item.daysFromNow() + " : " + item.content);

        params.leftMargin = leftMargin;
//        double positionOffsetAccelerated = Math.pow(1 + positionOffset, locationInDay) - 1;
//        double positionOffsetAccelerated = positionOffset * locationInDay;
        /*double positionOffsetAdjusted = positionOffset;
        if (positionOffsetAccelerated > positionOffset) {
            positionOffsetAdjusted = positionOffset;
        } else {
            positionOffsetAdjusted = positionOffsetAccelerated;
        }*/
        params.topMargin = (int) (positionOffset * topMargin);

        TextView cell = (TextView) graphHolder.findViewById(item.hashCode());
        if (cell == null) {
            cell = new TextView(mainActivity);
            cell.setId(item.hashCode());
            graphHolder.addView(cell);
        }

        cell.setText(item.content);
        cell.setTextSize(8);
        cell.setTextColor(item.getTextColor());
        cell.setPadding(2, 2, 2, 2);
        cell.setGravity(Gravity.CENTER);
        cell.setLines(2);

        cell.setAlpha((float) positionOffset);

        cell.setLayoutParams(params);
//        cell.setText(item.content);
        cell.setBackgroundColor(item.getColor());


        cell.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                setTextViewItem(item);


                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    ClipData data = ClipData.newPlainText("id", item.id);
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setTextViewItem(final Item item) {
        itemDescAndHelp.setText(item.getWeekDayTime() + " - " + item.content);
        divider.setBackgroundColor(item.getColor());
        itemDescAndHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new SnoozeDialog(mainActivity, item)).show();
            }
        });
    }

    @Override
    public void onPageSelected(int position) {
        magicTabsPagerChangedListener.onPageSelected(position);
        if (position != 2) {
            crazyGraphRoot.setVisibility(View.GONE);
            mainActivity.findViewById(R.id.days_week_holder).setVisibility(View.GONE);
        } else {
            mainActivity.findViewById(R.id.days_week_holder).setVisibility(View.VISIBLE);
            List<Integer> columnTitle = new ArrayList<Integer>();

            columnTitle.add(R.id.day0);
            columnTitle.add(R.id.day1);
            columnTitle.add(R.id.day2);
            columnTitle.add(R.id.day3);
            columnTitle.add(R.id.day4);
            columnTitle.add(R.id.day5);
            columnTitle.add(R.id.day6);

            for (int title : columnTitle) {

                TextView tv = (TextView) mainActivity.findViewById(title);

                int titleLocation = columnTitle.indexOf(title);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, titleLocation);
                CharSequence dayName = DateFormat.format("EEE", c.getTime());

                if (titleLocation == 0) {
                    tv.setText("Today");
                    tv.setTextColor(Color.BLACK);
                } else {
//                    tv.setText("day " + titleLocation);
                    tv.setText(dayName);
                }

                if (c.get(Calendar.DAY_OF_WEEK) == c.getFirstDayOfWeek()) {
                    tv.setBackgroundResource(R.drawable.week_divider);
                }
            }

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        magicTabsPagerChangedListener.onPageScrollStateChanged(state);
    }

    private void addDropTarget(final int daysFromNow) {
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(squareWidth, 2000);

        int leftMargin = (squareWidth + horizontalSpacing) * daysFromNow;

        params.leftMargin = leftMargin;
        params.topMargin = topBound;

        final View dropTarget = new View(mainActivity);
        dropTarget.setLayoutParams(params);
        dropTarget.setBackgroundColor(Color.parseColor("#00ffffff"));

        dropTarget.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        dropTarget.setBackgroundColor(Color.parseColor("#03000000"));
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        dropTarget.setBackgroundColor(Color.parseColor("#11000000"));
                        vibrator.vibrate(1);//1 milliseconds
//                        mp.stop();
//                        mp.start();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        dropTarget.setBackgroundColor(Color.parseColor("#03000000"));
                        break;
                    case DragEvent.ACTION_DROP:

                        ClipData clip = event.getClipData();
                        ClipData.Item i = clip.getItemAt(0);
                        CharSequence t = i.getText();
                        draggedItemToEdit = Items.getItemById(t.toString());

                        Calendar sourceCalendar = Calendar.getInstance();
                        sourceCalendar.setTimeInMillis(draggedItemToEdit.time);
                        int h = sourceCalendar.get(Calendar.HOUR_OF_DAY);
                        int m = sourceCalendar.get(Calendar.MINUTE);

                        Calendar targetCalendar = Calendar.getInstance();
                        targetCalendar.setTimeInMillis(System.currentTimeMillis()
                                + daysFromNow * DateUtils.DAY_IN_MILLIS);
                        targetCalendar.set(Calendar.HOUR_OF_DAY, h);
                        targetCalendar.set(Calendar.MINUTE, m);

                        draggedItemToEdit.time = targetCalendar.getTimeInMillis();

                        Items.createOrReplace(draggedItemToEdit,false);
                        setTextViewItem(draggedItemToEdit);
                        refreshGraph();
                        App.refreshItemViews();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        dropTarget.setBackgroundColor(Color.parseColor("#00000000"));
                    default:
                        break;
                }
                return true;
            }
        });

        graphHolder.addView(dropTarget);
    }


    public void refreshGraph() {
        graphHolder.removeAllViews();
        refreshGraph(1f);
        addDropTargets(dayCountOnGraph);
    }

    private void refreshGraph(float positionOffset) {
        int lastDaysFromNow = 0;
        int locationInDay = 0;
        for (Item item : Items.ITEMS) {

            /*int locationInList = Items.ITEMS.indexOf(item);
            if (positionOffset > 0.5
                    && locationInList < 8) {
                continue;
            }*/

            if (item.daysFromNow() > lastDaysFromNow) {
                locationInDay = 0;
            }

            lastDaysFromNow = item.daysFromNow();
            draw(positionOffset, item, locationInDay);
            locationInDay++;
        }
    }

    private void addDropButtons() {
        remove = (TextView) mainActivity.findViewById(R.id.drop_remove);
/*        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.showMessage("Remove items by dragging here");
            }
        });*/
        someday = (TextView) mainActivity.findViewById(R.id.drop_someday);
        remove.setTypeface(App.fontAwesome());
        someday.setTypeface(App.fontAwesome());
/*        someday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.showMessage("Defer items to someday by dragging here");
            }
        });*/

        remove.setOnDragListener(new ButtonDragListener() {
            @Override
            public void onDropEvent(DragEvent dragEvent) {
                ClipData clipDrop = dragEvent.getClipData();
                ClipData.Item iDrop = clipDrop.getItemAt(0);
                CharSequence cs = iDrop.getText();
                Item draggedItemDropped = Items.getItemById(cs.toString());

                Items.removeItem(draggedItemDropped);
                refreshGraph();
            }
        });
        someday.setOnDragListener(new ButtonDragListener() {
            @Override
            public void onDropEvent(DragEvent dragEvent) {
                ClipData clipDrop = dragEvent.getClipData();
                ClipData.Item iDrop = clipDrop.getItemAt(0);
                CharSequence cs = iDrop.getText();
                Item draggedItemDropped = Items.getItemById(cs.toString());

                long randTime = System.currentTimeMillis() + DateUtils.WEEK_IN_MILLIS + DateUtils.DAY_IN_MILLIS;
                //rand between one week from now to 5 weeks from now.
                long range = DateUtils.WEEK_IN_MILLIS * 4;
                Random r = new Random();
                long number = (long) (r.nextDouble() * range);
                randTime = randTime + number;
                draggedItemDropped.time = randTime;
                Items.createOrReplace(draggedItemDropped);
                refreshGraph();
            }
        });
    }
}
