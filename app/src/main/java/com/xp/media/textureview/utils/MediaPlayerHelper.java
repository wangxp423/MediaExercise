package com.xp.media.textureview.utils;

import com.xp.media.textureview.view.VideoMediaController;
import com.xp.media.textureview.view.VideoPlayer;

/**
 * @类描述：MediaPlayer 工具类
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 11:46
 * @修改人：
 * @修改时间：2018/3/28 0028 11:46
 * @修改备注：
 */

public class MediaPlayerHelper {
    private static MediaPlayerHelper mHelper;
    private VideoPlayer mVideoPlayer;


    private MediaPlayerHelper() {
    }

    public static MediaPlayerHelper getInstance() {
        if (null == mHelper) {
            synchronized (MediaPlayerHelper.class) {
                if (null == mHelper) {
                    mHelper = new MediaPlayerHelper();
                }
            }
        }
        return mHelper;
    }

    public void setVideoPlayer(VideoPlayer player) {
        this.mVideoPlayer = player;
    }

    //暂停
    public void pause() {
        if (null != mVideoPlayer) {
            mVideoPlayer.mVideoMediaControl.mediaPause();
        }
    }

    //继续
    public void continues() {
        if (null != mVideoPlayer) {
            mVideoPlayer.mVideoMediaControl.mediaContinue();
        }
    }

    //释放
    public void release() {
        if (null != mVideoPlayer) {
            mVideoPlayer.videoPlayerRelease();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        if (null != mVideoPlayer) {
            int status = mVideoPlayer.mVideoMediaControl.getTextureWindowStatus();
            if (status == VideoMediaController.TEXTURE_WINDOW_FULLSCREEN) {
                mVideoPlayer.exitFullScreen();
                return true;
            } else if (status == VideoMediaController.TEXTURE_WINDOW_TINY) {
                mVideoPlayer.exitTinyWindow();
                return true;
            } else {
                release();
            }
        }
        return false;
    }
}
