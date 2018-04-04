package com.xp.media.textureview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.activity.ActivityPlayActivity;
import com.xp.media.textureview.activity.FloatWindowActivity;
import com.xp.media.textureview.activity.ListVideoActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：TextureView 播放视频 主页面
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/27 0027 11:19
 * @修改人：
 * @修改时间：2018/3/27 0027 11:19
 * @修改备注：
 */

public class TextureViewMainActivity extends StatusBarBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textureview_activity_main_function);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_activity_play)
    public void clickActivityPlay() {
        startActivity(new Intent(this, ActivityPlayActivity.class));
    }

    @OnClick(R.id.btn_recycler_play)
    public void clickRecyclerViewPlay() {
        startActivity(new Intent(this, ListVideoActivity.class));
    }

    @OnClick(R.id.btn_window_play)
    public void clickWindowPlay() {
        startActivity(new Intent(this, FloatWindowActivity.class));
    }
}
