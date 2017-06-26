package com.example.androidcodechallenge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tienl on 26/6/17.
 */

public class Timer extends View {

    public static final int DEFAULT_THICKNESS = 50; //pixels


    private Paint arcPaint = new Paint();

    private RectF arcRect = new RectF(0, 0, 0, 0);

    public Timer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(DEFAULT_THICKNESS);
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

        arcRect.right = arcRect.bottom = w;

        setRingThickness((int) arcPaint.getStrokeWidth());
    }

    /**
     * Sets the size of the timer ring, and the size of the view
     * @param size
     */
    public void setSize(int size) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = size;
        setLayoutParams(params);
    }

    public void setRingThickness(int thickness) {
        arcPaint.setStrokeWidth(thickness);

        arcRect.left = thickness / 2;
        arcRect.top = thickness / 2;
        arcRect.right = getMeasuredWidth() - (thickness / 2);
        arcRect.bottom = getMeasuredHeight() - (thickness / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Shader gradient = new SweepGradient(arcRect.width() / 2, arcRect.width() / 2, Color.RED, Color.TRANSPARENT);
        arcPaint.setShader(gradient);
        canvas.drawArc(arcRect, 0, 360, true, arcPaint);
    }
}
