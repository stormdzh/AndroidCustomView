package com.stormdzh.androidcustomview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 使用参考：https://blog.csdn.net/qq_38261174/article/details/80057833
 *
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2019/7/18 9:57 AM
 */
public class SurfaceBubbleView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private UpdateViewThread updatethread;
    private boolean hasSurface;
    private Paint mPain;

    public SurfaceBubbleView(Context context) {
        this(context, null);
    }

    public SurfaceBubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);//以自身作为callback,回调方法

//        绘制透明
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mPain = new Paint();
        mPain.setColor(Color.RED);
    }

    public void resume() {
        //创建和启动 图片更新线程
        if (updatethread == null) {
            updatethread = new UpdateViewThread();
            if (hasSurface == true) {
                updatethread.start();
            }
        }
    }

    public void pause() {
        //停止 图像更新线程
        if (updatethread != null) {
            updatethread.requestExitAndWait();
            updatethread = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        hasSurface = true;
        resume(); //开启线程更新
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (updatethread != null) {
            updatethread.onWindowResize(width, height);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        hasSurface = false;
        pause(); //停止线程更新
    }


    class UpdateViewThread extends Thread {

        //定义图像是否更新完成的标志
        private boolean done;

        public UpdateViewThread() {
            super();
            done = false;

        }

        @Override
        public void run() {
            Log.i("test", "run");
            SurfaceHolder surfaceholder = holder;

            while (!done) {
                Canvas canvas = surfaceholder.lockCanvas();//锁定surfaceview,准备绘制
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


                canvas.drawText("你好呀", 100, 100, mPain);

                surfaceholder.unlockCanvasAndPost(canvas);//解锁canvas，渲染绘制图像
            }


            //循环绘制，直到线程停止
//            while (!done) {
//                Canvas canvas = surfaceholder.lockCanvas();//锁定surfaceview,准备绘制
//                //绘制背景
//                canvas.drawBitmap(back, 0, 0, null);
//                //鱼游出屏幕外，重新初始化鱼的位置
//                if (fishx < 0) {
//                    fishx = 778;
//                    fishy = 500;
//                    fishAngle = new Random().nextInt(60);
//                }
//                if (fishy < 0) {
//                    fishx = 778;
//                    fishy = 500;
//                    fishAngle = new Random().nextInt(60);
//                }
//                //用matrix控制鱼的旋转角度和位置
//                matrix.reset();
//                matrix.setRotate(fishAngle);//下面的位置计算看图片的解释如下：
//                matrix.postTranslate(fishx -= fishSpeed * Math.cos(Math.toRadians(fishAngle)), fishy -= fishSpeed * Math.sin(Math.toRadians(fishAngle)));
//                canvas.drawBitmap(fishs[fishIndex++ % fishs.length], matrix, null);
//                surfaceholder.unlockCanvasAndPost(canvas);//解锁canvas，渲染绘制图像
//                try {
//                    Thread.sleep(60);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
        }


        public void requestExitAndWait() {
            //将绘制线程 标记为完成 ,并合并到主线程中
            done = true;
            try {
                join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onWindowResize(int width, int height) {
            //处理surfaceview的大小改变事件
        }
    }

}
