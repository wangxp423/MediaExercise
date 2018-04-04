package com.xp.media.textureview.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.Toast;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.textureview.view.VideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：小窗播放 退出小窗
 * @创建人：Wangxiaopan
 * @创建时间：2018/4/3 0003 15:15
 * @修改人：
 * @修改时间：2018/4/3 0003 15:15
 * @修改备注：
 */
public class FloatWindowActivity extends StatusBarBaseActivity {
    @BindView(R.id.texture_floatwindow_player)
    VideoPlayer textureVideoPlayer;
    @BindView(R.id.btn_floatwindow_cancel)
    Button btnConfigFloatWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floatwindow_activity_support_play);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //数据的初始化
        VideoPlayerInfo info = new VideoPlayerInfo();
        info.setId(0);
        String url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid_bigbuckbunny).toString();
        info.setVideoUrl(url);
        info.setVideoTitle("Android官方  MediaPlayer小视频");
        textureVideoPlayer.setPlayData(info);
    }

    @OnClick(R.id.btn_floatwindow_support)
    public void clickFloatWindow() {
        textureVideoPlayer.enterFloatWindow();
    }

    @OnClick(R.id.btn_floatwindow_cancel)
    public void clickFloatWindowCancle() {
        boolean isSupport = MediaPlayerHelper.getInstance().getSupportFloatWindow();
        if (isSupport) {
            btnConfigFloatWindow.setText("设置--默认不支持退出小窗");
            MediaPlayerHelper.getInstance().setSupportFloatWindow(false);
        } else {
            btnConfigFloatWindow.setText("设置--默认支持退出小窗");
            MediaPlayerHelper.getInstance().setSupportFloatWindow(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (MediaPlayerHelper.getInstance().onBackPressed()) return;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerHelper.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerHelper.getInstance().continues();
    }


    private static final int OVERLAY_PERMISSION_REQ_CODE = 0x001;

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //6.0以上需要动态获取悬浮窗权限
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OVERLAY_PERMISSION_REQ_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.canDrawOverlays(this)) {
                            Toast.makeText(FloatWindowActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                        } else {
                        }
                    }
                    break;
            }
        }
    }
}
