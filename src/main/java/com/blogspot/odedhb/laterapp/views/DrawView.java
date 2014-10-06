package com.blogspot.odedhb.laterapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //do stuff that was in your original constructor...
    }

    @Override
    public void onDraw(Canvas canvas) {


        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setShader(new LinearGradient(40, 40, 200, 200, Color.TRANSPARENT, Color.parseColor("#22000000"), Shader.TileMode.CLAMP));
//        paint.setShader(new LinearGradient(40, 40, 200, 200, Color.TRANSPARENT, Color.parseColor("#33000000"), Shader.TileMode.CLAMP));
//        paint.setColor(Color.parseColor("#66333333"));
        paint.setAntiAlias(true);
        Path p = new Path();

        Point checkBottom = new Point(50, 162);

        p.moveTo(checkBottom.x, checkBottom.y);
        p.lineTo(-200, 80);//far left bottom
        p.lineTo(80, -200);//far left top
        p.lineTo(126, 130);//check right top
        p.lineTo(80, 170);//check armpit
        p.lineTo(checkBottom.x, checkBottom.y);
        canvas.drawPath(p, paint);

    }

    public void onDrawOld(Canvas canvas) {


        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor("#11333333"));
        paint.setAntiAlias(true);
        Path p = new Path();

        Point bottomLeft = new Point(84, 198);

        p.moveTo(bottomLeft.x, bottomLeft.y);//fa
        p.lineTo(1100, 800);//far right bottom
        p.lineTo(1100, -400);//far right top
        p.lineTo(126, 130);//top left
        p.lineTo(bottomLeft.x, bottomLeft.y);
        canvas.drawPath(p, paint);

    }

}