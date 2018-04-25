package com.xp.media.ijkplayer;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;

import com.xp.media.R;
import com.xp.media.statusbar.StatusBarBaseActivity;
import com.xp.media.textureview.view.CompatTextureView;
import com.xp.media.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * @类描述：ijkplayer 最普通播放
 * @创建人：Wangxiaopan
 * @创建时间：2018/4/16 0016 17:49
 * @修改人：
 * @修改时间：2018/4/16 0016 17:49
 * @修改备注：
 */
public class NormalPlayActivity extends StatusBarBaseActivity {
    private CompatTextureView textureView;
    private IMediaPlayer mediaPlayer;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ijk_activity_normal_play);
        initMediaPlayer();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        mSurfaceTexture.release();
        mSurfaceTexture = null;
    }

    private void initView() {
//        IjkMediaPlayer.loadLibrariesOnce(null);
//        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        textureView = (CompatTextureView) findViewById(R.id.texturev_normal_play);
        textureView.setSurfaceTextureListener(surfaceTextureListener);

    }

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        /**
         * 创建完成  TextureView才可以进行视频画面的显示
         * 当mContainer移除重新添加后，mContainer及其内部的mTextureView和mController都会重绘，
         * mTextureView重绘后，会重新new一个SurfaceTexture，并重新回调onSurfaceTextureAvailable方法，
         * 这样mTextureView的数据通道SurfaceTexture发生了变化，但是mMediaPlayer还是持有原先的mSurfaceTexture，
         * 所以在切换全屏之前要保存之前的mSurfaceTexture，当切换到全屏后重新调用onSurfaceTextureAvailable时，
         * 将之前的mSurfaceTexture重新设置给mTextureView，这样就保证了切换时视频播放的无缝衔接
         *
         * @param surface
         * @param width
         * @param height
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtils.d("Test", "onSurfaceTextureAvailable");
            if (mSurfaceTexture == null) {
                mSurfaceTexture = surface;
                openMediaPlayer();
            } else {
                textureView.setSurfaceTexture(mSurfaceTexture);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            LogUtils.d("Test", "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            LogUtils.d("Test", "onSurfaceTextureDestroyed");
            return textureView == null;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//            LogUtils.d("Test","onSurfaceTextureUpdated");
        }
    };

    private void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new IjkMediaPlayer();
//            ((IjkMediaPlayer) mediaPlayer).setOption(1, "analyzemaxduration", 100L);
//            ((IjkMediaPlayer) mediaPlayer).setOption(1, "probesize", 10240L);
//            ((IjkMediaPlayer) mediaPlayer).setOption(1, "flush_packets", 1L);
//            ((IjkMediaPlayer) mediaPlayer).setOption(4, "packet-buffering", 0L);
//            ((IjkMediaPlayer) mediaPlayer).setOption(4, "framedrop", 1L);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置监听
            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.setOnInfoListener(mOnInfoListener);
            mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        }
    }

    //视频播放（视频的初始化）
    private void openMediaPlayer() {
        //raw下文件可以播放
        String url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid_bigbuckbunny).toString();
        //https 不可以播放,加了.so文件以后就可以播放了。
        String url1 = "https://mtenroll.oss-cn-hangzhou.aliyuncs.com/ueditor/video/20180131/6365302297303492635856363.mp4";
        //普通http 文件可以播放
        String url2 = "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4";
        //sd卡下的 mp4文件 可以播放
        String url3 = Environment.getExternalStorageDirectory().getPath().concat("/test-xiaoye.mp4");
        //sd卡下的 rm文件 不可以播放
        String url4 = Environment.getExternalStorageDirectory().getPath().concat("/njluyou.rm");
        //sd卡下的 rmvb文件 不可以播放 加了.so可以播放 但是画面只有声音
        String url5 = Environment.getExternalStorageDirectory().getPath().concat("/JCamera/qqfb.rmvb");
        //sd卡下的 mkv文件 可以播放
        String url6 = Environment.getExternalStorageDirectory().getPath().concat("/JCamera/xszr15.mkv");
        //不可以播放
        String url7 = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        //不可以播放
        String url8 = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
        //可以播放
        String url9 = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        String url10 = "http://mov.bn.netease.com/open-movie/nos/flv/2017/01/03/SC8U8K7BC_hd.flv";
        String urlTest = "rtmp://3891.liveplay.myqcloud.com/live/3891_testclock_rtmpacc?bizid=3891&txSecret=a1078d511863267194d17b2b642c9503&txTime=5AE18E86";
        Uri resourceUri = Uri.parse(url7);
        if (!TextUtils.isEmpty(resourceUri.getScheme()) && resourceUri.getScheme().equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getContentResolver().openAssetFileDescriptor(resourceUri, "r");
                IMediaDataSource dataSource = new RawDataSourceProvider(fileDescriptor);
                mediaPlayer.setDataSource(dataSource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mediaPlayer.setDataSource(this, resourceUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //让MediaPlayer和TextureView进行视频画面的结合
        mediaPlayer.setSurface(new Surface(mSurfaceTexture));
        //异步准备
        mediaPlayer.prepareAsync();
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener
            = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            LogUtils.d("Test", "onPrepared");
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener
            = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            textureView.adaptVideoSize(width, height);
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener
            = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener
            = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            LogUtils.d("Test", "onError ——> STATE_ERROR ———— what：" + what + ", extra: " + extra);
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener
            = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener
            = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        }
    };


    public class RawDataSourceProvider implements IMediaDataSource {
        private AssetFileDescriptor mDescriptor;

        private byte[] mMediaBytes;

        public RawDataSourceProvider(AssetFileDescriptor descriptor) {
            this.mDescriptor = descriptor;
        }

        @Override
        public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
            if (position + 1 >= mMediaBytes.length) {
                return -1;
            }

            int length;
            if (position + size < mMediaBytes.length) {
                length = size;
            } else {
                length = (int) (mMediaBytes.length - position);
                if (length > buffer.length)
                    length = buffer.length;

                length--;
            }
            System.arraycopy(mMediaBytes, (int) position, buffer, offset, length);

            return length;
        }

        @Override
        public long getSize() throws IOException {
            long length = mDescriptor.getLength();
            if (mMediaBytes == null) {
                InputStream inputStream = mDescriptor.createInputStream();
                mMediaBytes = readBytes(inputStream);
            }


            return length;
        }

        @Override
        public void close() throws IOException {
            if (mDescriptor != null)
                mDescriptor.close();

            mDescriptor = null;
            mMediaBytes = null;
        }

        private byte[] readBytes(InputStream inputStream) throws IOException {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        }
    }
}
