package com.xp.media.textureview.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xp.media.R;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.util.LogUtils;
import com.xp.media.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

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

public class VideoMediaController extends FrameLayout {

    private static final String TAG = "VideoMediaController";
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.iv_replay)
    ImageView ivReplay;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.rl_play_finish)
    RelativeLayout rlPlayFinish;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.tv_all_time)
    TextView tvAllTime;
    @BindView(R.id.tv_use_time)
    TextView tvUseTime;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_fullscreen)
    ImageView ivFullscreen;
    @BindView(R.id.ll_play_control)
    LinearLayout llPlayControl;
    private boolean hasPause;//是否暂停

    private static final int MSG_UPDATE_TIME_PROGRESS = 1;
    private static final int MSG_HIDE_TITLE_CONTROLLER = 2;
    //消息处理器
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_TIME_PROGRESS:
                    updatePlayTimeAndProgress();
                    break;
                case MSG_HIDE_TITLE_CONTROLLER:
                    if (!isAnimation) {
                        showOrHideTitleControl();
                    }
                    break;
            }
        }
    };


    public VideoMediaController(Context context) {
        this(context, null);
    }

    public VideoMediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private VideoPlayer myVideoPlayer;

    public void setVideoPlayer(VideoPlayer myVideoPlayer) {
        this.myVideoPlayer = myVideoPlayer;
    }

    //初始化控件
    private void initView() {
        View view = View.inflate(getContext(), R.layout.textureview_video_controller, this);
        ButterKnife.bind(this, view);
        initViewDisplay();
        //设置视频播放时的点击界面
//        setOnTouchListener(onTouchListener);
        //设置SeekBar的拖动监听
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }


    //初始化控件的显示状态
    public void initViewDisplay() {
        tvTitle.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        ivPlay.setImageResource(R.drawable.new_play_video);
        tvAllTime.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llPlayControl.setVisibility(View.GONE);
        rlPlayFinish.setVisibility(View.GONE);
        tvUseTime.setText("00:00");
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);
    }

    //设置视频加载进度条的显示状态
    public void setPbLoadingVisiable(int visiable) {
        pbLoading.setVisibility(visiable);
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        //拖动的过程中调用
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtils.d("Test", "progress = " + progress);
        }

        //开始拖动的时候调用
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            LogUtils.d("Test", "onStartTrackingTouch");
            //暂停视频的播放、停止时间和进度条的更新
            MediaPlayerHelper.pause();
            mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
        }

        //停止拖动时调用
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtils.d("Test", "onStopTrackingTouch");
            //把视频跳转到对应的位置
            int progress = seekBar.getProgress();
            int duration = myVideoPlayer.mPlayer.getDuration();
            int position = duration * progress / 100;
            myVideoPlayer.mPlayer.seekTo(position);
            //开始播放、开始时间和进度条的更新
            MediaPlayerHelper.play();
            updatePlayTimeAndProgress();
        }
    };

    public void showOrHideTitleControl() {
        if (!isAnimation) {
            if (tvTitle.getVisibility() == VISIBLE && llPlayControl.getVisibility() == VISIBLE) {
                hideTitleAndControl();
            } else {
                showTitleAndControl();
            }
        }
    }

    private void showTitleAndControl() {
        ivPlay.setVisibility(VISIBLE);
        showTitleAnimation();
        showControlAnimation();
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_TITLE_CONTROLLER, 5000);
    }

    private void hideTitleAndControl() {
        ivPlay.setVisibility(GONE);
        hideTitleAnimation();
        hideControlAnimation();
        mHandler.removeMessages(MSG_HIDE_TITLE_CONTROLLER);
    }

    private boolean isAnimation;//是否处于动画状态

    private void showTitleAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.top_enter);
        animation.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                isAnimation = false;
                tvTitle.setVisibility(VISIBLE);
            }
        });
        tvTitle.startAnimation(animation);
    }

    public void hideTitleAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.top_exit);
        animation.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                tvTitle.setVisibility(GONE);
                isAnimation = false;
            }
        });
        tvTitle.startAnimation(animation);
    }

    private void showControlAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_enter);
        animation.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                llPlayControl.setVisibility(VISIBLE);
                isAnimation = false;
            }
        });
        llPlayControl.startAnimation(animation);
    }

    private void hideControlAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_exit);
        animation.setAnimationListener(new SimpleAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                llPlayControl.setVisibility(GONE);
                isAnimation = false;
            }
        });
        llPlayControl.startAnimation(animation);
    }

    //更新进度条的第二进度（缓存）
    public void updateSeekBarSecondProgress(int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    //设置播放视频的总时长
    public void setDuration(int duration) {
        String time = formatDuration(duration);
        tvTime.setText(time);
        tvUseTime.setText("00:00");
    }

    //格式化时间 00：00
    public String formatDuration(int duration) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(duration));
    }

    //更新播放的时间和进度
    public void updatePlayTimeAndProgress() {
        //获取目前播放的进度
        int currentPosition = MediaPlayerHelper.getInstance().getCurrentPosition();
        //格式化
        String useTime = formatDuration(currentPosition);
        tvUseTime.setText(useTime);
        //更新进度
        int duration = MediaPlayerHelper.getInstance().getDuration();
        if (duration == 0) {
            return;
        }
        int progress = 100 * currentPosition / duration;
        seekBar.setProgress(progress);
        //发送一个更新的延时消息
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_PROGRESS, 500);
    }

    //移除所有的消息
    public void removeAllMessage() {
        mHandler.removeCallbacksAndMessages(null);
    }

    //显示视频播放完成的界面
    public void showPlayFinishView() {
        showTitleAnimation();
        rlPlayFinish.setVisibility(View.VISIBLE);
        tvAllTime.setVisibility(View.VISIBLE);
    }

    //重新播放
    private void replay() {
        //隐藏播放完成界面
        rlPlayFinish.setVisibility(View.GONE);
        //隐藏时间
        tvAllTime.setVisibility(View.GONE);
        tvUseTime.setText("00:00");
        //进度条
        seekBar.setProgress(0);
        //把媒体播放器的位置移动到开始的位置
        MediaPlayerHelper.getInstance().seekTo(0);
        //开始播放
        MediaPlayerHelper.play();
        //延时隐藏标题
        hideTitleAnimation();
    }


    //简单的动画监听器（不需要其他的监听器去实现多余的方法）
    private class SimpleAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @OnClick({R.id.iv_replay, R.id.iv_share, R.id.iv_play, R.id.iv_fullscreen, R.id.ll_play_control})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_replay:
                replay();
                break;
            case R.id.iv_share:
                ToastUtil.showShortToast(getContext(), "分享链接");
                break;
            case R.id.iv_play:
                if (MediaPlayerHelper.getInstance().isPlaying()) {
                    //暂停
                    MediaPlayerHelper.pause();
                    //移除隐藏Controller布局的消息
                    mHandler.removeMessages(MSG_HIDE_TITLE_CONTROLLER);
                    //移除更新播放时间和进度的消息
                    mHandler.removeMessages(MSG_UPDATE_TIME_PROGRESS);
                    ivPlay.setImageResource(R.drawable.new_play_video);
                    hasPause = true;
                } else {
                    if (hasPause) {
                        //继续播放
                        MediaPlayerHelper.play();
                        mHandler.sendEmptyMessageDelayed(MSG_HIDE_TITLE_CONTROLLER, 2000);
                        updatePlayTimeAndProgress();
                        hasPause = false;
                    } else {
                        //播放
                        ivPlay.setVisibility(View.GONE);
                        tvAllTime.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.VISIBLE);
                        //视频播放界面也需要显示
                        myVideoPlayer.setVideoViewVisiable(View.VISIBLE);
                    }
                    ivPlay.setImageResource(R.drawable.new_pause_video);
                }
                break;
            case R.id.iv_fullscreen:
                ToastUtil.showShortToast(getContext(), "全屏播放");
                break;
            case R.id.ll_play_control:
                break;
        }
    }

}