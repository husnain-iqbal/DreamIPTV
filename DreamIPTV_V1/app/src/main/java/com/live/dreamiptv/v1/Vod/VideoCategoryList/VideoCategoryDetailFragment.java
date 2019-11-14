package com.live.dreamiptv.v1.Vod.VideoCategoryList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.live.dreamiptv.v1.Network.AppController;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;
import com.live.dreamiptv.v1.Vod.VideoList.VideoListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 3/28/2017.
 */

public class VideoCategoryDetailFragment extends Fragment implements VideoCategoryBaseAdapter.SendData {
    private Activity mActivity;
    private Context mContext;
    private ListView mListView;
    private boolean isSetAdapter;
    private String mVideoCategoriesUrl;
    private ArrayList<String> mCategoryIdList;
    private ArrayList<VideoCategoriesModel> mCategoryList;
    private VideoCategoryBaseAdapter mCategoryAdapter;

    private String getVideoCategoriesUrl() {
        return mVideoCategoriesUrl;
    }

    private void setVideoCategoriesUrl(String url) {
        mVideoCategoriesUrl = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSetAdapter = true;
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mCategoryList = new ArrayList<>();
        mCategoryIdList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_category_detail_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.listLiveCategories);
        mCategoryAdapter = new VideoCategoryBaseAdapter(this, mActivity, mCategoryList);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchVideoCategories(getVideoCategoriesUrl());
                mCategoryAdapter.setSelectedCategoryIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Utilities.setFocusedFragmentVodId(Utilities.FRAGMENT_ONE_TAG);
                }
            }
        });
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        String userName = pref.getString(Utilities.USERNAME_TEXT, "no value come");
        String passWord = pref.getString(Utilities.PASSWORD_TEXT, "no value come");
        String url = Utilities.URL + Utilities.USERNAME_TEXT + Utilities.EQUAL_SIGN + userName + Utilities.AND_SIGN + Utilities.PASSWORD_TEXT + Utilities.EQUAL_SIGN + passWord + Utilities.AND_SIGN + Utilities.URL_ACTION_VOD_CATEGORIES;
        setVideoCategoriesUrl(url);
        fetchVideoCategories(url);
        return view;
    }

    private void fetchVideoCategories(String url) {
        JsonArrayRequest jsonObject = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String categoryName = object.getString(Utilities.CATEGORY_NAME_TEXT);
                        final String categoryId = object.getString(Utilities.CATEGORY_ID_TEXT);
                        if (!mCategoryIdList.contains(categoryId)) {
                            mCategoryList.add(new VideoCategoriesModel(categoryName, categoryId));
                            mCategoryIdList.add(categoryId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (isSetAdapter) {
                    mListView.setAdapter(mCategoryAdapter);
                    isSetAdapter = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showShortToast(mContext, Utilities.ERROR_FAILED_TO_LOAD_CATEGORIES);
            }
        });
        AppController.getInstances().addToRequestQueue(jsonObject);
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    public void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    @Override
    public void sendCategoryId(String position) {
        VideoListFragment fragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.fragment_two_vod);
        if (fragment != null) {
            fragment.setVideoCategoryId(position);
            fragment.populateListView();
        }
    }

    @Override
    public void sendKeyRotate2VideoListFirstItemEvent() {
        VideoListFragment fragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.fragment_two_vod);
        if (fragment != null) {
            fragment.selectListViewFirstItem();
        }
    }

    @Override
    public void sendKeyRotate2CategoryListFirstItemEvent() {
        selectListViewFirstItem();
    }

    @Override
    public void sendKeyRotate2CategoryListLastItemEvent(int lastItemPosition) {
        selectListViewLastItem(lastItemPosition);
    }
}
