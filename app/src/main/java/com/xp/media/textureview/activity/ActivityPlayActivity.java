package com.xp.media.textureview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.view.VideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @类描述：在Activity中播放
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/28 0028 10:51
 * @修改人：
 * @修改时间：2018/3/28 0028 10:51
 * @修改备注：
 */

public class ActivityPlayActivity extends StatusBarBaseActivity {
    @BindView(R.id.iv_video_cover)
    ImageView ivVideoCover;
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
        String url = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";
        int id = 0;
        //数据的初始化
        VideoPlayerInfo info = new VideoPlayerInfo(id, url);
        textureVideoPlayer.setPlayData(info);
    }
}
