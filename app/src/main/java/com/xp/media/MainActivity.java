package com.xp.media;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xp.media.activity.CustomRecordActivity;
import com.xp.media.activity.VideoViewActivity;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.util.FileUtil;
import com.xp.media.weixin.WeixinRecordActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * @类描述：主页
 * @创建人：Wangxiaopan
 * @创建时间：2018/3/20 0020 11:39
 * @修改人：
 * @修改时间：2018/3/20 0020 11:39
 * @修改备注：
 */

public class MainActivity extends StatusBarBaseActivity {
    public static final int RECORD_SYSTEM_VIDEO = 1;
    public static final int RECORD_CUSTOM_VIDEO = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermission();
    }

    @OnClick(R.id.btn_videoview)
    public void clickVedioView() {
        startActivity(new Intent(this, VideoViewActivity.class));
    }


    @OnClick(R.id.btn_system_record_view)
    public void clickSystemRecordView() {
        Uri fileUri = Uri.fromFile(new File(FileUtil.getRecordViewName(this)));
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);     //限制的录制时长 以秒为单位
//        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024);        //限制视频文件大小 以字节为单位
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);      //设置拍摄的质量0~1
//        intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, false);        // 全屏设置
        startActivityForResult(intent, RECORD_SYSTEM_VIDEO);
    }

    @OnClick(R.id.btn_custom_record_view)
    public void clickCustomRecordView() {
        startActivity(new Intent(this, CustomRecordActivity.class));
    }

    @OnClick(R.id.btn_weixin_view)
    public void clickWeixinView() {
        startActivity(new Intent(this, WeixinRecordActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == RECORD_SYSTEM_VIDEO) {
            if (null == data) return;
            Uri uri = data.getData();
            Intent intent = new Intent(MainActivity.this, VideoViewActivity.class);
            intent.putExtra(VideoViewActivity.EXTRA_URI, uri.toString());
            startActivity(intent);
            //跳转
        }
    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // `permission.name` is granted !
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            Toast.makeText(MainActivity.this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
