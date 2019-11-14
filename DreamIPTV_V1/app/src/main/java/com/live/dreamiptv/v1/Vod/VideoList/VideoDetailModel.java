package com.live.dreamiptv.v1.Vod.VideoList;

/**
 * Created by hp on 3/16/2017.
 */

public class VideoDetailModel {

    private String mExtension;
    private String mStreamId;
    private String mStreamType;
    private String mVideoName;
    private String mVideoCategoryId;

    public VideoDetailModel(String streamId, String streamType, String channelName, String categoryId, String extension) {
        mExtension = extension;
        mStreamId = streamId;
        mStreamType = streamType;
        mVideoName = channelName;
        mVideoCategoryId = categoryId;
    }

    public String getStreamId() {
        return mStreamId;
    }

    public String getStreamType() {
        return mStreamType;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public String getVideoCategoryId() {
        return mVideoCategoryId;
    }

    public String getExtension() {
        return mExtension;
    }
    public void setStreamId(String streamId) {
        mStreamId = streamId;
    }

    public void setStreamType(String streamType) {
        mStreamType = streamType;
    }

    public void setVideoName(String channelName) {
        mVideoName = channelName;
    }

    public void setVideoCategoryId(String categoryId) {
        mVideoCategoryId = categoryId;
    }
}
