package com.live.dreamiptv.v1.Vod.VideoCategoryList;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;

import java.util.ArrayList;

/**
 * Created by hp on 3/20/2017.
 */

public class VideoCategoryBaseAdapter extends BaseAdapter {

    private boolean isLastItem;
    private boolean isFirstItem;
    private Activity mActivity;
    private SendData mSendCategoryId;
    private int mSelectedCategoryIndex;
    private LayoutInflater mLayoutInflater;
    private ArrayList<VideoCategoriesModel> mVideoCategoryList = new ArrayList<>();

    public VideoCategoryBaseAdapter(SendData categoryId, Activity activity, ArrayList<VideoCategoriesModel> categoryList) {
        isFirstItem = false;
        isLastItem = false;
        mSelectedCategoryIndex = -1;
        mActivity = activity;
        mSendCategoryId = categoryId;
        mVideoCategoryList = categoryList;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    public void setSelectedCategoryIndex(int index) {
        mSelectedCategoryIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mVideoCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoCategoryList.get(position);
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
            vi = mLayoutInflater.inflate(R.layout.live_category_row, parent, false);
            holder = new ViewHolder();
            holder.videoCategoryButton = (Button) vi.findViewById(R.id.category_name);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.videoCategoryButton.setFocusable(true);
        holder.videoCategoryButton.setFocusableInTouchMode(true);
        VideoCategoriesModel item = mVideoCategoryList.get(position);
        holder.videoCategoryButton.setText(item.getCategoryName());
        holder.videoCategoryButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String categoryId = mVideoCategoryList.get(position).getCategoryId();
                    mSendCategoryId.sendCategoryId(categoryId);
                    v.setSelected(true);
                    v.setEnabled(true);
                    v.setFocusableInTouchMode(true);
                    v.requestFocus();
                    checkIfFirstItem(position);
                    checkIfLastItem(position);
                    notifyDataSetChanged();
                }
            }
        });
        holder.videoCategoryButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int keyAction = event.getAction();
                if (keyAction == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        mSendCategoryId.sendKeyRotate2VideoListFirstItemEvent();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && isLastItem) {
                        mSendCategoryId.sendKeyRotate2CategoryListFirstItemEvent();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstItem) {
                        mSendCategoryId.sendKeyRotate2CategoryListLastItemEvent(mVideoCategoryList.size() - 1);
                        return true;
                    }
                }
                return false;
            }
        });
        if (mSelectedCategoryIndex != -1 && position == mSelectedCategoryIndex) {
            Utilities.setListItemBackgroundBlue(mActivity, holder.videoCategoryButton);
            holder.videoCategoryButton.requestFocus();
        } else {
            Utilities.setListItemBackgroundGray(mActivity, holder.videoCategoryButton);
        }
        return vi;
    }

    private void checkIfLastItem(int itemPosition) {
        if (itemPosition + 1 == mVideoCategoryList.size()) {
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
        Button videoCategoryButton;
    }

    public interface SendData {
        void sendCategoryId(String position);

        void sendKeyRotate2VideoListFirstItemEvent();

        void sendKeyRotate2CategoryListFirstItemEvent();

        void sendKeyRotate2CategoryListLastItemEvent(int lastItemPosition);
    }
}
