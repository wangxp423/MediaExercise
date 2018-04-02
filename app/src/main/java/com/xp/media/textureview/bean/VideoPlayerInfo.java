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
    public String videoUrl;
    public String imageUrl;
    public String videoTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
