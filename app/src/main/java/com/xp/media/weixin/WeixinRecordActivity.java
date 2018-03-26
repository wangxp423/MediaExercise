package com.xp.media.weixin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.VideoView;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.util.LogUtils;
import com.xp.media.util.ScreenUtils;
import com.xp.media.util.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：仿微信拍照录段视频
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/23 0023 17:45
 * @修改人：
 * @修改时间：2018/3/23 0023 17:45
 * @修改备注：
 */

public class WeixinRecordActivity extends StatusBarBaseActivity implements CameraInterface.CameraOpenOverCallback {
    @BindView(R.id.vv_weixin_videoview)
    VideoView vvWeixinVideoview;
    @BindView(R.id.iv_weixin_control)
    ImageView ivWeixinControl;

    private float screenWidth, screenHeight;
    private float prop = 0f;
    private boolean isRecording;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_record);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
        if (prop == 0) {
            prop = screenHeight / screenWidth;
        }
        LogUtils.d("Test", "prop = " + prop);
        vvWeixinVideoview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CameraInterface.getInstance().doOpenCamera(WeixinRecordActivity.this);
                    }
                }).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                CameraInterface.getInstance().doDestroyCamera();
            }
        });
        //设置录像存储路径
        CameraInterface.getInstance().setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
    }

    @OnClick(R.id.iv_weixin_control)
    public void startRecord() {
        if (isRecording) {
            ToastUtil.showShortToast(this, "结束录制,存储路径为JCamera");
            isRecording = false;
            ivWeixinControl.setImageResource(R.drawable.recordvideo_control_start);
            CameraInterface.getInstance().stopRecord(false, new CameraInterface.StopRecordCallback() {
                @Override
                public void recordResult(String url, Bitmap firstFrame) {

                }
            });
        } else {
            ToastUtil.showShortToast(this, "开始录制");
            isRecording = true;
            ivWeixinControl.setImageResource(R.drawable.recordvideo_control_stop);
            CameraInterface.getInstance().startRecord(vvWeixinVideoview.getHolder().getSurface(), prop, null);
        }
    }

    @Override
    public void cameraHasOpened() {
        CameraInterface.getInstance().doStartPreview(vvWeixinVideoview.getHolder(), prop);
    }
}
