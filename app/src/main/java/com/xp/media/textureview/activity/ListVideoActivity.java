package com.xp.media.textureview.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.adapter.ListVideoAdapter;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.utils.MediaPlayerHelper;
import com.xp.media.textureview.view.VideoPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @类描述：列表播放
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/30 0030 18:22
 * @修改人：
 * @修改时间：2018/3/30 0030 18:22
 * @修改备注：
 */

public class ListVideoActivity extends StatusBarBaseActivity {
    @BindView(R.id.rv_texture_list_video)
    RecyclerView rvTextureListVideo;

    private ListVideoAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textureview_activity_list_video);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        rvTextureListVideo.setLayoutManager(new LinearLayoutManager(this));
        rvTextureListVideo.setHasFixedSize(true);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.divide_line_white));
        rvTextureListVideo.addItemDecoration(divider);
        adapter = new ListVideoAdapter(this, getVideoList());
        rvTextureListVideo.setAdapter(adapter);
        rvTextureListVideo.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                VideoPlayer player = (VideoPlayer) view.findViewById(R.id.vp_item_texture);
                if (player != null) {
                    player.videoPlayerRelease();
                }
            }
        });
    }

    private List<VideoPlayerInfo> getVideoList() {
        List<VideoPlayerInfo> videoPlayerInfos = new ArrayList<>();
        VideoPlayerInfo info0 = new VideoPlayerInfo();
        info0.setId(0);
        String url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid_bigbuckbunny).toString();
        info0.setVideoUrl(url);
        info0.setVideoTitle("Android官方  MediaPlayer小视频");
        videoPlayerInfos.add(info0);

        VideoPlayerInfo info1 = new VideoPlayerInfo();
        info1.setId(1);
        info1.setVideoUrl("http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4");
        info1.setVideoTitle("北大美女学霸为音乐放弃保研 成金曲奖最大黑马");
        videoPlayerInfos.add(info1);

        VideoPlayerInfo info2 = new VideoPlayerInfo();
        info2.setId(2);
        info2.setVideoTitle("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？");
        info2.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg");
        info2.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4");
        videoPlayerInfos.add(info2);

        VideoPlayerInfo info3 = new VideoPlayerInfo();
        info3.setId(3);
        info3.setVideoTitle("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》");
        info3.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg");
        info3.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4");
        videoPlayerInfos.add(info3);

        VideoPlayerInfo info4 = new VideoPlayerInfo();
        info4.setId(4);
        info4.setVideoTitle("糗事百科 搞笑小视频");
        info4.setVideoUrl("http://qiubai-video.qiushibaike.com/YXSKWQA6N838MJC4_3g.mp4");
        videoPlayerInfos.add(info4);

        VideoPlayerInfo info5 = new VideoPlayerInfo();
        info5.setId(5);
        info5.setVideoTitle("针织方便面，这可能是史上最不方便的方便面");
        info5.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg");
        info5.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4");
        videoPlayerInfos.add(info5);

        VideoPlayerInfo info6 = new VideoPlayerInfo();
        info6.setId(6);
        info6.setVideoTitle("宵夜的下午茶，办公室不只有KPI，也有诗和远方");
        info6.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg");
        info6.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4.mp4");
        videoPlayerInfos.add(info6);

        VideoPlayerInfo info7 = new VideoPlayerInfo();
        info7.setId(7);
        info7.setVideoTitle("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家");
        info7.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg");
        info7.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4");
        videoPlayerInfos.add(info7);

        VideoPlayerInfo info8 = new VideoPlayerInfo();
        info8.setId(8);
        info8.setVideoTitle("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候");
        info8.setImageUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg");
        info8.setVideoUrl("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4");
        videoPlayerInfos.add(info8);

        return videoPlayerInfos;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != adapter) {
            adapter.destory();
            adapter = null;
        }
    }
}
