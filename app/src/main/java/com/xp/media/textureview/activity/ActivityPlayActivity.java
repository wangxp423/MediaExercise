package com.xp.media.textureview.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.textureview.view.VideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：在Activity中播放
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 10:51
 * @修改人：
 * @修改时间：2018/3/28 0028 10:51
 * @修改备注：
 */

public class ActivityPlayActivity extends StatusBarBaseActivity {
    @BindView(R.id.texture_video_player)
    VideoPlayer textureVideoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textureview_activity_activity_play);
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

    @OnClick(R.id.texture_video_window)
    public void clickLittleWindow() {
        textureVideoPlayer.enterTinyWindow();
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
}
