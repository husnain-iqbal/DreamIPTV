package com.live.dreamiptv.v1.Vod.VideoList;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Vod.VodUtils;

import java.util.ArrayList;

/**
 * Created by hp on 3/20/2017.
 */

public class VideoListBaseAdapter extends BaseAdapter {
    private boolean isFirstItem;
    private boolean isLastItem;
    private View mCurrentListItem;
    private Activity mActivity;
    private Context mContext;
    private SendData mSendData;
    private LayoutInflater mLayoutInflater;
    private ArrayList<VideoDetailModel> mVideoList;

    public VideoListBaseAdapter(SendData sendData, Activity activity, ArrayList<VideoDetailModel> list) {
        isFirstItem = false;
        isLastItem = false;
        mVideoList = new ArrayList<>();
        mVideoList = list;
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mSendData = sendData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void refreshListView() {
        notifyDataSetChanged();
    }

    public View getCurrentListItem() {
        return mCurrentListItem;
    }

    private void setCurrentListItem(View view) {
        mCurrentListItem = view;
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        if (vi == null) {
            vi = mLayoutInflater.inflate(R.layout.channel_detail_row, parent, false);
            holder = new ViewHolder();
            holder.videosButton = (Button) vi.findViewById(R.id.channelId);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.videosButton.setFocusable(true);
        holder.videosButton.setFocusableInTouchMode(true);
        VideoDetailModel item = mVideoList.get(position);
        holder.videosButton.setText(item.getVideoName());
        holder.videosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentListItem(v);
                VideoDetailModel videoItem = mVideoList.get(position);
                mSendData.sendStreamInfo(videoItem.getStreamType(), videoItem.getStreamId(), videoItem.getExtension(), true);
                v.setSelected(true);
                v.setEnabled(true);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
//                notifyDataSetChanged();
            }
        });
        holder.videosButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setCurrentListItem(v); // cache the clicked item
                    checkIfFirstItem(position);
                    checkIfLastItem(position);
//                    v.setSelected(true);
//                    v.setEnabled(true);
//                    v.setFocusable(true);
//                    v.setFocusableInTouchMode(true);
//                    Utilities.setListItemBackgroundBlue(mActivity, v);
//                    v.requestFocus();
                }
//                else {
//                            v.setSelected(false);
//                    Utilities.setListItemBackgroundGray(mActivity, v);
//                }
            }
        });
        holder.videosButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) { //TODO: return true if listener has consumed the event
                int keyAction = event.getAction();
                if (keyAction == KeyEvent.ACTION_DOWN) {
                    if (VodUtils.isUserOnLargeScreen()) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return false;
                        }
                        return true;
                    } else {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && isLastItem) {
                            mSendData.sendKeyRotate2ChannelListFirstItemEvent();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstItem) {
                            mSendData.sendKeyRotate2ChannelListLastItemEvent(mVideoList.size() - 1);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        return vi;
    }

    private void checkIfLastItem(int itemPosition) {
        if (itemPosition + 1 == mVideoList.size()) {
            isLastItem = true;
        } else {
            isLastItem = false;
        }
    }

    private void checkIfFirstItem(int itemPosition) {
        if (itemPosition == 0) {
            isFirstItem = true;
        } else {
            isFirstItem = false;
        }
    }

    private static class ViewHolder {
        Button videosButton;
    }

    public interface SendData {
        void sendStreamInfo(String streamType, String streamId, String extension, boolean isVideoClicked);

        void sendKeyRotate2ChannelListFirstItemEvent();

        void sendKeyRotate2ChannelListLastItemEvent(int lastItemPosition);
    }
}
