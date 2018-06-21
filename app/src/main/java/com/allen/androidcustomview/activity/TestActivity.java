package com.allen.androidcustomview.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.allen.androidcustomview.R;
import com.allen.androidcustomview.widget.TXWaveViewByBezier;

/**
 * Created by a111 on 2018/6/15.
 * 实现水波动画
 */

public class TestActivity extends AppCompatActivity {


    private TXWaveViewByBezier waveViewByBezier1, waveViewByBezier2;

    private ImageView img_1;
    private ImageView img_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        waveViewByBezier1 = findViewById(R.id.wave_bezier1);
        waveViewByBezier2 = findViewById(R.id.wave_bezier2);

        waveViewByBezier1.setWaveType(TXWaveViewByBezier.SIN);
        waveViewByBezier1.startAnimation();

        waveViewByBezier2.setWaveType(TXWaveViewByBezier.COS);
        waveViewByBezier2.startAnimation();


        img_1=findViewById(R.id.img_1);
        img_2=findViewById(R.id.img_2);
        startAimation();
    }

    private void startAimation() {
//        AnimationSet aset=new AnimationSet(true);
//
//        int dy=100;
//
//        TranslateAnimation translateAnimation1=new TranslateAnimation(0,0,0,dy);
//
//        TranslateAnimation translateAnimation2=new TranslateAnimation(0,0,0,-dy);
//
//        aset.addAnimation(translateAnimation1);
//        aset.addAnimation(translateAnimation2);
//
//
//
//        //设置插值器
//        aset.setInterpolator(new LinearInterpolator());
//        //设置动画持续时长
//        aset.setDuration(3000);
//        //设置动画结束之后是否保持动画的目标状态
//        aset.setFillAfter(true);
//        //设置动画结束之后是否保持动画开始时的状态
//        aset.setFillBefore(true);
//        //设置重复模式
//        aset.setRepeatMode(AnimationSet.REVERSE);
//        //aset
//        aset.setRepeatCount(AnimationSet.INFINITE);
//        //设置动画延时时间
//        aset.setStartOffset(2000);
//        //取消动画
////        aset.cancel();
//        //释放资源
////        aset.reset();
//
//
//        img_1.startAnimation(aset);


//        ObjectAnimator animator1=ObjectAnimator.ofFloat(img_1, "rotation", 0,360f);
//        ObjectAnimator animator2=ObjectAnimator.ofFloat(img_1, "translationX", 0,200f);
//        ObjectAnimator animator3=ObjectAnimator.ofFloat(img_1, "translationY", 0,200f);


//        float dy=100l;
//
//        ObjectAnimator animator1=ObjectAnimator.ofFloat(img_1, "translationY", 0,200f);
//        ObjectAnimator animator2=ObjectAnimator.ofFloat(img_1, "translationY", 0,0f);
//        ObjectAnimator animator3=ObjectAnimator.ofFloat(img_1, "translationY", 0,-200f);
//        ObjectAnimator animator4=ObjectAnimator.ofFloat(img_1, "translationY", 0,0f);
//
//        AnimatorSet set=new AnimatorSet();
//        //set.playTogether(animator1,animator2,animator3);
//        set.playSequentially(animator1,animator2,animator3,animator4);
//        set.setDuration(4000);
//        set.start();


//        ObjectAnimator animator1=ObjectAnimator.ofFloat(img_1, "translationY", -100f,100f);
//        ObjectAnimator animator2=ObjectAnimator.ofFloat(img_1, "translationY", 100f,-100f);
//
//        animator1.setRepeatCount(ValueAnimator.INFINITE);
//        animator1.setDuration(2000);
//
//        animator2.setRepeatCount(ValueAnimator.INFINITE);
//        animator2.setDuration(2000);
//
//        AnimatorSet handAnimSet = new AnimatorSet();
//        handAnimSet.setDuration(4000);
//
//
//        handAnimSet.play(animator1).after(animator2);
//
//        handAnimSet.start();

        Animation animation = shakeAnimation(5);
        img_1.startAnimation(animation);
        img_2.startAnimation(animation);
    }


    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 0, -15, 15);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(8000);
        translateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        return translateAnimation;
    }
}
