package com.xp.media.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.Toast;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.util.LogUtils;
import com.xp.media.widget.CustomVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：VideoView 页面
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/20 0020 14:27
 * @修改人：
 * @修改时间：2018/3/20 0020 14:27
 * @修改备注：
 */

public class VideoViewActivity extends StatusBarBaseActivity {
    @BindView(R.id.vv_videoview)
    CustomVideoView vvVideoview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //VideoView跟MediaPlayer很相似，其实是VideoView帮我们做了一层封装而已。背后仍然是MediaPlayer对视频文件进行控制的
        //另外VideoView并不是一个万能的播放器，在视频格式和播放效率上都存在严重不足，所以只适用于播放一些片头动画，视频宣传
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        //快进 进度条 暂停 播放 控制器
        MediaController controller = new MediaController(this);
//        controller.setVisibility(View.INVISIBLE);
        vvVideoview.setMediaController(controller);
        //播放完成监听
        vvVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VideoViewActivity.this, "OnCompletionListener", Toast.LENGTH_SHORT).show();
            }
        });
        //开始播放 loading隐藏
        vvVideoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Toast.makeText(VideoViewActivity.this, "OnPreparedListener", Toast.LENGTH_SHORT).show();
            }
        });
        //失败监听
        vvVideoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoViewActivity.this, "OnErrorListener", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //缓冲监听
        vvVideoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoViewActivity.this, "OnInfoListener", Toast.LENGTH_SHORT).show();
                LogUtils.d("Test", "what = " + what + "  extra = " + extra);
                return false;
            }
        });
    }

    @OnClick(R.id.btn_videoview_local)
    public void localStart() {
        vvVideoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid_bigbuckbunny));
        vvVideoview.start();
    }

    @OnClick(R.id.btn_videoview_net)
    public void netStart() {
        //http://qiubai-video.qiushibaike.com/YXSKWQA6N838MJC4_3g.mp4
        vvVideoview.setVideoURI(Uri.parse("http://qiubai-video.qiushibaike.com/YXSKWQA6N838MJC4_3g.mp4"));
        vvVideoview.start();
    }

//    int getCurrentPosition()：获取当前播放的位置。
//    int getDuration()：获取当前播放视频的总长度。
//    isPlaying()：当前VideoView是否在播放视频。
//    void pause()：暂停
//    void seekTo(int msec)：从第几毫秒开始播放。
//    void resume()：重新播放。
//    void setVideoPath(String path)：以文件路径的方式设置VideoView播放的视频源。
//    void setVideoURI(Uri uri)：以Uri的方式设置VideoView播放的视频源，可以是网络Uri或本地Uri。
//    void start()：开始播放。
//    void stopPlayback()：停止播放。并释放资源
//    setMediaController(MediaController controller)：设置MediaController控制器。
//    setOnCompletionListener(MediaPlayer.onCompletionListener l)：监听播放完成的事件。
//    setOnErrorListener(MediaPlayer.OnErrorListener l)：监听播放发生错误时候的事件。
//    setOnPreparedListener(MediaPlayer.OnPreparedListener l)：：监听视频装载完成的事件。
}
