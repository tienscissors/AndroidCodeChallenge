package com.example.androidcodechallenge.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tienl on 26/6/17.
 */

public class Timer extends View {

    private static final String TAG = Timer.class.getSimpleName();


    private static final SimpleDateFormat TIMER_FORMAT = new SimpleDateFormat("mm:ss");

    static {
        TIMER_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static final int DEFAULT_THICKNESS = 50; //pixels

    public static final int DEFAULT_COLOR = 0xff458dd2; //the light blue from the example

    public static final int DEFAULT_TIME = 20000; //20 seconds


    public static final int SECONDARY_TEXT_COLOR = 0xffababab; //text color for 2 bottom strings


    private static final String MOVES = "8 MOVES";

    private static final String REMAINING = "REMAINING";


    //Modified code snippet here: https://stackoverflow.com/questions/12166476/android-canvas-drawtext-set-font-size-from-width
    private static TextAttributes setTextAttributes(TextAttributes attr, Paint paint, float desiredWidth,
                                                    String text) {

        if(attr == null) {
            attr = new TextAttributes();
        }

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        attr.textSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(attr.textSize);

        paint.getTextBounds(text, 0, text.length(), bounds);

        attr.textHeight = bounds.height();

        return attr;
    }




    private Paint arcPaint = new Paint();

    private RectF arcRect = new RectF(0, 0, 0, 0);

    private Paint textPaint = new Paint();

    private ObjectAnimator ringRotationAnimator;

    private ObjectAnimator timeAnimator;

    private int rotation = -90;

    private int color = DEFAULT_COLOR;

    private Date timeDate = new Date(DEFAULT_TIME);

    private String timeString = TIMER_FORMAT.format(timeDate);

    private int textWidth;


    public Timer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(DEFAULT_THICKNESS);

        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        //Should set typeface here
//        textPaint.setTypeface()

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
        ringRotationAnimator = ObjectAnimator.ofInt(this, "ringRotation", -90, 270).setDuration(1000);
        ringRotationAnimator.setRepeatCount(-1);
        ringRotationAnimator.setInterpolator(new LinearInterpolator());

        timeAnimator = ObjectAnimator.ofInt(this, "time", DEFAULT_TIME, 0).setDuration(DEFAULT_TIME);
        timeAnimator.setInterpolator(new LinearInterpolator());

        ringRotationAnimator.start();
        timeAnimator.start();
    }

    public void stop() {
        ringRotationAnimator.cancel();

        rotation = -90;
        invalidate();
    }

    public void pause() {
        ringRotationAnimator.pause();
    }

    public void setRingRotation(int rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public void setTime(int time) {
        if(time <= 0) {
            stop();
        }

        timeDate.setTime(time);

        timeString = TIMER_FORMAT.format(timeDate);

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

        textWidth = (int) ((getMeasuredWidth() - (2 * thickness)) * 0.6);

        setTextAttributes(timeTextAttr, textPaint, textWidth, timeString);

        setTextAttributes(movesTextAttr, textPaint, textWidth, MOVES);

        setTextAttributes(remainingTextAttr, textPaint, textWidth, REMAINING);
    }

    private TextAttributes timeTextAttr = new TextAttributes();

    private TextAttributes movesTextAttr = new TextAttributes();

    private TextAttributes remainingTextAttr = new TextAttributes();



    /**
     * Sets the color for the ring and the timer text
     *
     * @param color In ARGB format
     */
    public void setThemeColor(int color) {
        arcPaint.setShader(new SweepGradient(arcRect.width() / 2, arcRect.width() / 2, Color.TRANSPARENT, color));
        textPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.rotate(rotation, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);

        canvas.restore();

        canvas.save();
        canvas.translate(0, -((timeTextAttr.textHeight + movesTextAttr.textHeight + remainingTextAttr.textHeight) / 2));

        //Draw timer
        textPaint.setColor(color);
        textPaint.setTextSize(timeTextAttr.textSize);
        canvas.drawText(timeString, canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);

        textPaint.setColor(SECONDARY_TEXT_COLOR);

        textPaint.setTextSize(movesTextAttr.textSize);
        canvas.translate(0, timeTextAttr.textHeight);
        canvas.drawText(MOVES, canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);

        textPaint.setTextSize(remainingTextAttr.textSize);
        canvas.translate(0, movesTextAttr.textHeight);
        canvas.drawText(REMAINING, canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
    }

    static class TextAttributes {
        float textSize;
        float textHeight;
    }

}
