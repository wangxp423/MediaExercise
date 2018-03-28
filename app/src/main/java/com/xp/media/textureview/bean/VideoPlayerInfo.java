package com.xp.media.textureview.bean;

import java.io.Serializable;

/**
 * @类描述：播放视频实体类
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 11:44
 * @修改人：
 * @修改时间：2018/3/28 0028 11:44
 * @修改备注：
 */

public class VideoPlayerInfo implements Serializable {
    public int id;
    public String url;

    public VideoPlayerInfo(int id, String url) {
        this.id = id;
        this.url = url;
    }
}
