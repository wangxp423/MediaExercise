package com.xp.media.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.VideoView;

import com.xp.media.R;
import com.xp.media.util.FileUtil;
import com.xp.media.util.LogUtils;
import com.xp.media.util.ScreenUtils;
import com.xp.media.util.ToastUtil;
import com.xp.media.util.VideoUtils;
import com.xp.media.weixin.util.CameraParamUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：自定义录屏
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/22 0022 10:45
 * @修改人：
 * @修改时间：2018/3/22 0022 10:45
 * @修改备注：
 */

public class CustomRecordActivity extends AppCompatActivity {
    @BindView(R.id.vv_custom_videoview)
    VideoView vvCustomVideoview;
    @BindView(R.id.iv_record_save)
    ImageView ivRecordSave;
    @BindView(R.id.iv_record_control)
    ImageView ivRecordControl;
    @BindView(R.id.iv_record_pause)
    ImageView ivRecordPause;
    @BindView(R.id.cn_record_time)
    Chronometer cnRecordTime;

    private SurfaceHolder mSurfaceHolder;
    private boolean isPause, isRecording;
    private long mPauseTime = 0;

    private Camera mCamera;
    private Camera.Parameters mParameters = null;
    private int CAMERA_FRONT_POSITION = -1; //前置摄像头
    private int CAMERA_POST_POSITION = -1; //后置摄像头
    private int SELETE_CAMERA = -1;//选择按个摄像头
    private MediaRecorder mediaRecorder;
    private String currentVideoFilePath, saveVideoPath;

    private float screenWidth, screenHeight;

    private float prop = 0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //三个问题
        //1 预览和录像的时候 拉伸的问题
        //2 摄像头旋转的问题
        //3 播放的时候录像拉伸的问题
        setContentView(R.layout.activity_custom_record);
        ButterKnife.bind(this);
        initView();
        findAvailableCameras();
    }

    @OnClick(R.id.iv_record_switch)
    public void clickSwitch() {
        if (SELETE_CAMERA == CAMERA_POST_POSITION) {
            SELETE_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELETE_CAMERA = CAMERA_POST_POSITION;
        }
        stopCamera();
        openCamera(SELETE_CAMERA);
//        mCamera = Camera.open();
        if (Build.VERSION.SDK_INT > 17 && this.mCamera != null) {
            try {
                this.mCamera.enableShutterSound(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startPreview();
    }

    @OnClick(R.id.iv_record_save)
    public void clickSave() {
        String extraPath;
        if (TextUtils.isEmpty(saveVideoPath)) {
            extraPath = currentVideoFilePath;
        } else {
            extraPath = saveVideoPath;
        }
        Intent intent = new Intent(CustomRecordActivity.this, VideoViewActivity.class);
        intent.putExtra(VideoViewActivity.EXTRA_URI, extraPath);
        startActivity(intent);
    }

    @OnClick(R.id.iv_record_pause)
    public void clickPause() {
        if (!isPause && !isRecording) {
            finish();
        } else if (isRecording) {
            isPause = true;
            ivRecordPause.setImageResource(R.drawable.recordvideo_play);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success == true) {
                        CustomRecordActivity.this.mCamera.cancelAutoFocus();
                    }
                }
            });
            stopRecord();
            saveAppendVideo();
            mPauseTime = SystemClock.elapsedRealtime() - cnRecordTime.getBase();
        } else {
            LogUtils.d("Test", "pauseTime = " + mPauseTime);
            isPause = false;
            ivRecordPause.setImageResource(R.drawable.recordvideo_stop);
            if (mPauseTime != 0) {
                cnRecordTime.setBase(SystemClock.elapsedRealtime() - mPauseTime);
            } else {
                cnRecordTime.setBase(SystemClock.elapsedRealtime());
            }
            cnRecordTime.start();
            //继续视频录制
            startRecord();
        }
    }

    @OnClick(R.id.iv_record_control)
    public void clickControl() {
        //如果左边按钮是暂停状态 再次点击 显示删除和保存按钮
        if (isPause || isRecording) {
            ivRecordPause.setImageResource(R.drawable.recordvideo_delete);
            ivRecordSave.setVisibility(View.VISIBLE);
            ivRecordControl.setVisibility(View.GONE);
            stopRecord();
            cnRecordTime.stop();
            mPauseTime = 0;
            saveAppendVideo();
        } else {
            ivRecordControl.setImageResource(R.drawable.recordvideo_control_stop);
            ivRecordPause.setImageResource(R.drawable.recordvideo_stop);
            startRecord();
        }
    }

    private void initView() {
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
        if (prop == 0) {
            prop = screenHeight / screenWidth;
        }
        LogUtils.d("Test", "prop = " + prop);
        //配置surfaceHolder
        mSurfaceHolder = vvCustomVideoview.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(callback);
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopCamera();
        }
    };

    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
        SELETE_CAMERA = CAMERA_POST_POSITION;
    }

    private synchronized void openCamera(int id) {
        try {
            this.mCamera = Camera.open(id);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > 17 && this.mCamera != null) {
            try {
                this.mCamera.enableShutterSound(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CJT", "enable shutter sound faild");
            }
        }
    }

    private void startPreview() {
        //默认启动后置摄像头
        openCamera(SELETE_CAMERA);
        if (null == mCamera) {
            ToastUtil.showLongToast(this, "未能获取到相机！");
            return;
        }
        if (mCamera != null) {
            try {
                mParameters = mCamera.getParameters();
                Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParameters
                        .getSupportedPreviewSizes(), 1000, prop);
                //用来 处理图片的
                Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParameters
                        .getSupportedPictureSizes(), 1200, prop);

                mParameters.setPreviewSize(previewSize.width, previewSize.height);

                mParameters.setPictureSize(pictureSize.width, pictureSize.height);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(
                        mParameters.getSupportedFocusModes(),
                        Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParameters.getSupportedPictureFormats(),
                        ImageFormat.JPEG)) {
                    mParameters.setPictureFormat(ImageFormat.JPEG);
                    mParameters.setJpegQuality(100);
                }
                mCamera.setParameters(mParameters);
//                mParameters = mCamera.getParameters();
                mCamera.setPreviewDisplay(mSurfaceHolder);  //SurfaceView
                mCamera.setDisplayOrientation(getPreviewDegree(this));//浏览角度
//                mCamera.setPreviewCallback(this); //每一帧回调
                mCamera.startPreview();//启动浏览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //开始录制
    private void startRecord() {
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        if (mParameters == null) {
            mParameters = mCamera.getParameters();
        }
        List<String> focusModes = mParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(mParameters);
        mCamera.unlock();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(OnErrorListener);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
//        mediaRecorder.setAudioChannels(2);
        //设置最大录像时间 单位：毫秒
//        mediaRecorder.setMaxDuration(60 * 1000);
        //设置最大录制的大小 单位，字节
//        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024) {
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        } else {
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        }
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);
        LogUtils.d("Test", "frameRate = " + mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(getPreviewDegree(this));
        Camera.Size videoSize;
        if (mParameters.getSupportedVideoSizes() == null) {
            videoSize = CameraParamUtil.getInstance().getPreviewSize(mParameters.getSupportedPreviewSizes(), 600,
                    prop);
        } else {
            videoSize = CameraParamUtil.getInstance().getPreviewSize(mParameters.getSupportedVideoSizes(), 600,
                    prop);
        }
        if (videoSize.width == videoSize.height) {
            mediaRecorder.setVideoSize(mParameters.getPictureSize().width, mParameters.getPictureSize().height);
        } else {
            mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        }

        //这里暂时这么处理 前置摄像头拍摄问题(不然拍摄以后播放会翻转)
        if (SELETE_CAMERA == CAMERA_FRONT_POSITION) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }

        //设置录像视频保存地址
        currentVideoFilePath = FileUtil.getRecordViewName(this);
        mediaRecorder.setOutputFile(currentVideoFilePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRecording = true;
        if (mPauseTime != 0) {
            cnRecordTime.setBase(SystemClock.elapsedRealtime() - mPauseTime);
        } else {
            cnRecordTime.setBase(SystemClock.elapsedRealtime());
        }
        cnRecordTime.start();
    }

    private void stopRecord() {
        if (isRecording && mediaRecorder != null) {
            // 设置后不会崩
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(null);
            //停止录制
            mediaRecorder.stop();
            mediaRecorder.reset();
            //释放资源
            mediaRecorder.release();
            mediaRecorder = null;

            cnRecordTime.stop();
            isRecording = false;
        }
        stopCamera();
    }

    private void saveAppendVideo() {
        if (TextUtils.isEmpty(saveVideoPath)) {
            saveVideoPath = currentVideoFilePath;
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] str = new String[]{saveVideoPath, currentVideoFilePath};
                    VideoUtils.appendVideo(CustomRecordActivity.this, FileUtil.getMediaPath(getApplicationContext()) + "append.mp4", str);
                    File reName = new File(saveVideoPath);
                    File f = new File(FileUtil.getMediaPath(getApplicationContext()) + "append.mp4");
                    f.renameTo(reName);
                    if (reName.exists()) {
                        f.delete();
                        new File(currentVideoFilePath).delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private MediaRecorder.OnErrorListener OnErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public static int getPreviewDegree(Activity activity) {
        int degree = 0;
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }


}
