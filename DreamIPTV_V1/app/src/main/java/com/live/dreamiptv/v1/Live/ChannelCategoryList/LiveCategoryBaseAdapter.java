package com.live.dreamiptv.v1.Live.ChannelCategoryList;

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

public class LiveCategoryBaseAdapter extends BaseAdapter {
    private boolean isLastItem;
    private boolean isFirstItem;
    private Activity mActivity;
    private SendData mSendData;
    private int mSelectedCategoryIndex;
    private LayoutInflater mLayoutInflater;
    private ArrayList<LiveCategoriesModel> mLiveCategoryList;

    public LiveCategoryBaseAdapter(SendData categoryId, Activity activity, ArrayList<LiveCategoriesModel> categoryList) {
        isFirstItem = false;
        isLastItem = false;
        mSelectedCategoryIndex = -1;
        mActivity = activity;
        mSendData = categoryId;
        mLiveCategoryList = new ArrayList<>();
        mLiveCategoryList = categoryList;
        mLayoutInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    public void setSelectedCategoryIndex(int index) {
        mSelectedCategoryIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLiveCategoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLiveCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi = convertView;             //trying to reuse a recycled view
        final ViewHolder holder;
        if (vi == null) {
            //The view is not a recycled one: we have to inflate
            vi = mLayoutInflater.inflate(R.layout.live_category_row, parent, false);
            holder = new ViewHolder();
            holder.categoryButton = (Button) vi.findViewById(R.id.category_name);
            vi.setTag(holder);
        } else {
            // View recycled !
            // no need to inflate
            // no need to findViews by id
            holder = (ViewHolder) vi.getTag();
        }
        holder.categoryButton.setFocusable(true);
        holder.categoryButton.setFocusableInTouchMode(true);
        LiveCategoriesModel item = mLiveCategoryList.get(position);
        holder.categoryButton.setText(item.getCatgeName());
        holder.categoryButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String categoryId = mLiveCategoryList.get(position).getId();
                    mSendData.sendCategoryId(categoryId);
                    v.setSelected(true);
                    v.setFocusableInTouchMode(true);
                    v.setEnabled(true);
                    v.requestFocus();
                    notifyDataSetChanged();
                    checkIfFirstItem(position);
                    checkIfLastItem(position);
                }
            }
        });
        holder.categoryButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int keyAction = event.getAction();
                if (keyAction == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        mSendData.sendKeyRotate2ChannelListFirstItemEvent();
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && isLastItem) {
                        mSendData.sendKeyRotate2CategoryListFirstItemEvent();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && isFirstItem) {
                        mSendData.sendKeyRotate2CategoryListLastItemEvent(mLiveCategoryList.size() - 1);
                        return true;
                    }
                }
                return false;
            }
        });
        if (mSelectedCategoryIndex != -1 && position == mSelectedCategoryIndex) {
            Utilities.setListItemBackgroundBlue(mActivity, holder.categoryButton);
            holder.categoryButton.requestFocus();
        } else {
            Utilities.setListItemBackgroundGray(mActivity, holder.categoryButton);
        }
        return vi;
    }

    private void checkIfLastItem(int itemPosition) {
        if (itemPosition + 1 == mLiveCategoryList.size()) {
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
        Button categoryButton;
    }

    public interface SendData {
        void sendCategoryId(String position);

        void sendKeyRotate2ChannelListFirstItemEvent();

        void sendKeyRotate2CategoryListFirstItemEvent();

        void sendKeyRotate2CategoryListLastItemEvent(int lastItemPosition);
    }
}
