package com.xp.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xp.media.activity.VideoViewActivity;
import com.xp.media.statusbar.StatusBarBaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @类描述：主页
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/20 0020 11:39
 * @修改人：
 * @修改时间：2018/3/20 0020 11:39
 * @修改备注：
 */

public class MainActivity extends StatusBarBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_videoview)
    public void clickVedioView() {
        startActivity(new Intent(this, VideoViewActivity.class));
    }
}
