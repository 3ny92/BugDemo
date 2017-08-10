package com.reisen.studiobugdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * @author Reisen at 2017-08-03
 */

public class MyViewGroup extends RelativeLayout implements Runnable {

    private Paint mPaint;
    private Context context;
    private static final double mAngelSize = Math.PI / 180;
    private int mAngle;
    private static PointCoord[] sCoords = new PointCoord[721];
    private Thread mThread;
    private boolean isRunning = true;
    private boolean isLooper;
    private PointCoord mCenterPoint = new PointCoord(0, 0);
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs, defStyleAttr);
        mThread = new Thread(this);
        mThread.start();
    }

    private void initLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        setBackgroundColor(Color.BLACK);
        setWillNotDraw(false);
    }

    private boolean isInitDate;

    private void initDate() {
        if (isInitDate) {
            return;
        }
        isInitDate = true;
        for (int i = 0; i < 361; i++) {
            float x1 = (float) Math.sin(mAngelSize * i);
            float y1 = (float) Math.cos(mAngelSize * i);
            sCoords[i] = new PointCoord(x1, y1);
        }
        System.arraycopy(sCoords, 0, sCoords, 360, 361);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int childCount = getChildCount();
        int centerX;
        int centerY;
        if (childCount == 0) {
            centerX = getWidth() >> 1;
            centerY = getWidth() >> 1;
        } else {
            View view = getChildAt(0);
            centerX = (int) (view.getX() + (view.getWidth() >> 1));
            centerY = (int) (view.getY() + (view.getHeight() >> 1));
        }
        float innerCircle = dip2px(context, 83);
        float ringWidth = dip2px(context, 5);

        mPaint.setARGB(255, 232, 255, 155);
        if (mAngle >= 360) {
            mAngle = 0;
        }
        initDate();
        mCenterPoint.setPointCoord(centerX, centerY);
        drawCenterCircle(canvas, mCenterPoint, innerCircle, ringWidth);
    }

    private void drawCenterCircle(Canvas canvas, PointCoord center, float innerCircle, float ringWidth) {
        if (mAngle >= 360) {
            mAngle = 0;
        }
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(center.x, center.y, innerCircle, mPaint);
        canvas.drawPoint(center.x, center.y, mPaint);
        mPaint.setStrokeWidth(ringWidth);
        canvas.drawCircle(center.x, center.y, ringWidth * 2, mPaint);
        mPaint.setStrokeWidth(0);
        mPaint.setARGB(255, 232, 255, 155);

        canvas.drawLine(
                center.x + innerCircle * sCoords[mAngle].x,
                center.y - innerCircle * sCoords[mAngle].y,
                center.x + innerCircle * sCoords[mAngle + 120].x,
                center.y - innerCircle * sCoords[mAngle + 120].y, mPaint);
        canvas.drawLine(
                center.x + innerCircle * sCoords[mAngle + 120].x,
                center.y - innerCircle * sCoords[mAngle + 120].y,
                center.x - innerCircle * sCoords[mAngle + 60].x,
                center.y + innerCircle * sCoords[mAngle + 60].y, mPaint);
        canvas.drawLine(
                center.x - innerCircle * sCoords[mAngle + 60].x,
                center.y + innerCircle * sCoords[mAngle + 60].y,
                center.x + innerCircle * sCoords[mAngle].x,
                center.y - innerCircle * sCoords[mAngle].y, mPaint);
    }

    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    public void stop() {
        isRunning = false;
        isLooper = false;
    }

    public void start() {
        isRunning = true;
        if (!isLooper) {
            mThread = new Thread(this);
            mThread.start();
        }

    }

    @Override
    public void run() {
        isLooper = true;
        while (isLooper) {
            while (isRunning) {
                SystemClock.sleep(10);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAngle = mAngle == 360 ? 0 : mAngle + 1;
                        requestLayout();
                        invalidate();
                    }
                });
            }
        }

    }

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
    }
}
