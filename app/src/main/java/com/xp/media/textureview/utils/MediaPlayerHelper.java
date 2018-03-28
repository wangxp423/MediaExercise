package com.xp.media.textureview.utils;

import android.media.MediaPlayer;

/**
 * @类描述：MediaPlayer 工具类
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 11:46
 * @修改人：
 * @修改时间：2018/3/28 0028 11:46
 * @修改备注：
 */

public class MediaPlayerHelper {
    private static MediaPlayer mPlayer;

    private MediaPlayerHelper() {
    }

    public static MediaPlayer getInstance() {
        if (null == mPlayer) {
            synchronized (MediaPlayerHelper.class) {
                if (null == mPlayer) {
                    mPlayer = new MediaPlayer();
                }
            }
        }
        return mPlayer;
    }

    //播放
    public static void play() {
        if (mPlayer != null) {
            mPlayer.start();
        }
    }

    //暂停
    public static void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    //释放
    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
