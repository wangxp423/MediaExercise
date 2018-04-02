package com.xp.media.textureview.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xp.media.R;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.textureview.utils.MediaUtil;
import com.xp.media.util.LogUtils;

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
    public CompatTextureView videoView;
    public FrameLayout mContainer;
    public VideoMediaController mVideoMediaControl;

    private SurfaceTexture mSurfaceTexture;
    public MediaPlayer mPlayer;

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
        mContainer = (FrameLayout) view.findViewById(R.id.video_container);
        mVideoMediaControl = (VideoMediaController) view.findViewById(R.id.mediaController);
        mVideoMediaControl.initViewDisplay();
        mVideoMediaControl.setVideoPlayer(this);
    }

    private void initMediaPlayer() {
        if (null == mPlayer) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setScreenOnWhilePlaying(true);
            //设置监听
            mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setOnErrorListener(onErrorListener);
            mPlayer.setOnPreparedListener(onPreparedListener);
            mPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
            mPlayer.setOnInfoListener(infoListener);
        }
    }

    public void videoPlayerStart() {
        MediaPlayerHelper.getInstance().release();
        MediaPlayerHelper.getInstance().setVideoPlayer(this);
        initMediaPlayer();
        initTexture();
        addTexture();
        MediaPlayerHelper.getInstance().setVideoPlayer(this);
    }

    private void initTexture() {
        //进行TextureView控件创建的监听
        if (null == videoView) {
            videoView = new CompatTextureView(getContext());
            videoView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    private void addTexture() {
        mContainer.removeView(videoView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(videoView, 0, params);
    }

    private VideoPlayerInfo info;

    public void setPlayData(VideoPlayerInfo info) {
        this.info = info;
        mVideoMediaControl.setTitleAndConverImage();
    }

    public VideoPlayerInfo getPlayData() {
        return info;
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        /**
         * 创建完成  TextureView才可以进行视频画面的显示
         * 当mContainer移除重新添加后，mContainer及其内部的mTextureView和mController都会重绘，
         * mTextureView重绘后，会重新new一个SurfaceTexture，并重新回调onSurfaceTextureAvailable方法，
         * 这样mTextureView的数据通道SurfaceTexture发生了变化，但是mMediaPlayer还是持有原先的mSurfaceTexture，
         * 所以在切换全屏之前要保存之前的mSurfaceTexture，当切换到全屏后重新调用onSurfaceTextureAvailable时，
         * 将之前的mSurfaceTexture重新设置给mTextureView，这样就保证了切换时视频播放的无缝衔接
         *
         * @param surface
         * @param width
         * @param height
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtils.d("Test", "onSurfaceTextureAvailable");
            if (mSurfaceTexture == null) {
                mSurfaceTexture = surface;
                openMediaPlayer();
            } else {
                videoView.setSurfaceTexture(mSurfaceTexture);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtils.d("Test", "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtils.d("Test", "onSurfaceTextureDestroyed");
            return mSurfaceTexture == null;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtils.d("Test","onSurfaceTextureUpdated");
        }
    };

    //视频播放（视频的初始化）
    private void openMediaPlayer() {
        try {
//            mPlayer.setDataSource(info.getVideoUrl());
            mPlayer.setDataSource(getContext(), Uri.parse(info.getVideoUrl()));
            //让MediaPlayer和TextureView进行视频画面的结合
            mPlayer.setSurface(new Surface(mSurfaceTexture));
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
            mVideoMediaControl.mediaPreparedPlay();
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
            mVideoMediaControl.showPlayFinishView();
        }
    };

    //缓冲的监听
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            LogUtils.d("Test", "percent = " + percent);
            if (percent < mVideoMediaControl.mProgress) {
                mVideoMediaControl.setPbLoadingVisiable(View.VISIBLE);
            } else {
                mVideoMediaControl.setPbLoadingVisiable(View.GONE);
            }
            mVideoMediaControl.updateSeekBarSecondProgress(percent);
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            LogUtils.d("Test", "onVideoSizeChanged.width = " + width + "  height = " + height);
            videoView.adaptVideoSize(width, height);
        }
    };

    private MediaPlayer.OnInfoListener infoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };


    //进入全屏
    public void enterFullScreen() {
        mVideoMediaControl.setTextureWindowStatus(VideoMediaController.TEXTURE_WINDOW_FULLSCREEN);
        MediaUtil.hideActionBar(getContext());
        MediaUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView = (ViewGroup) MediaUtil.scanForActivity(getContext()).findViewById(android.R.id.content);
        this.removeView(mContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
    }

    //退出全屏
    public void exitFullScreen() {
        mVideoMediaControl.setTextureWindowStatus(VideoMediaController.TEXTURE_WINDOW_NORMAL);
        MediaUtil.showActionBar(getContext());
        MediaUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 从contentView中移除Container
        ViewGroup contentView = (ViewGroup) MediaUtil.scanForActivity(getContext()).findViewById(android.R.id.content);
        contentView.removeView(mContainer);
        // 将Container添加至NiceMediaPlayer这个FrameLayout
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    //小窗播放
    public void enterTinyWindow() {
        mVideoMediaControl.setTextureWindowStatus(VideoMediaController.TEXTURE_WINDOW_TINY);
        this.removeView(mContainer);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp
        ViewGroup contentView = (ViewGroup) MediaUtil.scanForActivity(getContext()).findViewById(android.R.id.content);
        LayoutParams params = new LayoutParams((int) (MediaUtil.getScreenWidth(getContext()) * 0.6f),
                (int) (MediaUtil.getScreenWidth(getContext()) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = MediaUtil.dp2px(getContext(), 8f);
        params.bottomMargin = MediaUtil.dp2px(getContext(), 8f);
        contentView.addView(mContainer, params);
    }

    //退出小窗播放
    public void exitTinyWindow() {
        mVideoMediaControl.setTextureWindowStatus(VideoMediaController.TEXTURE_WINDOW_NORMAL);
        ViewGroup contentView = (ViewGroup) MediaUtil.scanForActivity(getContext()).findViewById(android.R.id.content);
        contentView.removeView(mContainer);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    //小窗播放到全屏播放
    public void tinyWindowToFullScreen() {
        mVideoMediaControl.setTextureWindowStatus(VideoMediaController.TEXTURE_WINDOW_FULLSCREEN);
        MediaUtil.hideActionBar(getContext());
        MediaUtil.scanForActivity(getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView = (ViewGroup) MediaUtil.scanForActivity(getContext()).findViewById(android.R.id.content);
        contentView.removeView(mContainer);
        // 将Container添加至NiceMediaPlayer这个FrameLayout
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
    }

    public void videoPlayerRelease() {
        mContainer.removeView(videoView);
        if (null != mSurfaceTexture) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (null != mVideoMediaControl) {
            mVideoMediaControl.mediaReset();
        }
        if (null != mPlayer){
            mPlayer.release();
            mPlayer = null;
        }
    }

}
