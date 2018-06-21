package com.allen.androidcustomview.widget;

/**
 * Created by a111 on 2018/6/20.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class BubbleView2 extends View implements Runnable {
    private static final String TAG = "BubbleView";
    private static final int NORTH = 0;
    private static final int SOUTH = 1;
    private static final int EAST = 2;
    private static final int WEST = 3;
    private static final int NORTH_WEST = 4;
    private static final int NORTH_EAST = 5;
    private static final int SOUTH_WEST = 6;
    private static final int SOUTH_EAST = 7;

    private boolean isInitBubble = false;
    private static final int count = 10;
    private int height, width;

    private Rect viewRect;
    Shader mShasder;
    private Paint paint;
    private boolean isRunning = true;
    private Canvas canvas;
    private ArrayList<Circle> bubbles = new ArrayList<Circle>();
    private Random rand = new Random();
    private boolean isChangeRunning = false;

    private int frequency = 0;

    public BubbleView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BubbleView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleView2(Context context) {
        super(context);
        init();

    }

    @Override
    public void run() {
        while (isRunning) {
            SystemClock.sleep(10);
            this.postInvalidate();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:

                break;
            case KeyEvent.KEYCODE_BACK:
                isRunning = false;
                break;
            case KeyEvent.KEYCODE_HOME:
                isRunning = false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isChangeRunning) {
                    isChangeRunning = true;
                } else {
                    isChangeRunning = false;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isInitBubble) {
            height = this.getHeight();
            width = this.getWidth();
            viewRect = new Rect(0, 0, width, height);
            mShasder = new LinearGradient(0, 0, 0, height, new int[] {
                    Color.GREEN, Color.BLUE
            }, null, Shader.TileMode.CLAMP);
            isInitBubble = true;
            for (int i = 0; i < count; i++) {
                bubbles.add(new Circle(rand.nextInt(width), rand.nextInt(height),
                        rand.nextInt(30) + 20, rand.nextInt(60) + 40));
            }
        }

        paint.reset();
        paint.setShader(mShasder);
        canvas.drawColor(Color.TRANSPARENT);
//        canvas.drawRect(viewRect, paint);
//        canvas.drawColor(Color.BLACK);
//        canvas.drawRect(viewRect, paint);
        if (frequency % 5 == 0) {
            for (Circle c : bubbles) {
                if (isChangeRunning) {
                    c.runBubble();
                } else {
                    c.increase();
                }

            }

        }
        for (Circle c : bubbles) {
            c.draw(canvas, paint);
        }

        frequency++;
        super.onDraw(canvas);
    }

    private void init() {
        paint = new Paint();
        this.setFocusable(true);
        new Thread(this).start();
    }

    private class CircleCenterPoint {
        private float x;
        private float y;

        public CircleCenterPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public boolean isOutOfView(float r) {
            boolean isOutView = false;
            if (x > width - r || x < r || y > height - r || y <= r) {
                isOutView = true;
            }

            return isOutView;
        }

        public boolean isInCenterView(float r, float centerViewR) {
            boolean isOutView = false;
            if (x > width / 2 - centerViewR && x < width / 2 + centerViewR
                    && y > height / 2 - centerViewR && y <= height / 2 + centerViewR) {
                isOutView = true;
            }
            return isOutView;
        }
    }

    private class Circle {
        private CircleCenterPoint center;
        private float r;
        private int a;
        private int aMax;
        private double distance;
        private double minDistance;
        private int type = rand.nextInt(100);
        private int count = 0;

        private double delta = 0.1f;
        private double theta = 0;

        private boolean isInitDistance = false;
        private double delta2 = 0.1f;
        boolean isShouldDisapear = false;

        public Circle(float x, float y, float r, int a) {
            center = new CircleCenterPoint(x, y);
            this.r = r;
            this.a = a;
            minDistance = rand.nextInt(100);
        }

        public void runBubble() {
            if (!isInitDistance) {
                isInitDistance = true;
                float x = center.getX() - width / 2;
                float y = center.getY() - height / 2;

                distance = Math.sqrt((double) (x * x + y * y));

                if (x > 0) {

                    theta = Math.atan(y / x);
                } else {

                    theta = Math.atan(y / x) + Math.PI;
                }

            }
            theta = theta + 0.1;
            distance = getDistance(distance);
            center.setY((float) (distance * Math.sin(theta)) + height / 2);
            center.setX((float) (distance * Math.cos(theta)) + width / 2);
        }

        public void increase() {
            if (isInitDistance) {
                delta = 0.1f;
                isInitDistance = false;
            }
            changeCenterPoint(center, type);

            if (center.isOutOfView(r) || center.isInCenterView(r, 100f)) {
                isShouldDisapear = true;

            }

        }

        public void draw(Canvas canvas, Paint paint) {
            count++;
            if (count % ((type + 8) * 10) == 0) {
                type = rand.nextInt(100);
            }
            if (isShouldDisapear) {
                a--;
                if (a <= 0) {
                    isShouldDisapear = false;
                    center.setX(rand.nextInt(width));
                    center.setY(rand.nextInt(height));
                    r = rand.nextInt(30) + 20;
                    aMax = rand.nextInt(60) + 40;
                }

            } else {

                if (a <= aMax) {
                    a++;
                } else {
                    aMax = 0;
                }
            }
            paint.reset();
            paint.setColor(Color.WHITE);
            paint.setAlpha(a);
            canvas.drawCircle(center.getX(), center.getY(), r, paint);
        }

        private double getDistance(double distance) {

            double result = distance;
            delta = delta + 0.1;

            result = result - delta * delta;
            if (result < minDistance) {
                result = minDistance;
            }
            return result;
        }

    }

    private void changeCenterPoint(CircleCenterPoint center, int type) {

        switch (type % 8) {
            case NORTH:
                center.setX(center.getX());
                center.setY(center.getY() - rand.nextInt(4));
                break;
            case SOUTH:
                center.setX(center.getX());
                center.setY(center.getY() + rand.nextInt(4));
                break;
            case EAST:
                center.setX(center.getX() + rand.nextInt(4));
                center.setY(center.getY());
                break;
            case WEST:
                center.setX(center.getX() - rand.nextInt(4));
                center.setY(center.getY());

                break;
            case NORTH_WEST:
                center.setX(center.getX() - rand.nextInt(4));
                center.setY(center.getY() - rand.nextInt(4));
                break;
            case NORTH_EAST:
                center.setX(center.getX() + rand.nextInt(4));
                center.setY(center.getY() - rand.nextInt(4));
                break;
            case SOUTH_WEST:
                center.setX(center.getX() + rand.nextInt(4));
                center.setY(center.getY() + rand.nextInt(4));
                break;
            case SOUTH_EAST:
                center.setX(center.getX() - rand.nextInt(4));
                center.setY(center.getY() + rand.nextInt(4));
                break;
        }
    }

}