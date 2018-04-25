package com.xp.media.textureview.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
        //系统自带MediaPlayer能播放那些
        //raw下文件可以播放
        String url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid_bigbuckbunny).toString();
        //https 可以播放
        String url1 = "https://mtenroll.oss-cn-hangzhou.aliyuncs.com/ueditor/video/20180131/6365302297303492635856363.mp4";
        //普通http 文件可以播放
        String url2 = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";
        //sd卡下的 mp4文件 可以播放
        String url3 = Environment.getExternalStorageDirectory().getPath().concat("/test-xiaoye.mp4");
        //sd卡下的 rm文件 不可以播放
        String url4 = Environment.getExternalStorageDirectory().getPath().concat("/njluyou.rm");
        //sd卡下的 rmvb文件 不可以播放
        String url5 = Environment.getExternalStorageDirectory().getPath().concat("/JCamera/qqfb.rmvb");
        //sd卡下的 mkv文件 可以播放
        String url6 = Environment.getExternalStorageDirectory().getPath().concat("/JCamera/xszr15.mkv");
        //不可播放
        String url7 = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        //不可播放
        String url8 = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
        //可以播放
        String url9 = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
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
