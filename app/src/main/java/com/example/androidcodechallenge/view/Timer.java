package com.example.androidcodechallenge.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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


    //Using SimpleDateFormat is probably easiest way for use to convert time into required format
    private static final SimpleDateFormat TIMER_FORMAT = new SimpleDateFormat("mm:ss");

    static {
        //Need to set to GMT else it will add timezone offset to formats
        TIMER_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    //Default values
    public static final int DEFAULT_THICKNESS = 50; //pixels

    public static final int DEFAULT_COLOR = 0xff458dd2; //the light blue from the example

    public static final int DEFAULT_TIME = 2 * 60 * 1000; //2 minutes

    public static final int SECONDARY_TEXT_COLOR = 0xffababab; //text color for 2 bottom strings


    //Static / hard coded text, should change dynamically in real situation
    private static final String MOVES = "8 MOVES";

    private static final String REMAINING = "REMAINING";


    /**
     * Modified code snippet here: https://stackoverflow.com/questions/12166476/android-canvas-drawtext-set-font-size-from-width
     *
     * Calculates the necessary text size for the text to fit the desiredWidth
     *
     * @param attr TextAttributes object to store calculations
     * @param paint The paint used to draw/calculate
     * @param desiredWidth The width we want to fit the text into
     * @param text The text to be drawn
     *
     */
    private static void setTextAttributes(@NonNull TextAttributes attr, Paint paint, float desiredWidth,
                                                    String text) {
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
    }


    //For drawing the Ring
    private Paint arcPaint = new Paint();

    private RectF arcRect = new RectF(0, 0, 0, 0);

    //For drawing the text
    private Paint textPaint = new Paint();

    private TextAttributes timeTextAttr = new TextAttributes();

    private TextAttributes movesTextAttr = new TextAttributes();

    private TextAttributes remainingTextAttr = new TextAttributes();

    private Date timeDate = new Date(DEFAULT_TIME);

    private String timeString = TIMER_FORMAT.format(timeDate);

    //vertical spacing between text lines, we make this dynamic too
    private int textSpacing;

    private int initialTextYTranslation;


    //For Animations
    private ObjectAnimator ringRotationAnimator;

    private ObjectAnimator timeAnimator;

    private int rotation = -90;

    //Theme color
    private int color = DEFAULT_COLOR;

    //So we don't have to keep calculating half size
    private int halfSize;



    public Timer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(DEFAULT_THICKNESS);

        //Align text paint to be horizontally centered so we don't have to translate x
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

        halfSize = w / 2;

        //We have to recalibrate our paint after every size change
        setRingThickness((int) arcPaint.getStrokeWidth());
        setThemeColor(color);
    }

    /**
     * Starts the animations for ring rotation and timer
     */
    public void start() {
        ringRotationAnimator = ObjectAnimator.ofInt(this, "ringRotation", -90, 270).setDuration(1000);
        ringRotationAnimator.setRepeatCount(-1);
        ringRotationAnimator.setInterpolator(new LinearInterpolator());

        timeAnimator = ObjectAnimator.ofInt(this, "time", DEFAULT_TIME, 0).setDuration(DEFAULT_TIME);
        timeAnimator.setInterpolator(new LinearInterpolator());

        ringRotationAnimator.start();
        timeAnimator.start();
    }

    /**
     * Stops / Cancels the animation
     */
    public void stop() {
        ringRotationAnimator.cancel();
        timeAnimator.cancel();
    }

    /**
     * Pauses the animation
     *
     * Doesn't work, needs a resume, we won't do for this sample
     */
    public void pause() {
        ringRotationAnimator.pause();
        timeAnimator.pause();
    }

    public void setRingRotation(int rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public void setTime(int time) {
        //If time has finished, stop the animation
        if(time <= 0) {
            stop();
        }

        //Set the time string using value from animator
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

        //Hard coding the allocated space for text to be 60% of available space within the ring
        int textWidth = (int) ((getMeasuredWidth() - (2 * thickness)) * 0.6);

        //Calculate the size and height of the text to fit in our allocated space
        setTextAttributes(timeTextAttr, textPaint, textWidth, timeString);

        setTextAttributes(movesTextAttr, textPaint, textWidth, MOVES);

        setTextAttributes(remainingTextAttr, textPaint, textWidth, REMAINING);

        //Hard coding the space to be 20% of timer text height
        textSpacing = (int) (timeTextAttr.textHeight * 0.2);

        //Text is aligned to the bottom, so we negative translate the total_height / 2, and then positive translate the timer text height
        //We calculate it here so we don't have to calculate every draw loop
        initialTextYTranslation = (int) (timeTextAttr.textHeight-((timeTextAttr.textHeight + movesTextAttr.textHeight + remainingTextAttr.textHeight + (textSpacing * 2)) / 2));
    }

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

        //We rotate according to the animation
        canvas.rotate(rotation, halfSize, halfSize);

        //Draw the arc / ring
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);

        canvas.restore();



        canvas.save();

        canvas.translate(0, initialTextYTranslation);

        //Draw timer
        textPaint.setColor(color);
        textPaint.setTextSize(timeTextAttr.textSize);
        canvas.drawText(timeString, halfSize, halfSize, textPaint);

        textPaint.setColor(SECONDARY_TEXT_COLOR);

        //Draw Moves Text
        textPaint.setTextSize(movesTextAttr.textSize);
        canvas.translate(0, movesTextAttr.textHeight + textSpacing);
        canvas.drawText(MOVES, halfSize, halfSize, textPaint);

        //Draw Remaining text
        textPaint.setTextSize(remainingTextAttr.textSize);
        canvas.translate(0, remainingTextAttr.textHeight + textSpacing);
        canvas.drawText(REMAINING, halfSize, halfSize, textPaint);

        canvas.restore();
    }

    /**
     * Just a simple object to store our text size / height attributes
     */
    static class TextAttributes {
        float textSize;
        float textHeight;
    }

}
