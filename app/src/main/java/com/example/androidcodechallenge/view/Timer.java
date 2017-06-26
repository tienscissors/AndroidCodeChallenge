package com.example.androidcodechallenge.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by tienl on 26/6/17.
 */

public class Timer extends View {

    private static final String TAG = Timer.class.getSimpleName();


    public static final int DEFAULT_THICKNESS = 50; //pixels

    public static final int DEFAULT_COLOR = 0xff458dd2; //the light blue from the example

    private Paint arcPaint = new Paint();

    private RectF arcRect = new RectF(0, 0, 0, 0);

    private ObjectAnimator ringRotationAnimator;

    private int rotation = -90;

    private int color = DEFAULT_COLOR;

    public Timer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(DEFAULT_THICKNESS);

        setThemeColor(color);
    }

    /**
     * Ensures the view is always square using the width as height
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setRingThickness((int) arcPaint.getStrokeWidth());
        setThemeColor(color);
    }

    public void start() {
        ringRotationAnimator = ObjectAnimator.ofInt(this, "ringRotation", -90, 270);
        ringRotationAnimator.setDuration(1000).setRepeatCount(-1);
        ringRotationAnimator.setInterpolator(new LinearInterpolator());
        ringRotationAnimator.start();
    }

    public void stop() {
        ringRotationAnimator.cancel();
    }

    public void pause() {
        ringRotationAnimator.pause();
    }

    public void setRingRotation(int rotation) {
        Log.i(TAG, "Rotation: " + rotation);

        this.rotation = rotation;
        invalidate();
    }

    /**
     * Sets the size of the timer ring, and the size of the view
     * @param size In pixels
     */
    public void setSize(int size) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = size;
        setLayoutParams(params);
    }

    /**
     * Sets the thickness of the ring stroke.
     * @param thickness In Pixels
     */
    public void setRingThickness(int thickness) {
        arcPaint.setStrokeWidth(thickness);

        //We need to adjust the arc rects as it draws in the middle of the stroke width
        arcRect.left = arcRect.top = thickness / 2;
        arcRect.right = arcRect.bottom = getMeasuredWidth() - (thickness / 2);
    }

    /**
     * Sets the color for the ring and the timer text
     *
     * @param color In ARGB format
     */
    public void setThemeColor(int color) {
        arcPaint.setShader(new SweepGradient(arcRect.width() / 2, arcRect.width() / 2, Color.TRANSPARENT, color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate(rotation, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);
    }
}
