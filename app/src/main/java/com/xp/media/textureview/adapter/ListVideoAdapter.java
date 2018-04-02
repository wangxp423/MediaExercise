package com.xp.media.textureview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xp.media.R;
import com.xp.media.textureview.bean.VideoPlayerInfo;
import com.xp.media.textureview.view.VideoPlayer;

import java.util.List;

public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.VideoViewHolder> {
    private static final String TAG = "ListVideoAdapter";
    private Context mContext;
    private List<VideoPlayerInfo> mVideoList;

    public ListVideoAdapter(Context mContext, List<VideoPlayerInfo> mVideoList) {
        this.mContext = mContext;
        this.mVideoList = mVideoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.textureview_recycler_item_videoview, parent, false);
        VideoViewHolder holder = new VideoViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoPlayerInfo info = mVideoList.get(position);
        holder.bindData(info);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private VideoPlayer videoPlayer;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoPlayer = (VideoPlayer) itemView.findViewById(R.id.vp_item_texture);
        }

        public void bindData(VideoPlayerInfo info) {
            videoPlayer.setPlayData(info);
        }
    }

    public void destory() {
        if (mVideoList != null) {
            mVideoList.clear();
        }
    }
}
