package com.stormdzh.androidcustomview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.stormdzh.androidcustomview.R;
import com.stormdzh.androidcustomview.widget.SurfaceBubbleView;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2019/7/18 9:54 AM
 */
public class SurfaceViewBubbleActivity extends Activity {

    private SurfaceBubbleView mSurfaceBubbleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_surfaceview_bubble);


        mSurfaceBubbleView = findViewById(R.id.mSurfaceBubbleView);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(mSurfaceBubbleView!=null){
//            mSurfaceBubbleView.resume();
//        }
    }

    @Override
    protected void onPause() {
        if(mSurfaceBubbleView!=null){
            mSurfaceBubbleView.pause();
        }
        super.onPause();
    }
}
