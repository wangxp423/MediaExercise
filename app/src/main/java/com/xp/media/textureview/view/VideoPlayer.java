package com.xp.media.textureview.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.xp.media.R;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：播放页面
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 11:05
 * @修改人：
 * @修改时间：2018/3/28 0028 11:05
 * @修改备注：
 */

public class VideoPlayer extends FrameLayout {
    private static final String TAG = "VideoPlayer";
    @BindView(R.id.video_view)
    public TextureView videoView;
    @BindView(R.id.mediaController)
    public VideoMediaController mediaController;

    public MediaPlayer mPlayer;
    private Surface mSurface;

    public boolean hasPlay;//是否播放了

    public VideoPlayer(@NonNull Context context) {
        super(context);
        initView();
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //初始化布局
    private void initView() {
        View view = View.inflate(getContext(), R.layout.textureview_video_play, this);
        ButterKnife.bind(this, view);
        initViewDisplay();
        //把VideoPlayer对象传递给VideoMediaController
        mediaController.setVideoPlayer(this);
        //进行TextureView控件创建的监听
        videoView.setSurfaceTextureListener(surfaceTextureListener);
    }

    //初始化控件的显示状态
    public void initViewDisplay() {
        videoView.setVisibility(View.GONE);
        mediaController.initViewDisplay();
    }

    //设置视频播放界面的显示
    public void setVideoViewVisiable(int visible) {
        videoView.setVisibility(View.VISIBLE);
    }

    private VideoPlayerInfo info;

    public void setPlayData(VideoPlayerInfo info) {
        this.info = info;
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        //创建完成  TextureView才可以进行视频画面的显示
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //连接对象（MediaPlayer和TextureView）
            mSurface = new Surface(surface);
            play(info.url);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtils.d("Test", "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtils.d("Test", "onSurfaceTextureDestroyed");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtils.d("Test","onSurfaceTextureUpdated");
        }
    };

    //视频播放（视频的初始化）
    private void play(String url) {
        try {
            mPlayer = MediaPlayerHelper.getInstance();
            mPlayer.reset();
            mPlayer.setDataSource(url);
            //让MediaPlayer和TextureView进行视频画面的结合
            mPlayer.setSurface(mSurface);
            //设置监听
            mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnErrorListener(onErrorListener);
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setScreenOnWhilePlaying(true);//在视频播放的时候保持屏幕的高亮
            //异步准备
            mPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //准备完成监听
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtils.d("Test", "onPrepared");
            //隐藏视频加载进度条
            mediaController.setPbLoadingVisiable(View.GONE);
            //进行视频的播放
            MediaPlayerHelper.play();
            hasPlay = true;
            //隐藏标题
            mediaController.hideTitleAnimation();
            //设置视频的总时长
            mediaController.setDuration(mPlayer.getDuration());
            //更新播放的时间和进度
            mediaController.updatePlayTimeAndProgress();
        }
    };

    //错误监听
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    //完成监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtils.d("Test", "onCompletion");
            //视频播放完成
            mediaController.showPlayFinishView();
        }
    };

    //缓冲的监听
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            LogUtils.d("Test", "percent = " + percent);
            mediaController.updateSeekBarSecondProgress(percent);
        }
    };

    @OnClick(R.id.video_view)
    public void clickTextureView() {
        mediaController.showOrHideTitleControl();
    }

}
