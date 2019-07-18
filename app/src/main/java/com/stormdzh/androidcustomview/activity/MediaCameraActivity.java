package com.stormdzh.androidcustomview.activity;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stormdzh.androidcustomview.R;
import com.stormdzh.androidcustomview.utils.mediacamera.FileUtils;
import com.stormdzh.androidcustomview.utils.mediacamera.helper.MediaHelper;
import com.stormdzh.androidcustomview.utils.mediacamera.helper.PermissionHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 描述
 * @Author: dzh
 * @CreateDate: 2019/7/17 10:38 AM
 */
public class MediaCameraActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 相机预览
     */
    private SurfaceView mSurfaceView;
    /**
     * 开始录制按钮
     */
    private ImageView mStartVideo;
    /**
     * 正在录制按钮，再次点击，停止录制
     */
    private ImageView mStartVideoIng;
    /**
     * 录制时间
     */
    private TextView mTime;
    /**
     * 录制进度条
     */
    private ProgressBar mProgress;
    /**
     * 等待视频合成完成提示
     */
    private ProgressBar mWait;
    /**
     * 录制主要工具类
     */
    private MediaHelper mMediaHelper;
    /**
     * 录制进度值
     */
    private int mProgressNumber = 0;
    /**
     * 视频段文件编号
     */
    private int mVideoNumber = 1;
    private FileUtils mFileUtils;
    /**
     * 临时记录每段视频的参数内容
     */
    private List<Mp4TsVideo> mTsVideo = new ArrayList<>();
    /**
     * mp4转ts流后的地址，主要合成的文件
     */
    private List<String> mTsPath = new ArrayList<>();
    /**
     * 是否已经取消下一步，比如关闭了页面，就不再做线程处理，结束任务
     */
    private boolean isCancel;
    /**
     * 权限相关
     */
    private PermissionHelper mPermissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams p = this.getWindow().getAttributes();
        p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;//|=：或等于，取其一
        getWindow().setAttributes(p);
        setContentView(R.layout.activity_mediacamera);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_surface_view);
        mStartVideo = (ImageView) findViewById(R.id.start_video);
        mStartVideoIng = (ImageView) findViewById(R.id.start_video_ing);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mTime = (TextView) findViewById(R.id.time);
        mWait = findViewById(R.id.wait);
        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.inversion).setOnClickListener(this);

        mStartVideo.setOnClickListener(this);
        mStartVideoIng.setOnClickListener(this);

        //录制之前删除所有的多余文件
        mFileUtils = new FileUtils(this);
        mFileUtils.deleteFile(mFileUtils.getMediaVideoPath(), null);
        mFileUtils.deleteFile(mFileUtils.getStorageDirectory(), null);


        mMediaHelper = new MediaHelper(this);
        mMediaHelper.setTargetDir(new File(mFileUtils.getMediaVideoPath()));
        //视频段从编号1开始
        mMediaHelper.setTargetName(mVideoNumber + ".mp4");
        mPermissionHelper = new PermissionHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                mMediaHelper.stopRecordUnSave();
                finish();
                break;
            case R.id.start_video:
                mProgressNumber = 0;
                mProgress.setProgress(0);
                mMediaHelper.record();
                startView();
                break;
            case R.id.start_video_ing:
                if (mProgressNumber == 0) {
                    stopView(false);
                    break;
                }
                Log.e("SLog", "mProgressNumber:" + mProgressNumber);
                if (mProgressNumber < 8) {
                    //时间太短不保存
                    Toast.makeText(this, "请至少录制到红线位置", Toast.LENGTH_LONG).show();
                    mMediaHelper.stopRecordUnSave();
                    stopView(false);
                    break;
                }
                //停止录制
                mMediaHelper.stopRecordSave();
                stopView(true);
                break;
            case R.id.inversion:
                if (mMediaHelper.isRecording()) {
                    mMediaHelper.stopRecordSave();
                    addMp4Video();
                    mVideoNumber++;
                    mMediaHelper.setTargetName(mVideoNumber + ".mp4");
                    mMediaHelper.autoChangeCamera();
                    mMediaHelper.record();
                } else {
                    mMediaHelper.autoChangeCamera();
                }
                break;
        }
    }

    /**
     * 记录这个视频片段并且开始处理。
     */
    private void addMp4Video() {
        Mp4TsVideo mp4TsVideo = new Mp4TsVideo();
        mp4TsVideo.setMp4Path(mMediaHelper.getTargetFilePath());
        mp4TsVideo.setTsPath(mFileUtils.getMediaVideoPath() + "/" + mVideoNumber + ".ts");
        mp4TsVideo.setFlip(mMediaHelper.getPosition() == Camera.CameraInfo.CAMERA_FACING_FRONT);
        mTsVideo.add(mp4TsVideo);
//        mp4ToTs();
    }


    private void startView() {
        mStartVideo.setVisibility(View.GONE);
        mStartVideoIng.setVisibility(View.VISIBLE);
        mProgressNumber = 0;
        mTime.setText("00:00");
        handler.removeMessages(0);
        handler.sendMessage(handler.obtainMessage(0));
    }

    /**
     * 停止录制
     *
     * @param isSave
     */
    private void stopView(boolean isSave) {
        int timer = mProgressNumber;
        mProgressNumber = 0;
        mProgress.setProgress(0);
        handler.removeMessages(0);
        mTime.setText("00:00");
        mTime.setTag(timer);
        if (isSave) {
            String videoPath = mFileUtils.getMediaVideoPath();
            final File file = new File(videoPath);
            if (!file.exists()) {
                Toast.makeText(this, "文件已损坏或者被删除，请重试！", Toast.LENGTH_SHORT).show();
                return;
            }
            File[] files = file.listFiles();
            if (files.length == 1) {
                startMediaVideo(mMediaHelper.getTargetFilePath());
            } else {
                showProgressLoading();
                addMp4Video();
            }
        } else {
            mFileUtils.deleteFile(mFileUtils.getStorageDirectory(), null);
            mFileUtils.deleteFile(mFileUtils.getMediaVideoPath(), null);
            mVideoNumber = 1;
            isCancel = true;
        }
        mStartVideoIng.setVisibility(View.GONE);
        mStartVideo.setVisibility(View.VISIBLE);
    }


    /**
     * 进入下一步制作页面
     *
     * @param path
     */
    private void startMediaVideo(String path) {
        int timer = (int) mTime.getTag();
//        Log.d("SLog", "video path:" + path);
//        Intent intent = new Intent(this, MakeVideoActivity.class);
//        intent.putExtra("path", path);
//        intent.putExtra("time", timer);
//        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSurfaceView == null) return;
        //启动相机
        mMediaHelper.setSurfaceView(mSurfaceView);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mProgress.setProgress(mProgressNumber);
                    mTime.setText("00:" + (mProgressNumber < 10 ? "0" + mProgressNumber : mProgressNumber));
                    if (mProgress.getProgress() >= mProgress.getMax()) {
                        mMediaHelper.stopRecordSave();
                        stopView(true);
                    } else if (mMediaHelper.isRecording()) {
                        mProgressNumber = mProgressNumber + 1;
                        sendMessageDelayed(handler.obtainMessage(0), 1000);
                    }
                    break;
            }
        }
    };

    private void showProgressLoading() {
        mWait.setVisibility(View.VISIBLE);
    }

    private void dismissProgress() {
        mWait.setVisibility(View.GONE);
    }

    /**
     * 记录下每段视频
     */
    private class Mp4TsVideo {
        /**
         * 视频段的地址
         */
        private String mp4Path;
        /**
         * ts地址
         */
        private String tsPath;
        /**
         * 是否需要翻转
         */
        private boolean flip;

        public String getMp4Path() {
            return mp4Path;
        }

        public void setMp4Path(String mp4Path) {
            this.mp4Path = mp4Path;
        }

        public String getTsPath() {
            return tsPath;
        }

        public void setTsPath(String tsPath) {
            this.tsPath = tsPath;
        }

        public boolean isFlip() {
            return flip;
        }

        public void setFlip(boolean flip) {
            this.flip = flip;
        }
    }
}
