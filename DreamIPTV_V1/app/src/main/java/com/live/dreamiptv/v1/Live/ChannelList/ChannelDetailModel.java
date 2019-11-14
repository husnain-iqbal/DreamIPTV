package com.live.dreamiptv.v1.Live.ChannelList;

/**
 * Created by hp on 3/16/2017.
 */

public class ChannelDetailModel {
    private String mStreamId;
    private String mStreamType;
    private String mChannelName;
    private String mCategoryId;

    public ChannelDetailModel(String streamId, String streamType, String channelName, String categoryId) {
        mStreamId = streamId;
        mStreamType = streamType;
        mChannelName = channelName;
        mCategoryId = categoryId;
    }

    public String getStreamId() {
        return mStreamId;
    }

    public String getStreamType() {
        return mStreamType;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setStreamId(String mStreamId) {
        this.mStreamId = mStreamId;
    }

    public void setStreamType(String mStreamType) {
        this.mStreamType = mStreamType;
    }

    public void setChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public void setCategoryId(String mCategoryId) {
        this.mCategoryId = mCategoryId;
    }
}
