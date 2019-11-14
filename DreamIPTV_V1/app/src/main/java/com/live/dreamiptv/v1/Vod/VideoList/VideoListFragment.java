package com.live.dreamiptv.v1.Vod.VideoList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.live.dreamiptv.v1.Network.AppController;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;
import com.live.dreamiptv.v1.Vod.VideoPlay.VideoPlayFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 3/28/2017.
 */

public class VideoListFragment extends Fragment implements VideoListBaseAdapter.SendData {
    private Activity mActivity;
    private Context mContext;
    private ListView mListView;
    private boolean isSetAdapter;
    private String mVideoCategoryId;
    private VideoListBaseAdapter mVideosAdapter;
    private ArrayList<String> mStreamIdList;
    private ArrayList<VideoDetailModel> mSingleCategoryList;
    private ArrayList<VideoDetailModel> mAllCategoriesList;

    public String getVideoCategoryId() {
        return mVideoCategoryId;
    }

    public void setVideoCategoryId(String videoCategoryId) {
        mVideoCategoryId = videoCategoryId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mAllCategoriesList = new ArrayList<>();
        mStreamIdList = new ArrayList<>();
        mSingleCategoryList = new ArrayList<>();
        isSetAdapter = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fetchVideosList();
        final View view = inflater.inflate(R.layout.channel_video_combine_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.listViewChannelVideo);
        mVideosAdapter = new VideoListBaseAdapter(this, mActivity, mSingleCategoryList);
        mListView.setItemsCanFocus(true);
        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Utilities.setFocusedFragmentVodId(Utilities.FRAGMENT_TWO_TAG);
                }
            }
        });
        return view;
    }

    public void fetchVideosList() {
        SharedPreferences prefGet = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        String userName = prefGet.getString(Utilities.USERNAME_TEXT, "no value come");
        String passWord = prefGet.getString(Utilities.PASSWORD_TEXT, "no value come");
        String liveStreamsUrl = Utilities.URL + Utilities.USERNAME_TEXT + Utilities.EQUAL_SIGN + userName + Utilities.AND_SIGN + Utilities.PASSWORD_TEXT + Utilities.EQUAL_SIGN + passWord + Utilities.AND_SIGN + Utilities.URL_ACTION_VOD_STREAMS;
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(liveStreamsUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String channelName = object.getString(Utilities.CHANNEL_NAME_TEXT);
                        String streamId = object.getString(Utilities.STREAM_ID_TEXT);
                        String streamType = object.getString(Utilities.STREAM_TYPE_TEXT);
                        String categoryId = object.getString(Utilities.CATEGORY_ID_TEXT);
                        String extension = object.getString(Utilities.LINK_EXTENSION_TEXT);
                        if (!mStreamIdList.contains(streamId)) {
                            mStreamIdList.add(streamId);
                            mAllCategoriesList.add(new VideoDetailModel(streamId, streamType, channelName, categoryId, extension));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mAllCategoriesList.isEmpty()) {
                    Utilities.showShortToast(mContext, Utilities.ERROR_FAILED_TO_LOAD_VIDEOS);
                }
            }
        });
        AppController.getInstances().addToRequestQueue(jsonObjectRequest);
    }

    public void populateListView() {
        fetchVideosList();
        if (!mSingleCategoryList.isEmpty()) {
            mSingleCategoryList.clear();
        }
        for (int i = 0; i < mAllCategoriesList.size(); i++) {
            VideoDetailModel videoItem = mAllCategoriesList.get(i);
            if (videoItem.getVideoCategoryId().equals(getVideoCategoryId())) {
                mSingleCategoryList.add(videoItem);
            }
        }
        if (isSetAdapter) {
            mListView.setAdapter(mVideosAdapter);
            if (!mSingleCategoryList.isEmpty()) {
                isSetAdapter = false;
            }
        } else {
            mVideosAdapter.refreshListView();
        }
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    public void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    public void setFocusOnCurrentItem() {
        View v = mVideosAdapter.getCurrentListItem();
        v.requestFocus();
    }

    @Override
    public void sendStreamInfo(String streamType, String streamId, String extension, boolean isVideoClicked) {
        VideoPlayFragment fragment = (VideoPlayFragment) getFragmentManager().findFragmentById(R.id.fragment_three_vod);
        if (fragment != null) {
            fragment.attainUrl(streamType, streamId, extension, isVideoClicked);
        }
    }

    @Override
    public void sendKeyRotate2ChannelListFirstItemEvent() {
        selectListViewFirstItem();
    }

    @Override
    public void sendKeyRotate2ChannelListLastItemEvent(int lastItemPosition) {
        selectListViewLastItem(lastItemPosition);
    }
}
